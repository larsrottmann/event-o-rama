package com.eventorama.mobi.lib.tests;

import com.eventorama.mobi.lib.content.EventStreamContentProvider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;


public class EventStreamContentProviderTest extends ProviderTestCase3<EventStreamContentProvider>{

	public EventStreamContentProviderTest() {
		super(EventStreamContentProvider.class, MyIsolatedContext.TEST_PACKAGE);
	}
	
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    public void testDeleteData()
    {
    	ContentProvider provider = getProvider();
    	
    	Uri uri = EventStreamContentProvider.content_uri;
    	
    	Uri entryUri = provider.insert(uri, null);

    	Cursor c = provider.query(uri, null, null, null, null);    	
    	int entries = c.getCount();
    	c.close();
    	
    	int result = provider.delete(entryUri, null, null);
    	
    	c = provider.query(uri, null, null, null, null);    	
    	int entriesnow = c.getCount();
    	c.close();
    	
    	assertEquals(1, result);
    	assertTrue(entriesnow < entries);    
    }
    
    public void testUpdatedServerSync()
    {
    	//create dummy data
    	ContentProvider provider = getProvider();
       	Uri uri = EventStreamContentProvider.content_uri;
       	
       	ContentValues cv = new ContentValues();
       	cv.put(EventStreamContentProvider.Columns.SAVE_STATE, EventStreamContentProvider.SAVE_STATE_LOCAL);
       	for(int i = 0; i < 10; i++)
       	{
       		cv.put(EventStreamContentProvider.Columns.TEXT, "Text"+i);
       		provider.insert(uri, cv);
       	}

       	cv.put(EventStreamContentProvider.Columns.SAVE_STATE, EventStreamContentProvider.SAVE_STATE_SERVER);
       	for(int i = 0; i < 10; i++)
       	{
       		cv.put(EventStreamContentProvider.Columns.TEXT, "TextSaved"+i);
       		provider.insert(uri, cv);
       	}

       	Cursor c = provider.query(uri, null, EventStreamContentProvider.Columns.SAVE_STATE+"="+EventStreamContentProvider.SAVE_STATE_LOCAL, null, null);
       	assertEquals(10, c.getCount());
    	
       	c = provider.query(uri, null, EventStreamContentProvider.Columns.SAVE_STATE+"="+EventStreamContentProvider.SAVE_STATE_SERVER, null, null);
       	assertEquals(10, c.getCount());

       	//update everything to saved Server
       	cv = new ContentValues();
       	cv.put(EventStreamContentProvider.Columns.SAVE_STATE, EventStreamContentProvider.SAVE_STATE_SERVER);
       	int updates = provider.update(uri, cv, EventStreamContentProvider.Columns.SAVE_STATE+"="+EventStreamContentProvider.SAVE_STATE_LOCAL, null);
       	assertEquals(10,updates);
    }
    
    public void testInsertData()
    {
    	final String TEST_TEXT = "hi text";
    	
    	ContentProvider provider = getProvider();
    	
    	Uri uri = EventStreamContentProvider.content_uri;

    	Cursor c = provider.query(uri, null, null, null, null);    	
    	int entries = c.getCount();
    	c.close();
    	
    	Uri entryUri = provider.insert(uri, null);
    	c = provider.query(uri, null, null, null, null);
    	assertEquals(entries+1, c.getCount());    
    	
    	ContentValues values = new ContentValues();    	
		values.put(EventStreamContentProvider.Columns.TEXT, TEST_TEXT);
		long now = System.currentTimeMillis(); 
    	entryUri = provider.insert(uri, values);
    	
    	assertNotNull(entryUri);
    	
    	//read it back
    	c = provider.query(entryUri, null, null, null, null);
    	assertNotNull(c);
    	assertTrue(c.getCount() == 1);
    	
    	assertTrue(c.moveToFirst());
    	String text = c.getString(c.getColumnIndex(EventStreamContentProvider.Columns.TEXT));
    	assertTrue(text.equals(TEST_TEXT));
    	
    	long created = c.getLong(c.getColumnIndex(EventStreamContentProvider.Columns.CREATED));    	
    	assertTrue("created timstamp differs! is bigger then 100 ms "+(created-now)+ " created: "+created+" now "+now, new Long((created-now)).compareTo(new Long(100)) <= 0);
    	assertTrue("created timstamp differs! less then 0 ms "+(created-now)+ " created: "+created+" now "+now, new Long((created-now)).compareTo(new Long(0)) >= 0);

    }
    
    public void testUpdateData()
    {
    	final String TEST_TEXT = "hi text";

    	ContentProvider provider = getProvider();   	
    	Uri uri = EventStreamContentProvider.content_uri;
    	
    	Uri entryUri = provider.insert(uri, null);
    	
    	ContentValues cv = new ContentValues();
    	cv.put(EventStreamContentProvider.Columns.TEXT, TEST_TEXT);
    	
    	int updates = provider.update(entryUri, cv, null, null);
    	assertEquals(1, updates);
    	
    	cv = new ContentValues();
    	cv.put(EventStreamContentProvider.Columns.CREATED, 5555);
    	try {
    		provider.update(entryUri, cv, null, null);
    		fail("tried to update CREATED field, no exception thrown!");
    	}
    	catch(IllegalArgumentException ex)
    	{
    		assertTrue("Exception thrown as expected: "+ex,true);
    	}
    
    }
    

}
