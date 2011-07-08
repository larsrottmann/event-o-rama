package com.eventorama.mobi.lib.service;

import com.eventorama.mobi.lib.EventORamaApplication;
import com.eventorama.mobi.lib.content.PeopleContentProvider;
import com.eventorama.mobi.lib.data.HTTPResponse;
import com.eventorama.mobi.lib.data.PeopleEntry;
import com.eventorama.mobi.lib.location.LastLocationFinder;
import com.google.gson.Gson;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class GetLocationService extends IntentService {

	private static final String TAG = GetLocationService.class.getName();
	private LocationManager locationManager;
	private LastLocationFinder mlastLocationFinder;
	private EventORamaApplication mApplication;

	private Location gpsLocation = null;
	private Location bestEffortLocation = null;
	
	private final String QUERY = PeopleContentProvider.Columns.SERVER_ID+"= ?";
	
	
	public GetLocationService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
			
		mApplication = (EventORamaApplication)getApplication();
		
		locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
		
		//fire up GPS
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100l, 10.0f, gpsLocationUpdateListener);

		//request last known update in parallel
		
	    // Instantiate a LastLocationFinder class.
	    // This will be used to find the last known location when the application starts.
	    mlastLocationFinder = mApplication.getLastLocationFinder(this);
	    mlastLocationFinder.setChangedLocationListener(oneShotLocationUpdateListener);
	    mlastLocationFinder.getLastBestLocation(150, 15000*60);//150 meters / 15 minutes

	    //wait for 15 seconds, then remove the GPS thingy, take the last known location
	    try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			Log.e(TAG, "Should not happen!");			
		}
	    finally
	    {
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
		locationManager.removeUpdates(gpsLocationUpdateListener);		
	}

	protected LocationListener gpsLocationUpdateListener = new LocationListener() {
		public void onLocationChanged(Location l) {
			Log.v(TAG, "GPS recieved location update: "+l);
			gpsLocation = l;
			if(l.getAccuracy() <= 100)
				locationManager.removeUpdates(this);
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


}
