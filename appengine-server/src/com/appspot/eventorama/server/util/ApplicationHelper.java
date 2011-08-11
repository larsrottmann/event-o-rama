package com.appspot.eventorama.server.util;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.appspot.eventorama.shared.model.Application;

public class ApplicationHelper {

    public static JSONObject applicationToJsonObject(Application app) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("id", app.getKey().getId());
        values.put("creator", app.getUser().getEmail());
        values.put("title", app.getTitle());
        values.put("start-date", app.getStartDate().getTime());
        values.put("expiration-date", app.getExpirationDate().getTime());
        values.put("active", app.isActive());
        
        return new JSONObject(values);
    }

}
