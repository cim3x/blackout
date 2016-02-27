package com.firkinofbrain.blackout;

import java.util.ArrayList;
import java.util.List;

import com.firkinofbrain.blackout.database.event.EventType;
import com.firkinofbrain.blackout.database.event.Events;
import com.firkinofbrain.blackout.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class ViewCanvas extends View{
	
	private Paint paint;
	private List<Events> list;
	
	private int begin = 0;
	private float colWidth;
	private double max = 0;
	
	private int ceiling = 20, floor; 
	
	private int lightBlue, darkBlue, bg;
	
	public ViewCanvas(Context context, AttributeSet aSet) {
		super(context, aSet);
		this.paint = new Paint();
		this.list = new ArrayList<Events>();
		
		lightBlue = context.getResources().getColor(R.color.customLightBlue);
		darkBlue = context.getResources().getColor(R.color.customDarkBlue);
		bg = context.getResources().getColor(R.color.customBg);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int width = canvas.getWidth();
		int height = canvas.getHeight();
		float wP = width/4; //width of painting area
		float hP = height - 20; //height of painting area
			floor = height;
			
		colWidth = (width-wP)/(list.size()+1);
		double scale = hP/max;
		
		//draw map net
		paint.setColor(bg);
		canvas.drawRect(0, 0, width, height, paint);
		paint.setColor(Color.WHITE);
		paint.setStrokeWidth(1);
		canvas.drawLine(0, ceiling, width, ceiling, paint);
		canvas.drawLine(0, (ceiling+floor)/2, width, (ceiling+floor)/2, paint);
		canvas.drawLine(0, floor, width, floor, paint);
		canvas.drawText(String.valueOf(round2(max)), 0, ceiling, paint);
		canvas.drawText(String.valueOf(round2(max/2)), 0, (ceiling+floor)/2, paint);
		
		float x = wP, y;
		
		try{
			for(int i=0;i<list.size();i++){
				Events ev = list.get(i);
				
				String t = ev.getType();
				if(t == EventType.BEER || t == EventType.WINE || t == EventType.VODKA)
				if(i == begin){
					//First visible item
					y = (float) (floor - list.get(begin).getAlcohol()*scale);
					paint.setColor(lightBlue);
					canvas.drawRect(x, y, x+colWidth *0.9f, floor, paint);
				}else{
					y = (float) (floor - ev.getAlcohol()*scale);
					paint.setColor(darkBlue);
					canvas.drawRect(x, y, x+colWidth * 0.9f, floor, paint);
					paint.setColor(lightBlue);
					canvas.drawRect(x, y, x+colWidth * 0.9f, y+10, paint);
				}
				
				x += colWidth;
			}
		}catch(IndexOutOfBoundsException e){
			Log.e(AppBlackout.TAG, e.toString());
		}
	}
	
	public void setList(List<Events> list){
		this.list = list;
		
		for(int i=0;i<list.size();i++){
			double ev = list.get(i).getAlcohol();
			if(ev > max) max = ev;
		}
	}
	
	public void setMark(int mark){
		this.begin = mark;
	}
	
	private double round2(double n){
		n *= 100;
		n = Math.round(n);
		n /= 100;
		
		return n;
	}
}
