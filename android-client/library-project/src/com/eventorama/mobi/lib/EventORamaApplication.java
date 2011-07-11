package com.eventorama.mobi.lib;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;
import org.acra.ReportingInteractionMode;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;

import com.eventorama.mobi.lib.data.HTTPResponse;
import com.eventorama.mobi.lib.location.GingerBreadLocationFinder;
import com.eventorama.mobi.lib.location.LastLocationFinder;
import com.eventorama.mobi.lib.location.LegacyLastLocationFinder;

import android.app.Application;
import android.content.Context;
import android.util.Log;

@ReportsCrashes(formKey = "dFA3dE52U3hXTUU3OFRfajNGRDFfNHc6MQ",
mode = ReportingInteractionMode.TOAST,
resToastText=R.string.application_crash_toast)
public class EventORamaApplication extends Application {

	/*
	 * PUBLIC CONSTANTS
	 */

	public static final String PREFS_PREFERENCES_NAME = "eventorama-prefs";

	public static final String PREFS_USERNAME = "username";
	public static final String PREFS_DEVICE_ID = "device_id";
	public static final String PREFS_USERID = "user_id";
	public static final String PREFS_DEVICE_ID_SAVED = "device_id_saved";

	public static final int HTTP_METHOD_GET = 1;
	public static final int HTTP_METHOD_POST = 2;
	public static final int HTTP_METHOD_PUT = 3;


	public static boolean SUPPORTS_GINGERBREAD = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD;
	//	  public static boolean SUPPORTS_HONEYCOMB = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB;
	public static boolean SUPPORTS_FROYO = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO;
	public static boolean SUPPORTS_ECLAIR = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ECLAIR;


	/*
	 * Private stuff
	 */
	private static final int DEFAULT_BUFFER = 30720; //30k

	private static final String TAG = EventORamaApplication.class.getName();

	//	private static final String SERVER_URL = "http://10.0.2.2:8080/";
	private static final String SERVER_URL = "http://event-o-rama.appspot.com/";

	private static final String TEST_PACKAGE = "ag5zfmV2ZW50LW8tcmFtYXISCxILQXBwbGljYXRpb24YmwgM";

	private static final Integer CONNECTION_TIMEOUT = 15000;  // 15 sec
	private static final Integer SOCKET_TIMEOUT = 10000;  // 10 sec

	private static final String CHARSET = "UTF-8";
	//HttpClient for all requests
	private HttpClient httpclient = null;


	@Override
	public void onCreate() {
		// The following line triggers the initialization of ACRA
		ACRA.init(this);
		super.onCreate();
	}

	private String getServerUrl(String method)
	{
		StringBuilder sb = new StringBuilder(SERVER_URL);
		sb.append("app/").append(TEST_PACKAGE);
		sb.append(method);
		return sb.toString();
	}

	/**
	 * Use to initiate a HTTP Request to the appengine server
	 * @param method the remote method like "/users"
	 * @param body	the body you would like to POST
	 * @param http_method	the method you'd like to use, see {@link EventORamaApplication}HTTP_METHOD_* constants
	 * @return a {@link HTTPResponse} object
	 */
	public HTTPResponse doHttpRequest(String method, String body, int http_method)
	{
		synchronized (this) {
			if(this.httpclient == null)
				this.httpclient = initHttpClient();
		}
		try 
		{
			HttpUriRequest httpMessage = null;
			switch (http_method) {
			case HTTP_METHOD_POST:
				HttpPost post = new HttpPost(getServerUrl(method));
				if(body != null)
				{
					StringEntity postBody = new StringEntity(body, CHARSET);
					post.setEntity(postBody);
				}
				httpMessage = post;				
				break;
			case HTTP_METHOD_PUT:
				HttpPut put = new HttpPut(getServerUrl(method));
				if(body != null)
				{
					StringEntity putBody = new StringEntity(body, CHARSET);
					put.setEntity(putBody);
				}
				httpMessage = put;
				break;

			default:
				HttpGet get = new HttpGet(getServerUrl(method));
				httpMessage = get;
				break;
			}

			Log.v(TAG, "Executing http request to: "+httpMessage.getURI());			

			long now = System.currentTimeMillis();
			HttpResponse response = this.httpclient.execute(httpMessage);
			Log.v(TAG, "HTTP request finished in: "+(System.currentTimeMillis()-now)+" ms");

			HttpEntity responseEntity = response.getEntity();

			BufferedReader in = new BufferedReader(new InputStreamReader(responseEntity.getContent(), Charset.forName("UTF-8")), DEFAULT_BUFFER);
			String inputLine;
			StringBuilder sb = new StringBuilder();
			while ((inputLine = in.readLine()) != null)
			{
				sb.append(inputLine);
			}
			int respCode = response.getStatusLine().getStatusCode();			

			Log.v(TAG, "response body: "+sb.toString()+" code: "+respCode);

			return  new HTTPResponse(respCode, sb.toString(), response.getAllHeaders());
		}
		catch(Exception e)
		{
			//TODO: re-throw exception might make sense
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Use to get a suitable LastLocationFinder
	 * 
	 * @param context
	 * @return A LastLocationFinder Implementation
	 */
	public LastLocationFinder getLastLocationFinder(Context context) {
		return SUPPORTS_GINGERBREAD ? new GingerBreadLocationFinder(context) : new LegacyLastLocationFinder(context);
	}

	private HttpClient initHttpClient() {
		Log.v(TAG, "initialize HTTP Client for re-use");

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(
				new Scheme("http",PlainSocketFactory.getSocketFactory(),80));

		HttpParams httpParams = new BasicHttpParams();
		httpParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, CONNECTION_TIMEOUT);
		httpParams.setParameter(CoreConnectionPNames.SO_TIMEOUT, SOCKET_TIMEOUT);

		ConnManagerParams.setMaxTotalConnections(httpParams, 4);			

		ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(httpParams, schemeRegistry);

		HttpClient httpClient = new DefaultHttpClient(cm, httpParams);

		return httpClient;
	}


}
