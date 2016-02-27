package com.firkinofbrain.blackout.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.firkinofbrain.blackout.navmenu.NavMenuItem;
import com.firkinofbrain.blackout.R;

public class NavMenuAdapter extends BaseAdapter{
	
	private Context context;
	private List<NavMenuItem> list;
	
	public NavMenuAdapter(Context context, List<NavMenuItem> list){
		this.context = context;
		this.list = list;
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		
		NavMenuItem nav = list.get(position);
		
		if(v == null){
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if(nav.hasBitmap()){
				v = inflater.inflate(R.layout.row_navmenu_profile, null);
			}else{
				v = inflater.inflate(R.layout.row_navmenu, null);
			}
		}
		
		
		ImageView icon = (ImageView)v.findViewById(R.id.ivRowMenuIcon);
		TextView title = (TextView)v.findViewById(R.id.tvRowMenuTitle);
		
		
		if(nav.hasBitmap()){
			icon.setImageDrawable(nav.getDrawable());
		}else{
			icon.setImageResource(nav.getIcon());
		}
		title.setText(nav.getTitle());
		
		
		return v;
	}

}
