package com.eventorama.mobi.lib.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eventorama.mobi.lib.EventStreamActivity;
import com.eventorama.mobi.lib.EventStreamAdapter;
import com.eventorama.mobi.lib.R;

public class EventStreamListView extends ListView{

//	private AbsListView mlistView;
	private Rect paddingRectangle = new Rect();
	private GestureDetector mlistViewGestureDetector;

	private TextView mTextView = null;
	private boolean isRefreshing = false;
	private EventStreamActivity mActivity = null;
	private boolean isOverscroll = false;


	public EventStreamListView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mlistViewGestureDetector = new GestureDetector(new ListViewGestureDetector());
		//this is not nice but how should we trigger the refresh to the activity else?
		if(context instanceof EventStreamActivity)
			this.mActivity = (EventStreamActivity) context;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if(ev.getAction() == MotionEvent.ACTION_UP)
		{
			scrollTo(0, 0);
			this.isOverscroll = false;
		}
		else
		{
			this.isOverscroll = mlistViewGestureDetector.onTouchEvent(ev);
			if(this.isOverscroll)
				return true;
		}
		return super.dispatchTouchEvent(ev);

	}
	
	private void triggerRefresh() {
		this.isRefreshing  = true;
		mTextView.setText("Refreshing!!!!");
		//notify activity that we need a refresh
		if(this.mActivity != null)
			this.mActivity.doRefresh();
	}
	
	@Override
	public void scrollTo(int x, int y) {
		Log.v("---","OS"+isOverscroll);
		if(this.isOverscroll)
			return;
		super.scrollTo(x, y);
	}

	private boolean listIsAtTop()   {   return getChildAt(0).getTop() - paddingRectangle.top == 0;     }
	//	

	private class ListViewGestureDetector extends SimpleOnGestureListener
	{       
//		private float scrollDistanceSinceBoundary = 0;
		private final float REFRESH_DISTANCE = 40;

		
		
		@Override
		public boolean onScroll(MotionEvent downMotionEvent, MotionEvent currentMotionEvent, float distanceX, float distanceY) 
		{

			Log.v("GESTURE:", "onScroll "+isRefreshing);
			float distanceTraveled = downMotionEvent.getY() - currentMotionEvent.getY();
			if(listIsAtTop() && distanceTraveled <= 0) // At top and finger moving down
			{
				if(isRefreshing)
					return true;
//				scrollDistanceSinceBoundary -= distanceY;
				Log.v("GEST", "Moving down: "+ distanceY +" "+distanceTraveled);
				//scroll textview down
				if(mTextView == null)
					mTextView = (TextView) ((View)getParent()).findViewById(R.id.simpleText);
				if(-distanceTraveled >= REFRESH_DISTANCE)
				{
					//loc list and view to this position
					distanceTraveled = -REFRESH_DISTANCE;
					//set listview margins
					RelativeLayout.LayoutParams lpr = (android.widget.RelativeLayout.LayoutParams) getLayoutParams();
//					lpr.setMargins(0, (int) (lpr.topMargin+(-distanceTraveled)), 0, 0);
					lpr.setMargins(0, (int) (-distanceTraveled), 0, 0);
					setLayoutParams(lpr);
					//scrollTo(0, 0);
					
					triggerRefresh();
				}
			//	else
					//scrollTo(0, (int) distanceTraveled);
				
				RelativeLayout.LayoutParams lpr = (android.widget.RelativeLayout.LayoutParams) mTextView.getLayoutParams();
				lpr.setMargins(0, (int) (-distanceTraveled), 0, 0);
				mTextView.setLayoutParams(lpr);
				
				lpr = (android.widget.RelativeLayout.LayoutParams) getLayoutParams();
				lpr.setMargins(0, (int) (-distanceTraveled), 0, 0);
				setLayoutParams(lpr);
				return true;
			}

			Log.v("!!!!!!!!!!!!!", "FALSE!!!");
			return false;

		}
		//		private boolean listIsAtBottom(){ return mlistView.getChildAt(mlistView.getChildCount()-1).getBottom() + paddingRectangle.bottom == mlistView.getHeight(); } 

	
	}


	public void setAdapter(EventStreamAdapter mAdapter) {
		super.setAdapter(mAdapter);		
	}

	public void refreshComplete() {
		this.isRefreshing = false;
		//move back the views
		RelativeLayout.LayoutParams lpr = (android.widget.RelativeLayout.LayoutParams) getLayoutParams();
		lpr.setMargins(0, 0, 0, 0);
		setLayoutParams(lpr);
		if(mTextView != null)
		{
			mTextView.setLayoutParams(lpr);
			mTextView.setText("Pull to refresh");
		}
		invalidate();
	}

}
