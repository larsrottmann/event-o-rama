package com.eventorama.mobi.lib.tests;

import org.cirrus.mobi.tests.tools.PrivateAccessor;

import junit.framework.Assert;

import com.eventorama.mobi.lib.SignUpActivity;

import android.test.ActivityInstrumentationTestCase2;


public class SignUpActivityTest extends ActivityInstrumentationTestCase2<SignUpActivity> {

	private SignUpActivity mSignupActivity;

	public SignUpActivityTest() {
		super("com.eventorama.mobi.lib", SignUpActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.mSignupActivity = getActivity();
	}
	
	public void testCapitalize()
	{
		Object[] params = new Object[1];
		params[0] = "this is a Test s t r i n g   ";
		
		String result = (String) PrivateAccessor.invokePrivateMethod(mSignupActivity, "capitalizeString", params);
		
		Assert.assertTrue(result.equals("This Is A Test S T R I N G"));
		
	}
	
	public void testExtractUserId()
	{
		Object[] params = new Object[1];
		params[0] = "http://blah.blubs.com/test/830984/iwueo/4711";
		
		int result = (Integer) PrivateAccessor.invokePrivateMethod(mSignupActivity, "extractUserId", params);
		
		assertEquals(4711, result);		
	}

}
