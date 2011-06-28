package com.eventorama.mobi.lib;

import com.eventorama.mobi.lib.content.EventStreamContentProvider;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class EventStreamAdapter extends CursorAdapter {

	private LayoutInflater mInflater;

	public EventStreamAdapter(Context context, Cursor c) {
		super(context, c);
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		EventStreamViewHolder holder = (EventStreamViewHolder) view.getTag();
		holder.fillView(cursor, context);
	};


	@Override
	public long getItemId(int position) {
		if (getCursor().moveToPosition(position)) {
			int index = getCursor().getColumnIndex(EventStreamContentProvider.Columns.ID);
			return getCursor().getLong(index);
		}
		return -1;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View v = null;
		EventStreamViewHolder holder = null;
		v = mInflater.inflate(R.layout.eventstream_list_item_text, null);
		holder = new EventStreamViewHolder(v);
		v.setTag(holder);
		return v;
	}
	
	
	private static class EventStreamViewHolder
	{
		private TextView mMessageTextView;

		public EventStreamViewHolder(View v) {
			this.mMessageTextView = (TextView) v.findViewById(R.id.eventstream_list_item_text_message);
		}
		
		
		public void fillView(Cursor c, Context context) {
			int index = c.getColumnIndex(EventStreamContentProvider.Columns.TITLE);			
			String title = c.getString(index);
			if (title != null && title.length() > 0) {
				mMessageTextView.setText(title);
			}
		}
	}
}
