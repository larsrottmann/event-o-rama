package com.eventorama.mobi.lib.views;

import com.eventorama.mobi.lib.EventStreamAdapter;
import com.eventorama.mobi.lib.R;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EventStreamListView extends ListView{

//	private AbsListView mlistView;
	private Rect paddingRectangle = new Rect();
	private GestureDetector mlistViewGestureDetector;

	private TextView mTextView = null;


	public EventStreamListView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mlistViewGestureDetector = new GestureDetector(new ListViewGestureDetector());
		

		/*if( getTag() == null || getTag().toString().equalsIgnoreCase("ListView"))
		{   
			mlistView = new ListView(context);   
		}
		else if(getTag().toString().equalsIgnoreCase("GridView"))   
		{   
			mlistView = new GridView(context, attrs);
			((GridView)mlistView).getSelector().getPadding(paddingRectangle);
		}
		mlistView.setId(android.R.id.list);*/
		//		mlistView.setOverScrollMode(OVER_SCROLL_NEVER);
//		addView(this, new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if(ev.getAction() == MotionEvent.ACTION_UP)
			scrollTo(0, 0);
		else
			if(mlistViewGestureDetector.onTouchEvent(ev))
				return true;
		return super.dispatchTouchEvent(ev);

	}

	private boolean listIsAtTop()   {   return getChildAt(0).getTop() - paddingRectangle.top == 0;     }
	//	

	private class ListViewGestureDetector extends SimpleOnGestureListener
	{       
		private         float   scrollDistanceSinceBoundary     = 0;

		@Override
		public boolean onScroll(MotionEvent downMotionEvent, MotionEvent currentMotionEvent, float distanceX, float distanceY) 
		{

			Log.v("GESTURE:", "onScroll ");
			float distanceTraveled = downMotionEvent.getY() - currentMotionEvent.getY();
			if(listIsAtTop() && distanceTraveled <= 0) // At top and finger moving down
			{
				scrollDistanceSinceBoundary -= distanceY;
				Log.v("GEST", "Moving down: "+scrollDistanceSinceBoundary+" "+ distanceY +" "+distanceTraveled);
				scrollBy(0, (int) distanceY);
				//scroll textview down
				mTextView = (TextView) ((View)getParent()).findViewById(R.id.simpleText);
				RelativeLayout.LayoutParams lpr = (android.widget.RelativeLayout.LayoutParams) mTextView.getLayoutParams();
				lpr.setMargins(0, (int) (lpr.topMargin+(-distanceY)), 0, 0);
				mTextView.setLayoutParams(lpr);
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

}
