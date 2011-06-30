package com.appspot.eventorama.server.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.appspot.eventorama.shared.model.Activity;
import com.google.appengine.api.datastore.KeyFactory;

public class ActivityHelper {

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
    
}
