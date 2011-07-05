package com.eventorama.mobi.lib.tests;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;


import com.eventorama.mobi.lib.content.EventStreamContentProvider;
import com.eventorama.mobi.lib.content.PeopleContentProvider;

public class PeopleContentProviderTest extends ProviderTestCase3<PeopleContentProvider>{

	private ContentValues testValues = new ContentValues();
	
	private static final String TEST_NAME = "my test name";
	private static final float TEST_LAT = 5.8556f;
	private static final float TEST_LON = 3.212314416f;
	private static final long TEST_UPDATED = 1309787759850l;
	private static final String TEST_PROFILE_PIC = "tbd";
	private static final float TEST_ACCURACY = 201.8556f;
	private static final int TEST_SERVER_ID = 3001;
	
	public PeopleContentProviderTest()
	{
		super(PeopleContentProvider.class, MyIsolatedContext.TEST_PACKAGE);
	}


	protected void setUp() throws Exception {
		super.setUp();
		
		testValues.put(PeopleContentProvider.Columns.ACCURACY, TEST_ACCURACY);
		testValues.put(PeopleContentProvider.Columns.LAT, TEST_LAT);
		testValues.put(PeopleContentProvider.Columns.LONG, TEST_LON);
		testValues.put(PeopleContentProvider.Columns.UPDATED, TEST_UPDATED);
		testValues.put(PeopleContentProvider.Columns.PROFILE_PIC, TEST_PROFILE_PIC);
		testValues.put(PeopleContentProvider.Columns.NAME, TEST_NAME);
		testValues.put(PeopleContentProvider.Columns.SERVER_ID, TEST_SERVER_ID);
	}

	public void testInsertData()
	{
		ContentProvider provider = getProvider();
		Uri uri = PeopleContentProvider.content_uri;

		final long now = System.currentTimeMillis();
		
		Uri entryuri = provider.insert(uri, testValues);
		
		assertNotNull(entryuri);
		
		Cursor c = provider.query(entryuri, null, null, null, null);
		
		assertNotNull(c);
		assertTrue(c.moveToFirst());
		assertVals(c);
		assertFalse(c.getLong(c.getColumnIndex(PeopleContentProvider.Columns.CREATED)) == TEST_UPDATED);
		
		long created = c.getLong(c.getColumnIndex(EventStreamContentProvider.Columns.CREATED));    	
    	assertTrue("created timstamp differs! is bigger then 100 ms "+(created-now)+ " created: "+created+" now "+now, new Long((created-now)).compareTo(new Long(100)) <= 0);
    	assertTrue("created timstamp differs! less then 0 ms "+(created-now)+ " created: "+created+" now "+now, new Long((created-now)).compareTo(new Long(0)) >= 0);
		
		c.close();
	}
	
	public void testDeleteData()
	{
		ContentProvider provider = getProvider();
		Uri uri = PeopleContentProvider.content_uri;
		
		Uri entryuri = provider.insert(uri, testValues);
		
		assertNotNull(entryuri);
		
		int entries = provider.delete(entryuri, null, null);
		assertEquals(entries, 1);
		
		Cursor c = provider.query(entryuri, null, null, null, null);
		assertNotNull(c);
		assertFalse(c.moveToFirst());
	}
	
	public void testUpdateData()
	{
		ContentProvider provider = getProvider();
		Uri uri = PeopleContentProvider.content_uri;
		
		Uri entryuri = provider.insert(uri, testValues);

		final float new_lat = 8.6542867f;
		final float new_lon = 2.000000867f;
		final long new_updated = 1309787759856l;
		
		ContentValues cv = new ContentValues();
		cv.put(PeopleContentProvider.Columns.LAT, new_lat);
		cv.put(PeopleContentProvider.Columns.LONG, new_lon);
		cv.put(PeopleContentProvider.Columns.UPDATED, new_updated);
		
		int updated = provider.update(entryuri, cv, null, null);
		assertEquals(updated, 1);
		
		Cursor c = provider.query(entryuri, null, null, null, null);
		assertNotNull(c);
		assertTrue(c.moveToFirst());
		
		assertEquals(c.getFloat(c.getColumnIndex(PeopleContentProvider.Columns.LAT)), new_lat);
		assertEquals(c.getFloat(c.getColumnIndex(PeopleContentProvider.Columns.LONG)), new_lon);
		assertEquals(c.getLong(c.getColumnIndex(PeopleContentProvider.Columns.UPDATED)), new_updated);
		
		c.close();
	}
	


	private void assertVals(Cursor c) {
		assertEquals(c.getFloat(c.getColumnIndex(PeopleContentProvider.Columns.LONG)), TEST_LON);
		assertEquals(c.getFloat(c.getColumnIndex(PeopleContentProvider.Columns.LAT)), TEST_LAT);
		assertEquals(c.getFloat(c.getColumnIndex(PeopleContentProvider.Columns.ACCURACY)), TEST_ACCURACY);
		assertEquals(c.getString(c.getColumnIndex(PeopleContentProvider.Columns.NAME)), TEST_NAME);
		assertEquals(c.getString(c.getColumnIndex(PeopleContentProvider.Columns.PROFILE_PIC)), TEST_PROFILE_PIC);
		assertEquals(c.getInt(c.getColumnIndex(PeopleContentProvider.Columns.SERVER_ID)), TEST_SERVER_ID);
	}

}
