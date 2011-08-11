package com.appspot.eventorama.server.controller.avatar;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.logging.Logger;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.controller.validator.Errors;
import org.slim3.controller.validator.Validators;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.EntityNotFoundRuntimeException;
import org.slim3.util.AppEngineUtil;

import com.appspot.eventorama.server.meta.ApplicationMeta;
import com.appspot.eventorama.server.meta.AvatarMeta;
import com.appspot.eventorama.server.meta.UserMeta;
import com.appspot.eventorama.server.util.C2DMPusher;
import com.appspot.eventorama.server.util.UserHelper;
import com.appspot.eventorama.shared.model.Application;
import com.appspot.eventorama.shared.model.Avatar;
import com.appspot.eventorama.shared.model.User;
import com.google.appengine.api.datastore.KeyFactory;

public class AvatarController extends Controller {

    private static final Logger log = Logger.getLogger(AvatarController.class.getName());
    
    
    @Override
    public Navigation run() throws Exception {
        Validators v = new Validators(request);
        v.add("app_id", v.required());
        v.add("user_id", v.required());

        if (! v.validate()) {
            Errors errors = v.getErrors();
            log.warning(String.format("Got an invalid set of input parameters: app_id=%s (%s), user_id=%s (%s)", asString("app_id"), errors.get("app_id"), asString("user_id"), errors.get("user_id")));
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

        
        if ("get".equalsIgnoreCase(request.getMethod()))
        {
            return getUserAvatar(app, user);
        }
        else if ("post".equalsIgnoreCase(request.getMethod()))
        {
            return createUserAvatar(app, user);
        }
        else
        {
            log.warning(String.format("Unsupported request method '%s'", request.getMethod()));
            response.setStatus(405);    // Method Not Allowed
        }
            
        
        return null;
    }
    
    
    private Navigation getUserAvatar(Application app, User user) throws Exception {
        log.info(String.format ("Sending avatar for user %s, app=%s", user.getKey().getId(), KeyFactory.keyToString(app.getKey())));

        AvatarMeta avatarMeta = AvatarMeta.get();
        Avatar avatar = Datastore.query(avatarMeta)
            .filter(avatarMeta.userRef.equal(user.getKey()))
            .asSingle();
   
        log.info("Sending avatar binary data for avatar: " + avatar.getKey().getId());

        response.setHeader("content-type", avatar.getMimeType());
        response.setContentLength(avatar.getBytes().length);
        
        OutputStream out = response.getOutputStream();
        out.write(avatar.getBytes());
        out.flush();

        return null;
    }


    private Navigation createUserAvatar(Application app, User user) throws Exception {
        log.info(String.format ("Creating avatar for user %s, app=%s", user.getKey().getId(), KeyFactory.keyToString(app.getKey())));

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        InputStream in = request.getInputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        while (-1 != (n = in.read(buffer)))
            bout.write(buffer, 0, n);
        bout.flush();
        
        Avatar avatar = new Avatar();
        avatar.getUserRef().setModel(user);
        avatar.setMimeType(request.getContentType());
        avatar.setBytes(bout.toByteArray());
        
        Datastore.put(avatar);
        
        log.info("Wrote avatar: " + avatar.toString());
        
        response.setStatus(HttpURLConnection.HTTP_CREATED);
        response.setHeader("location", UserHelper.getLocationHeaderForUser(user) + "/avatar");
        
        if (AppEngineUtil.isServer())
            C2DMPusher.enqueueDeviceMessage(servletContext, app, user, C2DMPusher.C2DM_USERS_SYNC);

        return null;
    }

}
