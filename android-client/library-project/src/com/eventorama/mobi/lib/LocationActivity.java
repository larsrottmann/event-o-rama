package com.eventorama.mobi.lib;



import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.eventorama.mobi.lib.location.LastLocationFinder;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class LocationActivity extends MapActivity {
	
	private static final String TAG = LocationActivity.class.getName();
	
	private static final String PREF_LON_KEY = "p_lon";
	private static final String PREF_LAT_KEY = "p_lat";
	private static final String PREF_ZOOM_KEY = "p_zoom";
	private static final String PREFS_NAME = "map_prefs";

	private static final int DEFAULT_ZOOM = 15;
	
	
	private MapView mMapView;

	private EventORamaApplication mApplication;

	private LastLocationFinder mlastLocationFinder;

	//0wKeEJnsZleEt7KEuAtS6xj5g9BdReRFzyu5t7g
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_map);

		this.mApplication = (EventORamaApplication) getApplication();
		this.mMapView = (MapView) findViewById(R.id.mapview);

		//we don't have settings, zoom to roughly current pos
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);		
		if(!settings.contains(PREF_LAT_KEY))
		{
			mlastLocationFinder = mApplication.getLastLocationFinder(getBaseContext());
			mlastLocationFinder.setChangedLocationListener(oneShotLocationUpdateListener);
			Location bestEffortLocation = mlastLocationFinder.getLastBestLocation(10, System.currentTimeMillis()-15*1000);//150 meters / 15 minutes
			if(bestEffortLocation != null)
				updateMap(bestEffortLocation);
		}		
		
		this.mMapView.setBuiltInZoomControls(true);
	}
	
	
	protected void updateMap(Location l) {
		mMapView.getController().setCenter(new GeoPoint((int)(l.getLatitude()*1E6), (int)(l.getLongitude()*1E6)));
		mMapView.getController().setZoom(DEFAULT_ZOOM);		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);		
		if(settings.getInt(PREF_ZOOM_KEY, -1) != -1)
		{		
			mMapView.getController().setCenter(new GeoPoint(settings.getInt(PREF_LAT_KEY,0), settings.getInt(PREF_LON_KEY,0)));
			mMapView.getController().setZoom(settings.getInt(PREF_ZOOM_KEY, 15));
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		//save current mapview
		SharedPreferences settings = getSharedPreferences(PREFS_NAME , Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = settings.edit();
		if(mMapView != null)
		{
			edit.putInt(PREF_ZOOM_KEY, mMapView.getZoomLevel());
			edit.putInt(PREF_LAT_KEY, mMapView.getMapCenter().getLatitudeE6());
			edit.putInt(PREF_LON_KEY, mMapView.getMapCenter().getLongitudeE6());
			edit.commit();
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	
	protected LocationListener oneShotLocationUpdateListener = new LocationListener() {
		public void onLocationChanged(Location l) {
			Log.v(TAG, "One shot recieved location update from "+l.getProvider());
			updateMap(l);
		}

		public void onProviderDisabled(String provider) {}
		public void onStatusChanged(String provider, int status, Bundle extras) {}
		public void onProviderEnabled(String provider) {}
	};



}
