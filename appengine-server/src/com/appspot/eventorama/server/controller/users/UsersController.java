package com.appspot.eventorama.server.controller.users;

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
import org.slim3.util.BeanUtil;

import com.appspot.eventorama.server.meta.ApplicationMeta;
import com.appspot.eventorama.server.meta.UserMeta;
import com.appspot.eventorama.server.util.UserHelper;
import com.appspot.eventorama.shared.model.Application;
import com.appspot.eventorama.shared.model.User;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.repackaged.com.google.common.base.StringUtil;

public class UsersController extends Controller {

    private static final Logger log = Logger.getLogger(UsersController.class.getName());

    
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
            return asString("user_id") == null ? getUsers(app) : getUser(app);
        }
        else if ("post".equalsIgnoreCase(request.getMethod()))
        {
            return createUser(app);
        }
        else if ("put".equalsIgnoreCase(request.getMethod()))
        {
            return updateUser(app);
        }
        else
        {
            log.warning(String.format("Unsupported request method '%s'", request.getMethod()));
            response.setStatus(405);    // Method Not Allowed
        }
            
        
        return null;
    }


    private Navigation getUsers(Application app) throws Exception {
        log.info("app=" + KeyFactory.keyToString(app.getKey()));

        UserMeta userMeta = UserMeta.get();
        List<User> users = Datastore.query(userMeta)
            .filter(userMeta.applicationRef.equal(app.getKey()))
            .asList();

        log.info("Sending users JSON payload: " + userMeta.modelsToJson(users.toArray(new User[0])));

        response.setHeader("content-type", "application/json; charset=utf-8");
        OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream());
        writer.write(UserHelper.usersToJsonArray(users).toString());
        writer.flush();

        return null;
    }

    
    private Navigation getUser(Application app) throws Exception {
        log.info("app=" + KeyFactory.keyToString(app.getKey()));

        UserMeta userMeta = UserMeta.get();
        User user = Datastore.query(userMeta)
            .filter(userMeta.key.equal(Datastore.createKey(userMeta, asLong("user_id"))),
                    userMeta.applicationRef.equal(app.getKey()))
            .asSingle();

        if (user == null) {
            log.warning(String.format("User with id '%s' for app '%s' not found.", asString("user_id"), KeyFactory.keyToString(app.getKey())));
            response.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
            return null;
        }
            
        log.info("Sending user JSON payload: " + userMeta.modelToJson(user));

        response.setHeader("content-type", "application/json; charset=utf-8");
        OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream());
        writer.write(UserHelper.userToJsonObject(user).toString());
        writer.flush();

        return null;
    }


    private Navigation createUser(Application app) throws Exception {
        log.info("app=" + KeyFactory.keyToString(app.getKey()));

        Validators v = new Validators(request);
        v.add("name", v.required());
        v.add("deviceId", v.required());

        JSONObject json;
        try {
            json = new JSONObject(new JSONTokener(request.getReader()));
            requestScope("name", json.getString("name"));
            requestScope("deviceId", json.getString("device-id"));
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

        UserMeta userMeta = UserMeta.get();
        if (Datastore.query(userMeta)
                .filter(userMeta.name.equal(asString("name")),
                        userMeta.applicationRef.equal(app.getKey()))
                .count() != 0) {
            log.warning(String.format("User name '%s' for app '%s' is already taken.", asString("name"), KeyFactory.keyToString(app.getKey())));
            response.setStatus(HttpURLConnection.HTTP_CONFLICT);      // Unprocessable Entity, user name already taken
            return null;
        }
            
        log.info(String.format("Creating new user '%s' for app '%s', device-id=%s", asString("name"), KeyFactory.keyToString(app.getKey()), asString("deviceId")));
        
        User user = new User();
        BeanUtil.copy(request, user);
        user.getApplicationRef().setModel(app);

        Datastore.put(user);
        
        response.setStatus(HttpURLConnection.HTTP_CREATED);
        response.setHeader("location", UserHelper.getLocationHeaderForUser(user));
        return null;
    }


    private Navigation updateUser(Application app) {
        log.info("app=" + KeyFactory.keyToString(app.getKey()));
        requestScope("user_id", asString("user_id"));
        
        Validators v = new Validators(request);
        v.add("lon", v.required());
        v.add("lat", v.required());
        v.add("accuracy", v.required());
        v.add("user_id", v.required());

        try {
            JSONObject json = new JSONObject(new JSONTokener(request.getReader()));
            requestScope("lon", json.getDouble("lon"));
            requestScope("lat", json.getDouble("lat"));
            requestScope("accuracy", json.getDouble("accuracy"));
            requestScope("location_update", json.optString("location-update"));
        }
        catch (Exception e)
        {
            log.warning("Cannot parse JSON payload: " + e.getMessage());
            response.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
            return null;
        }
        
        if (! v.validate()) {
            Errors errors = v.getErrors();
            log.warning(String.format("Got an invalid set of input parameters for PUT call: app_id=%s, user_id=%s (%s), lon=%s, lat=%s, location_updated=%s, accuracy=%s", 
                asString("app_id"), asString("user_id"), errors.get("user_id"), asString("lon"), asString("lat"), asString("location_update"), asString("accuracy")));
            response.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
            return null;
        }
        
        UserMeta userMeta = UserMeta.get();
        User user = Datastore.query(userMeta)
            .filter(userMeta.key.equal(Datastore.createKey(userMeta, asLong("user_id"))),
                    userMeta.applicationRef.equal(app.getKey()))
            .asSingle();

        if (user == null) {
            log.warning(String.format("User with id '%s' for app '%s' not found.", asString("user_id"), KeyFactory.keyToString(app.getKey())));
            response.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
            return null;
        }

        if (StringUtil.isEmpty(asString("location_update")))
            user.setLocationUpdated(new Date(System.currentTimeMillis()));
        else
            user.setLocationUpdated(new Date(asLong("location_update")));
        user.setLocation(new GeoPt(asFloat("lat"), asFloat("lon")));
        user.setAccuracy(asFloat("accuracy"));
        
        Datastore.put(user);

        return null;
    }


}
