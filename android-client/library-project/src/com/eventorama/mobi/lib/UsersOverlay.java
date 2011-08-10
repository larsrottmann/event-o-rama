package com.eventorama.mobi.lib;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class UsersOverlay extends ItemizedOverlay<OverlayItem>{

	private List<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

	public UsersOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		populate();
	}

	public void addEntry(OverlayItem overlay) {
		mOverlays.add(overlay);
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();		
	}

	public void addEntry(GeoPoint geoPoint, String name, String string,
			BitmapDrawable bd) {

		OverlayItem oi = new OverlayItem(geoPoint, name, string);
		if(bd != null)
			oi.setMarker(boundCenterBottom(bd));
		mOverlays.add(oi);
		populate();
	}

}
