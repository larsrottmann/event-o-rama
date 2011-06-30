package com.appspot.eventorama.server.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.appspot.eventorama.shared.model.User;
import com.google.appengine.api.datastore.KeyFactory;

public class UserHelper {

    public static JSONObject userToJsonObject(User user)
    {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("id", user.getKey().getId());
        values.put("name", user.getName());
        values.put("device-id", user.getDeviceId());
        if (user.getLocationUpdated() != null)
            values.put("location-update", user.getLocationUpdated().getTime());
        if (user.getLocation() != null) {
            values.put("lon", user.getLocation().getLongitude());
            values.put("lat", user.getLocation().getLatitude());
        }
        
        return new JSONObject(values);
    }

    public static JSONArray usersToJsonArray(List<User> users) {
        JSONArray jsonArray = new JSONArray();
        for (User user : users) {
            jsonArray.put(userToJsonObject(user));
        }
        
        return jsonArray;
    }

    public static String getLocationHeaderForUser(User user) {
        StringBuilder url = new StringBuilder();
        
        url.append(GAEHelper.getGaeHostName());
        url.append("/app/");
        url.append(KeyFactory.keyToString(user.getApplicationRef().getKey()));
        url.append("/users/");
        url.append(user.getKey().getId());
        
        return url.toString();
    }
}
