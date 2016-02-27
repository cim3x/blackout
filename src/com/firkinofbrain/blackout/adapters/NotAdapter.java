package com.firkinofbrain.blackout.adapters;

import java.util.List;

import com.firkinofbrain.blackout.database.notification.Not;
import com.firkinofbrain.blackout.database.notification.Not.NotType;
import com.firkinofbrain.blackout.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NotAdapter extends BaseAdapter{

	private Context context;
	private List<Not> list;
	
	public NotAdapter(Context context, List<Not> list){
		this.context = context;
		this.list = list;
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Not getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		
		if(v == null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.row_nots, null);
		}
		
		Not not = list.get(position);
		
		if(not.isRead()){
			v.setBackgroundColor(context.getResources().getColor(R.color.customBlack));
		}else{
			v.setBackgroundColor(context.getResources().getColor(R.color.customAb));
		}
		
		String type = not.getType();
		((ImageView)v.findViewById(R.id.ivRowNotsIcon)).setImageBitmap(getNotIcon(type));
		if(type.equals(NotType.PARTY)){
			((TextView)v.findViewById(R.id.tvRowNotsName)).setText(not.getDesc() + " in " + not.getWhere());
			((TextView)v.findViewById(R.id.tvRowNotsInfo)).setText(not.getWhen() + " • by " + not.getUserName());
		}else if(type.equals(NotType.STATUS)){
			((TextView)v.findViewById(R.id.tvRowNotsName)).setText(not.getDesc());
			((TextView)v.findViewById(R.id.tvRowNotsInfo)).setText(not.getUserName() + " changed status");
		}else if(type.equals(NotType.PHOTO)){
			//TODO
		}else if(type.equals(NotType.FOLLOWER)){
			((TextView)v.findViewById(R.id.tvRowNotsName)).setText(not.getDesc() + " is following you now");;
		}else if(type.equals(NotType.SEARCH)){
			((TextView)v.findViewById(R.id.tvRowNotsName)).setText(not.getDesc() + " in " + not.getWhere());
			((TextView)v.findViewById(R.id.tvRowNotsInfo)).setText(not.getWhen() + " • by " + not.getUserName());
		}else{
			((TextView)v.findViewById(R.id.tvRowNotsName)).setText(not.getDesc());
		}
		
		
		
		return v;
	}
	
	private int dstWidth = 48, dstHeight = 48;
	private Bitmap getNotIcon(String type){
		
		Bitmap bmp = null;
		Drawable d = null;
		
		if(type.equals(NotType.PARTY)){
			d = context.getResources().getDrawable(R.drawable.ic_menu_logo);
		}else if(type.equals(NotType.PHOTO)){
			d = context.getResources().getDrawable(R.drawable.ic_menu_image);
		}else if(type.equals(NotType.SEARCH)){
			d = context.getResources().getDrawable(R.drawable.ic_menu_search);
		}else if(type.equals(NotType.STATUS)){
			d = context.getResources().getDrawable(R.drawable.ic_menu_speech);
		}else if(type.equals(NotType.STATUS)){
			d = context.getResources().getDrawable(R.drawable.ic_menu_profile);
		}else{
			d = context.getResources().getDrawable(R.drawable.ic_menu_help);
		}
		
		bmp = ((BitmapDrawable)d).getBitmap();
		bmp = Bitmap.createScaledBitmap(bmp, dstWidth, dstHeight, false);
		
		return bmp;
	}
	
}
