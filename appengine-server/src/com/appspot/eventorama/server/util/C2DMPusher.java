package com.appspot.eventorama.server.util;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletContext;

import org.slim3.datastore.Datastore;

import com.appspot.eventorama.server.meta.UserMeta;
import com.appspot.eventorama.shared.model.Application;
import com.appspot.eventorama.shared.model.User;
import com.google.android.c2dm.server.C2DMessaging;

public class C2DMPusher {

    private static final Logger log = Logger.getLogger(C2DMPusher.class.getName());
    
    
    public static final String C2DM_ACTION = "action";
    public static final String C2DM_ACTIVITIES_SYNC = "refreshActivities";
    public static final String C2DM_USERS_SYNC = "refreshUsers";


    public static void enqueueDeviceMessage(ServletContext context, Application app, User sender, String action) {
        UserMeta userMeta = UserMeta.get();
        List<User> users = Datastore.query(userMeta)
            .filter(userMeta.applicationRef.equal(app.getKey()))
            .asList();

        
        int numDeviceMessages = 0;
        for (User user : users) {
            if (user.getRegistrationId() == null || user.getRegistrationId().equals(sender.getRegistrationId()))
                continue;
            
            String collapseKey = C2DM_ACTION + ":" + action;
        
            try {
                C2DMessaging.get(context).sendWithRetry(
                    user.getRegistrationId(),
                    collapseKey,
                    C2DM_ACTION,
                    action,
                    null, null, null, null);
                
                ++numDeviceMessages;
            } catch (IOException ex) {
                log.severe("Can't send C2DM message '" + action + "' to " + user.getRegistrationId() + ", next manual sync will get the changes.");
            }
        }

        log.info("Scheduled " + numDeviceMessages + " C2DM device messages of type '" + action + "' from user " +
                sender.getName() + ".");

    }

}
