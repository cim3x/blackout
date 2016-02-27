package com.firkinofbrain.blackout.tools;

import com.firkinofbrain.blackout.AppBlackout;

import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class OnSwipeTouchListener implements OnTouchListener{

	private static final int SWIPE_DISTANCE_THRESHOLD = 100;
	private static final int SWIPE_VELOCITY_THRESHOLD = 100;
	
	private boolean FLAG_DOWN = false;
	private PointF down;
	private long startTime;
	
	public OnSwipeTouchListener(){
		down = new PointF();
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		int action = event.getAction() & MotionEvent.ACTION_MASK;
		switch(action){
		case MotionEvent.ACTION_DOWN:
			if(!FLAG_DOWN){
				down.x = event.getX();
				down.y = event.getY();
				
				startTime = System.currentTimeMillis();
				FLAG_DOWN = true;
			}
			break;
		case MotionEvent.ACTION_UP:
			
			if(FLAG_DOWN){
				float dx = down.x - event.getX();
				float dt = dx/(System.currentTimeMillis() - startTime);
				Log.i(AppBlackout.TAG, "Swipe: "+ dx + " x " + dt);
				if(dx > SWIPE_DISTANCE_THRESHOLD && dt > SWIPE_VELOCITY_THRESHOLD)
				if(dx > 0){
					onSwipeLeft();
				}else if(dx < 0){
					onSwipeRight();
				}
				
				FLAG_DOWN = false;
			}
			break;
		}
		
		return false;
	}
	
	public void onSwipeRight(){
		
	}
	
	public void onSwipeLeft(){
		
	}

}
