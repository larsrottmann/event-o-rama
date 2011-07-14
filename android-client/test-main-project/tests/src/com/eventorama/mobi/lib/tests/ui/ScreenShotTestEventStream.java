package com.eventorama.mobi.lib.tests.ui;

import org.cirrus.mobi.tests.tools.Screenshot;

import com.eventorama.mobi.lib.EventStreamActivity;
import com.eventorama.mobi.lib.LocationActivity;
import com.eventorama.mobi.lib.tests.EmulatorCommands;
import com.google.android.maps.MapView;
import com.jayway.android.robotium.solo.Solo;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.test.ActivityInstrumentationTestCase2;

public class ScreenShotTestEventStream extends ActivityInstrumentationTestCase2<LocationActivity> {

	private LocationActivity mActivity;
	private Solo mSolo;


	public ScreenShotTestEventStream() {
		super(LocationActivity.class);
	}
	
	
	public void setUp() throws Exception {
		mActivity = getActivity();
		mSolo = new Solo(getInstrumentation(), mActivity);
	}
	
	public void testScreenshots() throws Exception {
		mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//		EmulatorCommands.injectFlipEvent(true);
		mSolo.waitForView(MapView.class);
		mSolo.sleep(10000);
		super.getInstrumentation().waitForIdleSync();
		Screenshot.save_screenshot(mActivity.getWindow(), "portrait");
		
		mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//		EmulatorCommands.injectFlipEvent(false);
		mSolo.waitForView(MapView.class);
		mSolo.sleep(10000);
		
		super.getInstrumentation().waitForIdleSync();
		Screenshot.save_screenshot(mActivity.getWindow(), "landscape");		
	}

}
