package com.firkinofbrain.blackout.adapters;

import java.util.List;

import com.firkinofbrain.blackout.database.usury.Usury;
import com.firkinofbrain.blackout.R;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UsuryAdapter extends BaseAdapter{
	private List<Usury> usuryList;
	private Context context;
	
	public UsuryAdapter(List<Usury> usuryList, Context context){
		this.usuryList = usuryList;
		this.context = context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View v = convertView;
		
		if(v == null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.row_usury, null);
		}
		
		TextView tvPrice = (TextView)v.findViewById(R.id.tvUsuryPrice);
		TextView tvWho = (TextView)v.findViewById(R.id.tvUsuryWho);
		boolean direct = usuryList.get(position).getDirection() == 1 ? true : false;
		
		RelativeLayout row = (RelativeLayout)v.findViewById(R.id.rlUsuryRow);
		Resources res = context.getResources();
		if(!direct){
			row.setBackgroundColor(res.getColor(R.color.customBg));
		}else{
			row.setBackgroundColor(res.getColor(R.color.customDarkBlue));
		}
		
		tvPrice.setText(usuryList.get(position).getPrice());
		tvWho.setText(usuryList.get(position).getWho());
		
		return v;
	}

	@Override
	public int getCount() {
		return usuryList.size();
	}

	@Override
	public Object getItem(int position) {
		return usuryList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public void update(List<Usury> usuryList){
		this.usuryList.clear();
		this.usuryList.addAll(usuryList);
		this.notifyDataSetChanged();
	}
}
