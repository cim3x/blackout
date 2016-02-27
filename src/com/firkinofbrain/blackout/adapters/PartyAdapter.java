package com.firkinofbrain.blackout.adapters;

import java.util.List;

import com.firkinofbrain.blackout.AppBlackout;
import com.firkinofbrain.blackout.database.party.Party;
import com.firkinofbrain.blackout.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PartyAdapter extends BaseAdapter{
	private List<Party> partyList;
	private Context context;
	private AppBlackout app;
	
	public PartyAdapter(List<Party> partyList, Context context){
		this.partyList = partyList;
		this.context = context;
		this.app = ((AppBlackout)context.getApplicationContext()).getInstance();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View v = convertView;
		
		if(v == null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.row_history, null);
		}
		
		Party party = partyList.get(position);
		((TextView)v.findViewById(R.id.tvHistoryInfo)).setText(party.getWhere() + " • " + party.getWhen());
		((TextView)v.findViewById(R.id.tvHistoryName)).setText(party.getName());
		
		if(party.getSync() == 1)
			((ImageView)v.findViewById(R.id.ivHistoryIcon)).setColorFilter(app.getFilterColor(party.getPartyID()));
		
		return v;
	}


	@Override
	public int getCount() {
		return partyList.size();
	}


	@Override
	public Object getItem(int position) {
		return partyList.get(position);
	}


	@Override
	public long getItemId(int position) {
		return 0;
	}
}
