package com.eventorama.mobi.lib;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eventorama.mobi.lib.content.EventStreamContentProvider;
import com.eventorama.mobi.lib.content.PeopleContentProvider;


public class PeopleAdapter extends CursorAdapter {
	private LayoutInflater mInflater;
	
	private static class PeopleViewHolder {

		private TextView mNameTextView;
		private TextView mStatusTextView;
		private ImageView mImageView;
		
		private String getLastStatusText(Cursor c, Context context) {
			String result =  null;
			int index = c.getColumnIndex(PeopleContentProvider.Columns.LAST_STATUS_ID);
			int lastId = c.getInt(index);
			if (lastId>0) {
				Uri feedUri = Uri.withAppendedPath(EventStreamContentProvider.content_uri, String.valueOf(lastId));
				Cursor feedCursor = context.getContentResolver().query(feedUri, new String[]{EventStreamContentProvider.Columns.TEXT, EventStreamContentProvider.Columns.TITLE}, null, null, null);
				index = feedCursor.getColumnIndex(EventStreamContentProvider.Columns.TEXT);
				result = feedCursor.getString(index);								
				feedCursor.close();
			} 
			return result;
		}
		
		PeopleViewHolder(View v) {
			mNameTextView = (TextView) v.findViewById(R.id.name);
			mStatusTextView = (TextView) v.findViewById(R.id.status);
			mImageView = (ImageView) v.findViewById(R.id.profile_pic);
		}

		public void fillView(Cursor c, Context context) {
			int index = c.getColumnIndex(PeopleContentProvider.Columns.NAME);
			String name = c.getString(index);
			if (name != null && name.length() > 0) {
				mNameTextView.setText(name);
			}

//			index = c.getColumnIndex(PeopleContentProvider.Columns.CREATED);
//			long created = c.getLong(index);
//			CharSequence formattedDate = DateFormat.format("MMM dd, yyyy h:mmaa",  new Date(created));
			
			index = c.getColumnIndex(PeopleContentProvider.Columns.PROFILE_PIC);
			String uriString = c.getString(index);

			BitmapDrawable drawable = (BitmapDrawable) mImageView.getDrawable();
			if (drawable != null) {
				Bitmap bmp = drawable.getBitmap();
				if (bmp != null) {
					bmp.recycle();
				}
			}

			String status = getLastStatusText(c, context);
			mStatusTextView.setText(status);
			
			if (uriString != null) {
				Bitmap thumb = getImageThumbnail(Uri.parse(uriString), context);
				mImageView.setImageBitmap(thumb);
			} else {
				mImageView.setImageBitmap(null);
			}

		}
	}

	private static Bitmap getImageThumbnail(Uri uri, Context context) {
		ContentResolver crThumb = context.getContentResolver();
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;
		long id = Long.valueOf(uri.getLastPathSegment());
		Bitmap thumb = MediaStore.Images.Thumbnails.getThumbnail(crThumb, id, MediaStore.Images.Thumbnails.MINI_KIND, null);
		return thumb;
	}



	public PeopleAdapter(Context context, Cursor c) {
		super(context, c);
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		PeopleViewHolder holder = (PeopleViewHolder) view.getTag();
		holder.fillView(cursor, context);
	};


	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View v = null;
		PeopleViewHolder holder = null;
		v = mInflater.inflate(R.layout.people_grid_element, null);
		holder = new PeopleViewHolder(v);
		v.setTag(holder);
		return v;
	}
}

