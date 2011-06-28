package com.eventorama.mobi.lib;

import android.app.Application;

public class EventORamaApplication extends Application {
	
	private static final String SERVER_URL = "http://10.0.2.2:8080/";

	public static final String PREFS_NAME = "eventorama-prefs";

	public static final String PREFS_USERNAME = "username";
	public static final String PREFS_DEVICE_ID = "device_id";
	public static final String PREFS_DEVICE_ID_SAVED = "device_id_saved";
	
	public String getServerUrl(String method)
	{
		StringBuilder sb = new StringBuilder(SERVER_URL);
		sb.append(method);
		return sb.toString();
	}

}
