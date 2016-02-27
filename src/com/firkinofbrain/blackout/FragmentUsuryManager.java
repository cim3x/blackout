package com.firkinofbrain.blackout;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.firkinofbrain.blackout.adapters.UsuryAdapter;
import com.firkinofbrain.blackout.database.DataManager;
import com.firkinofbrain.blackout.database.usury.Usury;
import com.firkinofbrain.blackout.database.usury.UsuryDialog;
import com.firkinofbrain.blackout.tools.HelpManager;
import com.firkinofbrain.blackout.R;

public class FragmentUsuryManager extends Fragment implements OnClickListener, OnItemLongClickListener{
	
	private AppBlackout app;
	private DataManager dbManager;
	private int position;
	private List<Usury> usuryList;
	private UsuryAdapter adapter;
	private ListView listV;
	
	private ActivityMain activity;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		activity = ((ActivityMain)getActivity());
		View view = inflater.inflate(R.layout.activity_usurymanager, container, false);
		activity.setTitle(getResources().getString(R.string.usury_header));
		
		view.findViewById(R.id.bUsuryAdd).setOnClickListener(this);
		
		app = ((AppBlackout)activity.getApplicationContext()).getInstance();
		dbManager = app.getDataBaseManager();
		usuryList = dbManager.getAllUsury();
		
		HelpManager hm = new HelpManager(activity);
		hm.getWindowHelp(HelpManager.LOAN, "Loan manager", R.string.help_loan);
		
		adapter = new UsuryAdapter(usuryList, activity.getApplicationContext());
		listV = (ListView)view.findViewById(R.id.lvUsury);
		listV.setAdapter(adapter);
		
		listV.setOnItemLongClickListener(this);
		
		position = -1;
		
		return view;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.bUsuryAdd:
			makePrompt();
			break;
		}
	}
	
	private void makePrompt(){
		
		UsuryDialog usuryDialog = new UsuryDialog(activity, adapter);
		usuryDialog.show();
		
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, final int position,
			long id) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(activity)
		.setTitle("Do you want to delete this item?")
		.setPositiveButton("Delete", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Usury u = usuryList.get(position);
				dbManager.deleteUsury(u.getId());
				usuryList.remove(position);
				adapter.notifyDataSetChanged();
			}
			
		});
		builder.create().show();
		
		return false;
	}
	
}
