package com.firkinofbrain.blackout.adapters;

import java.io.File;
import java.util.List;

import com.firkinofbrain.blackout.AppBlackout;
import com.firkinofbrain.blackout.database.event.EventType;
import com.firkinofbrain.blackout.database.event.Events;
import com.firkinofbrain.blackout.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EventAdapter extends BaseAdapter{
	private List<Events> eventList;
	private Context context;
	private String path;
	
	public EventAdapter(List<Events> eventList, Context context){
		this.eventList = eventList;
		this.context = context;
		path = Environment.getExternalStorageDirectory().getName() + File.separatorChar + "/BlackoutDiaryPhotos/";
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View v = convertView;
		
		if(v == null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.row_history, null);
		}
		
		Events event = eventList.get(position);
		
		String type = event.getType();
		if(type.equals(EventType.PHOTO) || type.equals(EventType.KISS) || type.equals(EventType.CIGAR) || type.equals(EventType.SHISHA) || type.equals(EventType.JOINT) || type.equals(EventType.GEO))
			((TextView)v.findViewById(R.id.tvHistoryName)).setText(event.getType());
		else
			((TextView)v.findViewById(R.id.tvHistoryName)).setText(event.getType() + " • " + event.getDescription());
		
		((TextView)v.findViewById(R.id.tvHistoryInfo)).setText(event.getTime());
		
		ImageView iv = (ImageView)v.findViewById(R.id.ivHistoryIcon);
		iv.setImageBitmap(getTypeIcon(event.getType()));
		
		return v;
	}

	@Override
	public int getCount() {
		return eventList.size();
	}

	@Override
	public Object getItem(int pos) {
		return eventList.get(pos);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}
	
	private int dstWidth = 54, dstHeight = 54;
	private Bitmap getTypeIcon(String type){
		
		Drawable d = null;
		Bitmap bmp = null;
		
		if(type.equals(EventType.BEER)){
			d = context.getResources().getDrawable(R.drawable.ic_status_beer);
		}else if(type.equals(EventType.VODKA)){
			d = context.getResources().getDrawable(R.drawable.ic_status_vodka);
		}else if(type.equals(EventType.WINE)){
			d = context.getResources().getDrawable(R.drawable.ic_status_wine);
		}else if(type.equals(EventType.PHOTO)){
			d = context.getResources().getDrawable(R.drawable.ic_status_photo);
		}else if(type.equals(EventType.CIGAR)){
			d = context.getResources().getDrawable(R.drawable.ic_status_cigarette);
		}else if(type.equals(EventType.JOINT)){
			d = context.getResources().getDrawable(R.drawable.ic_status_joint);
		}else if(type.equals(EventType.SHISHA)){
			d = context.getResources().getDrawable(R.drawable.ic_status_waterpipe);
		}else if(type.equals(EventType.KISS)){
			d = context.getResources().getDrawable(R.drawable.ic_status_kiss);
		}else{
			d = context.getResources().getDrawable(R.drawable.ic_status_logo);
		}
		
		bmp = ((BitmapDrawable)d).getBitmap();
		bmp = Bitmap.createScaledBitmap(bmp, dstWidth, dstHeight, false);
		
		return bmp;
		
	}
	
}
