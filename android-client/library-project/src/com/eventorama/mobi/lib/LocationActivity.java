package com.eventorama.mobi.lib;

import android.os.Bundle;

import com.google.android.maps.MapActivity;

public class LocationActivity extends MapActivity {
	//0wKeEJnsZleEt7KEuAtS6xj5g9BdReRFzyu5t7g
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
