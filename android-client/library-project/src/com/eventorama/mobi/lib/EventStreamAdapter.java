package com.eventorama.mobi.lib;

import com.eventorama.mobi.lib.content.EventStreamContentProvider;
import com.eventorama.mobi.lib.content.PeopleContentProvider;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EventStreamAdapter extends CursorAdapter {
	private static final String TAG = "EventStreamAdapter";

	private static final String PEOPLE_QUERY = PeopleContentProvider.Columns.SERVER_ID + " = ?";

	private LayoutInflater mInflater;
	private ContentResolver mResolver;
	
	public EventStreamAdapter(Context context, Cursor c) {
		super(context, c);
		this.mInflater = LayoutInflater.from(context);
		this.mResolver = context.getContentResolver();
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		EventStreamViewHolder holder = (EventStreamViewHolder) view.getTag();
		holder.fillView(cursor, context);
	};


	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View v = null;
		EventStreamViewHolder holder = null;
		//get view type
		int type = cursor.getInt(cursor.getColumnIndex(EventStreamContentProvider.Columns.TYPE));
		switch (type) {
		case 1:
			v = mInflater.inflate(R.layout.eventstream_list_item_text, null);			
			break;
		case 2: 			
			v = mInflater.inflate(R.layout.eventstream_list_item_text, null);
			break;
		default:
			Log.e(TAG, "unknown event type, cannot inflate view! type: "+type);
			break;
		}
				
		holder = new EventStreamViewHolder(v);
		v.setTag(holder);
		return v;
	}
	
	
	private static class EventStreamViewHolder
	{
		private TextView mMessageTextView;
		private TextView mUsernameTextView;
		private ImageView mUserPicView;
		private ContentResolver mResolver = null;

		public EventStreamViewHolder(View v) {
			this.mMessageTextView = (TextView) v.findViewById(R.id.eventstream_list_item_text_message);
			this.mUsernameTextView = (TextView) v.findViewById(R.id.eventstream_list_item_text_username);
			this.mUserPicView = (ImageView) v.findViewById(R.id.eventstream_list_item_image_user);
		}
		
		
		public void fillView(Cursor c, Context context) {
			if(this.mResolver == null)
				this.mResolver = context.getContentResolver();
			
			//TODO: user image
			
			//set user name from people content provider
			final int userid = c.getInt(c.getColumnIndex(EventStreamContentProvider.Columns.PEOPLE_ID));
			Cursor pc = this.mResolver.query(PeopleContentProvider.content_uri, null, PEOPLE_QUERY, new String[]{Integer.toString(userid)}, null);
			String username = null;
			if(pc != null && pc.moveToFirst())
			{
				username = pc.getString(pc.getColumnIndex(PeopleContentProvider.Columns.NAME));
				byte[]img = pc.getBlob(pc.getColumnIndex(PeopleContentProvider.Columns.PROFILE_PIC));
				if(img != null && img.length > 0)
				{
					Bitmap b = BitmapFactory.decodeByteArray(img, 0, img.length);
					this.mUserPicView.setImageBitmap(b);
				}
				else
					this.mUserPicView.setImageBitmap(null);
				pc.close();
			}			
			if(username != null && username.length() > 0)
				mUsernameTextView.setText(username);
			
			//set main text			
			final String title = c.getString(c.getColumnIndex(EventStreamContentProvider.Columns.TEXT));
			if (title != null && title.length() > 0) {
				mMessageTextView.setText(title);
			}
		}
	}
}
