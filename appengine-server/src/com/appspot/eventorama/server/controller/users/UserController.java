package com.appspot.eventorama.server.controller.users;

import java.net.HttpURLConnection;
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
import com.appspot.eventorama.shared.model.Application;
import com.appspot.eventorama.shared.model.User;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.utils.SystemProperty;

public class UserController extends Controller {

    private static final Logger log = Logger.getLogger(UserController.class.getName());

    
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
            v.add("user_id", v.required());
            
            if (! v.validate()) {
                Errors errors = v.getErrors();
                log.warning(String.format("Got an invalid set of input parameters for PUT call: app_id=%s, user_id=%s (%s)", asString("app_id"), asString("user_id"), errors.get("user_id")));
                response.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
                return null;
            }
            
            return modifyUser(app);
        }
        else
        {
            log.warning(String.format("Unsupported request method '%s'", request.getMethod()));
            response.setStatus(405);    // Method Not Allowed
        }
            
        
        return null;
    }


    private Navigation getUsers(Application app) {
        log.info("getUsers(): app=" + KeyFactory.keyToString(app.getKey()));

        return null;
    }

    
    private Navigation getUser(Application app) {
        log.info("getUser(): app=" + KeyFactory.keyToString(app.getKey()));

        return null;
    }


    private Navigation createUser(Application app) throws Exception {
        log.info("createUser(): app=" + KeyFactory.keyToString(app.getKey()));

        try {
            JSONObject json = new JSONObject(new JSONTokener(request.getReader()));
            requestScope("name", json.get("name"));
            requestScope("deviceId", json.get("device-id"));
            
            Validators v = new Validators(request);
            v.add("name", v.required());
            v.add("deviceId", v.required());

            if (! v.validate()) {
                log.warning(String.format("createUser(): app=%s, could not parse JSON: %s", KeyFactory.keyToString(app.getKey()),  request.getReader().toString()));
                response.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
                return null;
            }
        }
        catch (Exception e)
        {
            log.warning("Cannot parse JSON payload: " + e.getMessage());
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
        response.setHeader("location", getLocationHeaderForUser(user));
        return null;
    }


    private Navigation modifyUser(Application app) {
        log.info("modifyUser(): app=" + KeyFactory.keyToString(app.getKey()));

        return null;
    }


    private String getLocationHeaderForUser(User user)
    {
        StringBuilder url = new StringBuilder();
        
        if (SystemProperty.environment.value() ==
            SystemProperty.Environment.Value.Development) {
            // The app is not running on App Engine...
            url.append("http://localhost:8888");
        }
        else {
            url.append("http://event-o-rama.appspot.com");
        }
            
        url.append("/app/");
        url.append(KeyFactory.keyToString(user.getApplicationRef().getKey()));
        url.append("/users/");
        url.append(user.getKey().getId());
        
        return url.toString();
    }
}
