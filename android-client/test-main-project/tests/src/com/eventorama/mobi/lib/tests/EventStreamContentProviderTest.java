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
    	ContentProvider provider = getProvider();
    	
    	Uri uri = EventStreamContentProvider.content_uri;
    	ContentValues values = new ContentValues();
    	values.put(EventStreamContentProvider.Columns.TEXT, "hi text");
    	Uri entryUri = provider.insert(uri, values);
    	
    	assertNotNull(entryUri);
    	
    	Log.v("TTEST", "got entry uri: "+entryUri);
    	//read it back
    	Cursor c = provider.query(entryUri, null, null, null, null);
    	assertNotNull(c);
    	assertTrue(c.getCount() == 1);
    	
    	assertTrue(c.moveToFirst());
    	String text = c.getString(c.getColumnIndex(EventStreamContentProvider.Columns.TEXT));
    	assertTrue(text.equals("hi text"));
    	
    }
    

}
