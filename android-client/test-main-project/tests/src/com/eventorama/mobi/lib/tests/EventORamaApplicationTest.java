package com.eventorama.mobi.lib.tests;

import com.eventorama.mobi.lib.EventORamaApplication;
import com.eventorama.mobi.lib.data.HTTPResponse;

import android.content.Context;
import android.test.ApplicationTestCase;

public class EventORamaApplicationTest extends ApplicationTestCase<EventORamaApplication> {

	private Context mContext;
	private EventORamaApplication mApplication;

	
	public EventORamaApplicationTest() {
		super(EventORamaApplication.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createApplication();
		this.mContext = getSystemContext();
		this.mApplication = getApplication();
	}
	
	public void testGetLocationFinder()
	{
		assertNotNull(mApplication.getLastLocationFinder(mContext));
	}
	
	public void testDoHttpRequest()
	{
		HTTPResponse resp = mApplication.doHttpRequest("/users", "", EventORamaApplication.HTTP_METHOD_GET);
		assertNotNull("http-response should not be null", resp);
		assertEquals("response code should be 200", 200, resp.getRespCode());
		assertNotNull("http-response body should not be null", resp.getBody());
		assertTrue("response body length should be > 0",resp.getBody().length() > 0);
		
		resp = mApplication.doHttpRequest("/unknown", "", EventORamaApplication.HTTP_METHOD_GET);
		assertNotNull("http-response should not be null",resp);
		assertEquals("response code should be 404",404, resp.getRespCode());
		
		resp = mApplication.doHttpRequest("/posttest", "", EventORamaApplication.HTTP_METHOD_POST);
		assertNotNull("http-response should not be null",resp);
		assertEquals("response code should be 404",404, resp.getRespCode());
	
		resp = mApplication.doHttpRequest("/puttest", "", EventORamaApplication.HTTP_METHOD_PUT);
		assertNotNull("http-response should not be null",resp);
		assertEquals("response code should be 405",405, resp.getRespCode());		
	}

}
