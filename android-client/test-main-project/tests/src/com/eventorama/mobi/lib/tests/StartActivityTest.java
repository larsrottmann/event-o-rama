package com.eventorama.mobi.lib.tests;

import com.eventorama.mobi.lib.StartActivity;

import android.test.ActivityInstrumentationTestCase2;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.eventorama.mobi.lib.StartActivityTest \
 * com.eventorama.mobi.whatever.tests/android.test.InstrumentationTestRunner
 */
public class StartActivityTest extends ActivityInstrumentationTestCase2<StartActivity> {

    public StartActivityTest() {
        super("com.eventorama.mobi.whatever", StartActivity.class);
    }

}
