package com.eventorama.mobi.lib.tests;


import android.content.ContentResolver;
import android.content.Context;
import android.test.IsolatedContext;

public class MyIsolatedContext extends IsolatedContext {

	static final String TEST_PACKAGE = "com.eventorama.mobi.sowhatevernew";

	public MyIsolatedContext(ContentResolver eventStreamContentProvider, Context targetContext) {
		super(eventStreamContentProvider, targetContext);
	}
	
	@Override
	public String getPackageName() {
		return TEST_PACKAGE;
	}

}
