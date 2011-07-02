package com.appspot.eventorama.server.controller.activities;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.controller.validator.Errors;
import org.slim3.controller.validator.Validators;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.EntityNotFoundRuntimeException;
import org.slim3.datastore.ModelQuery;
import org.slim3.util.BeanUtil;

import com.appspot.eventorama.server.meta.ActivityMeta;
import com.appspot.eventorama.server.meta.ApplicationMeta;
import com.appspot.eventorama.server.meta.UserMeta;
import com.appspot.eventorama.server.util.ActivityHelper;
import com.appspot.eventorama.shared.model.Activity;
import com.appspot.eventorama.shared.model.Application;
import com.appspot.eventorama.shared.model.User;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Link;

public class ActivitiesController extends Controller {

    private static final Logger log = Logger.getLogger(ActivitiesController.class.getName());
        
        
    @Override
    public Navigation run() throws Exception {
        Validators v = new Validators(request);
        v.add("app_id", v.required());

        if (! v.validate()) {
            Errors errors = v.getErrors();
            log.warning(String.format("Got an invalid set of input parameters: app_id=%s (%s)", asString("app_id"), errors.get("app_id")));
            response.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
            return null;
        }

        Application app = null;
        try
        {
            app = Datastore.get(ApplicationMeta.get(), asKey("app_id"));
        }
        catch (EntityNotFoundRuntimeException e)
        {
            log.warning("App not found: " + asString("app_id"));
            response.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
            return null;
        }
        catch (Exception e)
        {
            log.warning(String.format("Not a valid app id: app_id=%s", asString("app_id")));
            response.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
            return null;
        }

        
        if ("get".equalsIgnoreCase(request.getMethod()))
        {
            return asString("activity_id") == null ? getActivities(app) : getActivity(app);
        }
        else if ("post".equalsIgnoreCase(request.getMethod()))
        {
            return createActivity(app);
        }
        else
        {
            log.warning(String.format("Unsupported request method '%s'", request.getMethod()));
            response.setStatus(405);    // Method Not Allowed
        }

        return null;
    }


    private Navigation getActivities(Application app) throws Exception {
        log.info("app=" + KeyFactory.keyToString(app.getKey()));

        ActivityMeta activityMeta = ActivityMeta.get();
        ModelQuery<Activity> modelQuery = Datastore.query(activityMeta)
            .filter(activityMeta.applicationRef.equal(app.getKey()));
        if (requestScope("since") != null)
        {
            modelQuery = modelQuery.filter(activityMeta.timestamp.greaterThanOrEqual(new Date(asLong("since"))));
        }
        List<Activity> activities = modelQuery.asList();

        log.info("Sending activities JSON payload: " + activityMeta.modelsToJson(activities.toArray(new Activity[0])));

        response.setHeader("content-type", "application/json; charset=utf-8");
        OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream());
        writer.write(ActivityHelper.activitiesToJsonArray(activities).toString());
        writer.flush();

        return null;
    }


    private Navigation getActivity(Application app) throws Exception {
        log.info("app=" + KeyFactory.keyToString(app.getKey()));

        ActivityMeta activityMeta = ActivityMeta.get();
        Activity activity = Datastore.query(activityMeta)
            .filter(activityMeta.key.equal(Datastore.createKey(activityMeta, asLong("activity_id"))),
                    activityMeta.applicationRef.equal(app.getKey()))
            .asSingle();

        if (activity == null) {
            log.warning(String.format("Activity with id '%s' for app '%s' not found.", asString("activity_id"), KeyFactory.keyToString(app.getKey())));
            response.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
            return null;
        }
            
        log.info("Sending activity JSON payload: " + activityMeta.modelToJson(activity));

        response.setHeader("content-type", "application/json; charset=utf-8");
        OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream());
        writer.write(ActivityHelper.activityToJsonObject(activity).toString());
        writer.flush();

        return null;
    }

    
    private Navigation createActivity(Application app) throws Exception {
        log.info("app=" + KeyFactory.keyToString(app.getKey()));

        Validators v = new Validators(request);
        v.add("userId", v.required());
        v.add("type", v.required());
        v.add("text", v.required());
        v.add("timestamp", v.required());

        JSONObject json;
        try {
            json = new JSONObject(new JSONTokener(request.getReader()));
            requestScope("userId", json.getString("user-id"));
            requestScope("type", json.getInt("type"));
            requestScope("text", json.getString("text"));
            requestScope("timestamp", new Date(json.getLong("timestamp")));
            requestScope("photoUrl", json.optString("photo-uri", null));
        }
        catch (Exception e)
        {
            log.warning("Cannot parse JSON payload: " + e.getMessage());
            response.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
            return null;
        }
        
        if (! v.validate()) {
            log.warning(String.format("app=%s, could not parse JSON: %s", KeyFactory.keyToString(app.getKey()), json.toString()));
            response.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
            return null;
        }

        if (!(app.getStartDate().before((Date) requestScope("timestamp")) && app.getExpirationDate().after((Date) requestScope("timestamp"))) || !app.isActive()) {
            log.warning(String.format("app=%s, activity is outside app lifetime or app is inactive: active=%s, start=%s, end=%s, json=%s", KeyFactory.keyToString(app.getKey()), app.isActive(), app.getStartDate().getTime(), app.getExpirationDate().getTime(), json.toString()));
            response.setStatus(HttpURLConnection.HTTP_NOT_ACCEPTABLE);
            return null;
        }
        
        UserMeta userMeta = UserMeta.get();
        User user = Datastore.query(userMeta)
            .filter(userMeta.key.equal(Datastore.createKey(userMeta, asLong("userId"))),
                    userMeta.applicationRef.equal(app.getKey()))
            .asSingle();

        if (user == null) {
            log.warning(String.format("User with id '%s' for app '%s' not found.", asString("userId"), KeyFactory.keyToString(app.getKey())));
            response.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
            return null;
        }

        Activity activity = new Activity();
        BeanUtil.copy(request, activity);
        activity.getApplicationRef().setModel(app);
        activity.getUserRef().setModel(user);
        if (requestScope("photoUrl") != null)
            activity.setPhotoUrl(new Link((String) requestScope("photoUrl")));

        Datastore.put(activity);
        
        response.setStatus(HttpURLConnection.HTTP_CREATED);
        response.setHeader("location", ActivityHelper.getLocationHeaderForActivity(activity));
        return null;
    }


}
