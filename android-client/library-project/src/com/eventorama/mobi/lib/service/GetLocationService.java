package com.eventorama.mobi.lib.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import com.eventorama.mobi.lib.EventORamaApplication;
import com.eventorama.mobi.lib.content.PeopleContentProvider;
import com.eventorama.mobi.lib.data.HTTPResponse;
import com.eventorama.mobi.lib.data.PeopleEntry;
import com.eventorama.mobi.lib.location.LastLocationFinder;
import com.google.gson.Gson;

/**
 * This service requests the user's location frequently and posts it to the server 
 * 
 * @author cirrus
 *
 */
public class GetLocationService extends Service {

	private static final String TAG = GetLocationService.class.getName();
	private static LocationManager locationManager;
	private LastLocationFinder mlastLocationFinder;
	private EventORamaApplication mApplication;

	private Location gpsLocation = null;
	private Location bestEffortLocation = null;
	private final String QUERY = PeopleContentProvider.Columns.SERVER_ID+"= ?";

	private boolean isUpdating = false;

	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;

	@Override
	public void onCreate() {

		Log.v(TAG,"on Create");
		locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
		mApplication = (EventORamaApplication)getApplication();
		//		this.looperThread = new LooperThread();
		//		this.looperThread.start();
		//		

		// background priority so CPU-intensive work will not disrupt our UI.
		HandlerThread thread = new HandlerThread("LocationGetter", Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();

		// Get the HandlerThread's Looper and use it for our Handler 
		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		synchronized (this) {
			Log.v(TAG," start command: " +startId + " isUpdateing: "+isUpdating);
			if(isUpdating)
			{
				return START_NOT_STICKY;
			}
			isUpdating = true;
		}

		// For each start request, send a message to start a job and deliver the
		// start ID so we know which request we're stopping when we finish the job
		Message msg = mServiceHandler.obtainMessage();
		msg.arg1 = startId;
		mServiceHandler.sendMessage(msg);

		return START_STICKY;

	}




	// Handler that receives messages from the thread
	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}
		@Override
		public void handleMessage(Message msg) {
			
			//request last known update in parallel			
			// Instantiate a LastLocationFinder class.
			// This will be used to find the last known location when the application starts.
			mlastLocationFinder = mApplication.getLastLocationFinder(getBaseContext());
			mlastLocationFinder.setChangedLocationListener(oneShotLocationUpdateListener);
			bestEffortLocation = mlastLocationFinder.getLastBestLocation(10, 1000*60);//150 meters / 15 minutes
			
			//trigger location update
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsLocationUpdateListener);

			//check result in 20 Seconds
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				Log.e(TAG, "Should not happen!");			
			}
			finally
			{
				Log.v(TAG, "Done waiting...");
				locationManager.removeUpdates(gpsLocationUpdateListener);
			}
			if(gpsLocation != null)
			{
				Log.v(TAG, "using GPS location: "+gpsLocation);
				updateLocationToDBandServer(gpsLocation);
				//trigger sync
				//Intent serviceintent = new Intent(this, PeopleSyncService.class);
				//startService(serviceintent);

			}
			else if(bestEffortLocation != null)
			{
				Log.v(TAG, "using best effort location: "+bestEffortLocation);
				updateLocationToDBandServer(bestEffortLocation);
				//trigger sync
				//Intent serviceintent = new Intent(this, PeopleSyncService.class);
				//startService(serviceintent);
			}
			isUpdating = false;

			//TODO: re-schedule run of this service
			stopSelf();
		}
	}



	private void updateLocationToDBandServer(Location location) {
		final Uri uri = PeopleContentProvider.content_uri;

		SharedPreferences prefs = getSharedPreferences(EventORamaApplication.PREFS_PREFERENCES_NAME, MODE_PRIVATE);
		int uid = prefs.getInt(EventORamaApplication.PREFS_USERID, -1);
		if(uid == -1)
		{
			Log.w(TAG, "I don't know who I am ???");
			return;
		}
		ContentValues cv = new ContentValues();
		cv.put(PeopleContentProvider.Columns.LAT, location.getLatitude());
		cv.put(PeopleContentProvider.Columns.LONG, location.getLongitude());
		cv.put(PeopleContentProvider.Columns.ACCURACY, location.getAccuracy());
		cv.put(PeopleContentProvider.Columns.UPDATED, location.getTime());

		getContentResolver().update(uri, cv, QUERY, new String[]{Integer.toString(uid)});	

		Gson gson = new Gson();
		PeopleEntry pe = new PeopleEntry(-1, null, (float)location.getLatitude(), (float)location.getLongitude(), (float)location.getAccuracy(), location.getTime());
		Log.v(TAG, "gonna post: "+gson.toJson(pe));
		HTTPResponse resp = mApplication.doHttpRequest("/users/"+uid, gson.toJson(pe), EventORamaApplication.HTTP_METHOD_PUT);
		if(resp != null && resp.getRespCode() == 200)
			Log.v(TAG, "Location post to server successfull!");
	}

	@Override
	public void onDestroy() {	
		super.onDestroy();
		Log.v(TAG,"on destroy");
		locationManager.removeUpdates(gpsLocationUpdateListener);		
	}

	protected LocationListener gpsLocationUpdateListener = new LocationListener() {
		public void onLocationChanged(Location l) {
			Log.v(TAG, "GPS recieved location update: "+l);
			gpsLocation = l;
			if(l.getAccuracy() <= 100)
			{
				Log.v(TAG, "Accuracy reached lesser then 100 meters, removing listener!");
				locationManager.removeUpdates(this);
			}

		}
		public void onProviderDisabled(String provider) {}
		public void onStatusChanged(String provider, int status, Bundle extras) {}
		public void onProviderEnabled(String provider) {}
	};

	protected LocationListener oneShotLocationUpdateListener = new LocationListener() {
		public void onLocationChanged(Location l) {
			Log.v(TAG, "One shot recieved location update from "+l.getProvider());
			bestEffortLocation = l;
		}

		public void onProviderDisabled(String provider) {}
		public void onStatusChanged(String provider, int status, Bundle extras) {}
		public void onProviderEnabled(String provider) {}
	};

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
