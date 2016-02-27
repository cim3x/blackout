package com.firkinofbrain.blackout;

import java.util.List;

import com.firkinofbrain.blackout.adapters.EventAdapter;
import com.firkinofbrain.blackout.database.DataManager;
import com.firkinofbrain.blackout.database.event.Events;
import com.firkinofbrain.blackout.server.ServerManager;
import com.firkinofbrain.blackout.tools.ReceiverNetworkState;
import com.firkinofbrain.blackout.R;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ListView;

public class ActivityHistoryDetails extends FragmentActivity implements OnClickListener{

	private ViewCanvas canvas;
	private Handler frame = new Handler();
	private static final int FRAME_RATE = 20;
	private String partyid = null;
	
	private List<Events> eventList;
	private DataManager dbManager;
	private AppBlackout app;
	private ServerManager sm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.activity_historydetails);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("Party details");
		
		app = (AppBlackout)getApplicationContext();
		dbManager = app.getDataBaseManager();
		sm = new ServerManager();
		
		canvas = (ViewCanvas) findViewById(R.id.canvas);
		Bundle b = getIntent().getExtras();
		if(b != null){
			partyid = b.getString(AppBlackout.I_PARTYID);
		}
		eventList = dbManager.getAllByUserAndParty(partyid, dbManager.getUser().getUserId());
		
		if(eventList.size() == 0 && ReceiverNetworkState.haveNetworkConnection(this)){
			eventList = sm.getEvents(getIntent().getExtras().getString(AppBlackout.I_PARTYID, AppBlackout.PARTY_ID));
		}
		
		canvas.setList(eventList);
		canvas.setMark(0);
		frame.removeCallbacks(frameUpdate);
		frame.postDelayed(frameUpdate, FRAME_RATE);
		
		((Button)findViewById(R.id.bGoToMap)).setOnClickListener(this);
		((Button)findViewById(R.id.bGoToPhotoGallery)).setOnClickListener(this);
		ListView lvEvents = (ListView)findViewById(R.id.lvHistoryDetailsEvents);
		EventAdapter adapter = new EventAdapter(eventList, this);
		lvEvents.setAdapter(adapter);
		lvEvents.setOnScrollListener(new OnScrollListener(){

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				canvas.setMark(firstVisibleItem);
			}
		});
		
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if(item.getItemId() == android.R.id.home)
			NavUtils.navigateUpFromSameTask(this);
		
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.bGoToMap:
			startActivity(new Intent(this, ActivityMapShow.class).putExtra(AppBlackout.I_PARTYID, partyid));
			break;
		case R.id.bGoToPhotoGallery:
			startActivity(new Intent(this, ActivityPhotoGallery.class).putExtra(AppBlackout.I_PARTYID, partyid));
			break;
		}
	}
	
	private Runnable frameUpdate = new Runnable(){

		@Override
		public void run() {
			frame.removeCallbacks(frameUpdate);
			canvas.invalidate();
			frame.postDelayed(frameUpdate, FRAME_RATE);
		}
		
	};
}
