package com.appspot.eventorama.server.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletContext;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slim3.datastore.Datastore;

import com.appspot.eventorama.server.meta.UserMeta;
import com.appspot.eventorama.shared.model.Activity;
import com.appspot.eventorama.shared.model.Application;
import com.appspot.eventorama.shared.model.User;
import com.google.android.c2dm.server.C2DMessaging;
import com.google.appengine.api.datastore.KeyFactory;

public class ActivityHelper {

    private static final Logger log = Logger.getLogger(ActivityHelper.class.getName());
    
    
    public static final String C2DM_ACTION = "action";
    public static final String C2DM_ACTIVITIES_SYNC = "refreshActivities";


    public static JSONObject activityToJsonObject(Activity activity)
    {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("id", activity.getKey().getId());
        values.put("user-id", activity.getUserRef().getKey().getId());
        values.put("type", activity.getType());
        values.put("text", activity.getText());
        if (activity.getPhotoUrl() != null)
            values.put("photo-uri", activity.getPhotoUrl().getValue());
        values.put("timestamp", activity.getTimestamp().getTime());
        
        return new JSONObject(values);
    }

    public static JSONArray activitiesToJsonArray(List<Activity> activities) {
        JSONArray jsonArray = new JSONArray();
        for (Activity activity : activities) {
            jsonArray.put(activityToJsonObject(activity));
        }
        
        return jsonArray;
    }

    public static String getLocationHeaderForActivity(Activity activity) {
        StringBuilder url = new StringBuilder();
        
        url.append(GAEHelper.getGaeHostName());
        url.append("/app/");
        url.append(KeyFactory.keyToString(activity.getApplicationRef().getKey()));
        url.append("/activities/");
        url.append(activity.getKey().getId());
        
        return url.toString();
    }
    
    public static void enqueueDeviceMessage(ServletContext context, Application app, User sender) {
        UserMeta userMeta = UserMeta.get();
        List<User> users = Datastore.query(userMeta)
            .filter(userMeta.applicationRef.equal(app.getKey()))
            .asList();

        
        int numDeviceMessages = 0;
        for (User user : users) {
            if (user.getRegistrationId() == null || user.getRegistrationId().equals(sender.getRegistrationId()))
                continue;
            
            ++numDeviceMessages;
        
            String collapseKey = C2DM_ACTION + ":" + C2DM_ACTIVITIES_SYNC;
        
            try {
                C2DMessaging.get(context).sendWithRetry(
                    user.getRegistrationId(),
                    collapseKey,
                    C2DM_ACTION,
                    C2DM_ACTIVITIES_SYNC,
                    null, null, null, null);
            } catch (IOException ex) {
                log.severe("Can't send C2DM message to " + user.getRegistrationId() + ", next manual sync will get the changes.");
            }
        }

        log.info("Scheduled " + numDeviceMessages + " C2DM device messages for user " +
                sender.getName() + ".");

    }
    
}
