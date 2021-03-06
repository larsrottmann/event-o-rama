package com.eventorama.mobi.lib;



import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.eventorama.mobi.lib.content.PeopleContentProvider;
import com.eventorama.mobi.lib.data.PeopleEntry;
import com.eventorama.mobi.lib.location.LastLocationFinder;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class LocationActivity extends MapActivity {

	private static final String TAG = "LocationActivity";

	private static final String PREF_LON_KEY = "p_lon";
	private static final String PREF_LAT_KEY = "p_lat";
	private static final String PREF_ZOOM_KEY = "p_zoom";
	private static final String PREFS_NAME = "map_prefs";

	private static final int DEFAULT_ZOOM = 15;

	private static final String PEOPLE_WITH_LOC_QUERY = PeopleContentProvider.Columns.LAT + " != 0.0 AND "+PeopleContentProvider.Columns.LONG+ "!= 0.0";


	private MapView mMapView;
	private EventORamaApplication mApplication;
	private LastLocationFinder mlastLocationFinder;
	private ContentResolver mContentResolver;
	private Context mContext = this;

	private UsersOverlay mUserOverlay;

	private AddPeopleTask mAddPeopleTask;

	private MarkerFactory mMarkerFactory;

	//0wKeEJnsZleEt7KEuAtS6xj5g9BdReRFzyu5t7g
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_map);


		this.mMarkerFactory = new MarkerFactory();
		this.mContentResolver = getContentResolver();
		this.mApplication = (EventORamaApplication) getApplication();
		this.mMapView = (MapView) findViewById(R.id.mapview);

		List<Overlay> mapOverlays = mMapView.getOverlays();

		this.mUserOverlay = new UsersOverlay(getResources().getDrawable(R.drawable.map_marker));
		mapOverlays.add(mUserOverlay);

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


		ImageView nav_events = (ImageView) findViewById(R.id.nav_events);		
		nav_events.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, EventStreamActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);				
			}
		});

		ImageView nav_people = (ImageView) findViewById(R.id.nav_people);		
		nav_people.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, PeopleActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);
			}
		});

		ImageView nav_location = (ImageView) findViewById(R.id.nav_location);
		nav_location.setSelected(true);
		nav_location.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
			}
		});

	}

	@Override
	protected void onStart() {
		super.onStart();
		//get all locations from the people content provider
		//TODO: run this in a seperate thread!
		this.mAddPeopleTask = new AddPeopleTask();
		this.mAddPeopleTask.doInBackground(null);
	}


	private class AddPeopleTask extends AsyncTask<Integer, Integer, Integer>
	{

		@Override
		protected Integer doInBackground(Integer... params) {
			updateLocationsFromDB();	
			return null;
		}

	}

	private void updateLocationsFromDB() {
		final Uri uri = PeopleContentProvider.content_uri;		
		Cursor c = mContentResolver.query(uri, null, PEOPLE_WITH_LOC_QUERY, null, null);
		if(c != null && c.moveToFirst())
		{
			do {
				PeopleEntry pe = new PeopleEntry(c.getInt(c.getColumnIndex(PeopleContentProvider.Columns.SERVER_ID)),
						c.getString(c.getColumnIndex(PeopleContentProvider.Columns.NAME)),
						c.getFloat(c.getColumnIndex(PeopleContentProvider.Columns.LAT)),
						c.getFloat(c.getColumnIndex(PeopleContentProvider.Columns.LONG)),
						c.getFloat(c.getColumnIndex(PeopleContentProvider.Columns.ACCURACY)),
						c.getLong(c.getColumnIndex(PeopleContentProvider.Columns.UPDATED)));

				//get bytes
				byte[]img = c.getBlob(c.getColumnIndex(PeopleContentProvider.Columns.PROFILE_PIC));
				BitmapDrawable bd = null;
				if(img != null)
					bd = mMarkerFactory.getMarker(pe.getServerId(), img);
				//			if(Log.isLoggable(TAG, Log.VERBOSE))
				Log.v(TAG, "adding: "+pe+" to the map!");
				//for now we just create a simple item
				mUserOverlay.addEntry(new GeoPoint((int)(pe.getLat()*1E6), (int)(pe.getLon()*1E6)), pe.getName(), pe.getAccuracy()+"", bd);

			} while(c.moveToNext());
		}
		if(c != null)
			c.close();

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


	private class MarkerFactory
	{
		private static final int BORDER = 4;
		private int MAX_WIDTH = 0;
		private int MAX_HEIGHT = 0;
		private Bitmap bitMapBG;
		private Bitmap blankImage;
		private Paint mPaint;

		public MarkerFactory() {
			this.bitMapBG = BitmapFactory.decodeResource(getResources(), R.drawable.map_marker);
			this.MAX_WIDTH = bitMapBG.getWidth()-(BORDER*2);
			this.MAX_HEIGHT = MAX_WIDTH;
			mPaint = new Paint();
			mPaint.setDither(true);
			mPaint.setAntiAlias(true);
		}

		private BitmapDrawable getMarker(int userId, byte[] image)
		{
			this.blankImage = Bitmap.createBitmap(bitMapBG.getWidth(), bitMapBG.getHeight(), this.bitMapBG.getConfig());
			
			//TODO: caching of course!
			Canvas canvas = new Canvas(blankImage);
			canvas.drawBitmap(bitMapBG, 0, 0, mPaint);

			Bitmap userImage = BitmapFactory.decodeByteArray(image, 0, image.length);
			if(userImage.getWidth() > MAX_WIDTH || userImage.getHeight() > MAX_HEIGHT)
			{
				//scale the thingy
				final float scaleWidth =  MAX_WIDTH / (float)userImage.getWidth();
				final float scaleHeight =  MAX_HEIGHT / (float)userImage.getHeight();
				final float scale = Math.min(scaleWidth, scaleHeight);

				Matrix matrix = new Matrix();
				// resize the bit map
				matrix.postScale(scale, scale);

				Bitmap resizedBitmap = Bitmap.createBitmap(userImage, 0, 0,
						userImage.getWidth(), userImage.getHeight(), matrix, true); 

				canvas.drawBitmap(resizedBitmap, BORDER, BORDER, mPaint);

			}
			else
				canvas.drawBitmap(userImage, BORDER, BORDER, mPaint);

			userImage.recycle();

			BitmapDrawable bmd = new BitmapDrawable(blankImage);

			return bmd;
		}

	}


}
