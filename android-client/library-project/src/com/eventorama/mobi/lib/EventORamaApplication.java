package com.eventorama.mobi.lib;

import android.app.Application;

public class EventORamaApplication extends Application {
	
	private static final String SERVER_URL = "http://localhost:8080/";
	
	
	public String getServerUrl(String method)
	{
		StringBuilder sb = new StringBuilder(SERVER_URL);
		sb.append(method);
		return sb.toString();
	}

}
