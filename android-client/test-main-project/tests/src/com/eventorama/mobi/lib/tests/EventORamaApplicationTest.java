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
		HTTPResponse resp = mApplication.doHttpRequest("/users", null, EventORamaApplication.HTTP_METHOD_GET);
		assertNotNull(resp);
		assertEquals(200, resp.getRespCode());
		assertNotNull(resp.getBody());
		assertTrue(resp.getBody().length() > 0);
		
		resp = mApplication.doHttpRequest("/unknown", null, EventORamaApplication.HTTP_METHOD_GET);
		assertNotNull(resp);
		assertEquals(404, resp.getRespCode());
		
		resp = mApplication.doHttpRequest("/posttest", null, EventORamaApplication.HTTP_METHOD_POST);
		assertNotNull(resp);
		assertEquals(404, resp.getRespCode());
	
		resp = mApplication.doHttpRequest("/puttest", null, EventORamaApplication.HTTP_METHOD_PUT);
		assertNotNull(resp);
		assertEquals(405, resp.getRespCode());
		
	}

}
