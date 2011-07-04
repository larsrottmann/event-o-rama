package com.eventorama.mobi.lib.content;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class EventStreamContentProvider extends ContentProvider {

	private final static String TAG = EventStreamContentProvider.class.toString();

	public static Uri content_uri = null;

	public static final int SAVE_STATE_LOCAL = 1;
	public static final int SAVE_STATE_SERVER = 2;
	
	public static final int TYPE_TEXT = 1;

	public static class Columns {
		public static final String ID = "_id";
		public static final String CREATED = "created";//timestamp
		public static final String TITLE = "title";
		public static final String TYPE = "type";
		public static final String TEXT = "text";
		public static final String PEOPLE_ID ="people_id";
		public static final String SAVE_STATE ="save_state";
	}
	
	
    private static UriMatcher sUriMatcher;
    private static final int EVENT = 0;
    private static final int EVENTS = 1;
	
    private static class DBHelper extends SQLiteOpenHelper {

        private static final String TABLE_NAME = "eventstream";
        private static final int DATABASE_VERSION = 4;
        
        private static final String DATABASE_NAME = "eventorama";
        
        private static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
        Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
        Columns.CREATED + " INTEGER, " +
        Columns.TYPE + " INTEGER, " +
        Columns.PEOPLE_ID + " INTEGER, " +
        Columns.TEXT + " TEXT, " +
        Columns.TITLE+ " TEXT, " +
        Columns.SAVE_STATE + " INTEGER );";
        
        DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
	
    
    private DBHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        String authority = getContext().getPackageName()+".events";
        
        content_uri =  Uri.parse("content://"+ authority +"/eventstream");
        Log.v(TAG, "using package as authority: "+authority+ "registered uri: "+content_uri);
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(authority, "eventstream/#", EVENT);
        sUriMatcher.addURI(authority, "eventstream", EVENTS);

        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case EVENT:
                return "vnd.android.cursor.dir/vnd.eventorama.event";
            case EVENTS:
                return "vnd.android.cursor.item/vnd.eventorama.events";
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

	  @Override
	    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
	        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
	        qb.setTables(DBHelper.TABLE_NAME);
	        String limit = null;

	        switch (sUriMatcher.match(uri)) {
	            case EVENT:
	                String id = uri.getLastPathSegment();
	                selection = Columns.ID + "=?";
	                selectionArgs = new String[] {id};
	                break;
	            case EVENTS:
	                break;
	            default:
	                throw new IllegalArgumentException("Unknown URI " + uri);
	        }

	        // If no sort order is specified use the default
	        String orderBy;
	        if (TextUtils.isEmpty(sortOrder)) {
	            orderBy = "created DESC";
	        } else {
	            orderBy = sortOrder;
	        }

	        // Get the database and run the query
	        SQLiteDatabase db = dbHelper.getReadableDatabase();
	        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy, limit);

	        // Tell the cursor what uri to watch, so it knows when its source data changes
	        c.setNotificationUri(getContext().getContentResolver(), uri);
	        return c;
	    }


	  @Override
	    public Uri insert(Uri uri, ContentValues values) {

	        if (values != null) {
	            values = new ContentValues(values);
	        } else {
	            values = new ContentValues();
	        }
	        
	        if(!values.containsKey(Columns.CREATED))
	        {
	        	final Long now = Long.valueOf(System.currentTimeMillis());
	        	values.put(Columns.CREATED, now);
	        }

	        SQLiteDatabase db = dbHelper.getWritableDatabase();
	        long rowId = db.insert(DBHelper.TABLE_NAME, null, values);
	        if (rowId > 0) {
	            Uri entryUri = ContentUris.withAppendedId(content_uri, rowId);
	            getContext().getContentResolver().notifyChange(content_uri, null);
	            return entryUri;
	        } else {
	            throw new SQLException("Failed to insert row into " + uri);
	        }
	    }

	    @Override
	    public int delete(Uri uri, String selection, String[] selectionArgs) {
	        SQLiteDatabase db = dbHelper.getWritableDatabase();
	        int count;
	        switch (sUriMatcher.match(uri)) {
	            case EVENTS:
	                count = db.delete(DBHelper.TABLE_NAME, selection, selectionArgs);
	                break;
	            case EVENT:
	                String id = uri.getLastPathSegment();
	                count = db.delete(DBHelper.TABLE_NAME, Columns.ID + "=?", new String[] {id});
	                break;
	            default:
	                throw new IllegalArgumentException("Unknown URI " + uri);
	        }
	        getContext().getContentResolver().notifyChange(uri, null);
	        return count;
	    }

	    @Override
	    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
	    	if(values != null)
	    	{
	    		if(values.containsKey(EventStreamContentProvider.Columns.CREATED))
	    			throw new IllegalArgumentException("You can not update the field 'CREATED'!");
	    	}
	    	
	        SQLiteDatabase db = dbHelper.getWritableDatabase();
	        int count = 0;
	        switch (sUriMatcher.match(uri)) {
	            case EVENTS:
	                count = db.update(DBHelper.TABLE_NAME, values, selection, selectionArgs);
	                break;

	            case EVENT: {
	                String id = uri.getLastPathSegment();
	                count = db.update(DBHelper.TABLE_NAME, values, Columns.ID + "=?", new String[] {id});
	                break;
	            }
	            default:
	                throw new IllegalArgumentException("Unknown URI " + uri);
	        }
	        getContext().getContentResolver().notifyChange(uri, null);
	        return count;
	    }

}
