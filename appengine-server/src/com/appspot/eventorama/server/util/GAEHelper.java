package com.appspot.eventorama.server.util;

import com.google.appengine.api.utils.SystemProperty;

public class GAEHelper {

    
    public static String getGaeHostName()
    {
        if (SystemProperty.environment.value() ==
            SystemProperty.Environment.Value.Development) {
            // The app is not running on App Engine...
            return "http://localhost:8888";
        }
        else {
            return "http://event-o-rama.appspot.com";
        }
    }
    
}
