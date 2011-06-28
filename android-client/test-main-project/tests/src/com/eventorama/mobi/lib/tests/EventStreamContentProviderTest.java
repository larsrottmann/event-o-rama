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
    
    
    public void testQuery(){
        ContentProvider provider = getProvider();

        Uri uri = EventStreamContentProvider.content_uri;

        Cursor cursor = provider.query(uri, null, null, null, null);

        assertNotNull(cursor);

        cursor = null;
        try {
            cursor = provider.query(Uri.parse("definitelywrong"), null, null, null, null);
            // we're wrong if we get until here!
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
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
    	
    	Log.v("TTEST", "got entry uri: "+entryUri);
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
    	
    }
    

}
