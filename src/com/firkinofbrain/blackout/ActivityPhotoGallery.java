package com.firkinofbrain.blackout;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;

import com.firkinofbrain.blackout.image.PhotoFeed;
import com.firkinofbrain.blackout.server.ServerManager;
import com.firkinofbrain.blackout.tools.NoConListener;
import com.firkinofbrain.blackout.tools.ReceiverNetworkState;
import com.firkinofbrain.blackout.R;

public class ActivityPhotoGallery extends FragmentActivity implements NoConListener{
	
	private ViewPager viewPager;
	private CollectionPagerAdapter pagerAdapter;
	
	private ServerManager sm;
	private int limit = 1, end = 10;
	private DownloadImageTask imageLoader;
	private String partyid= null;
	private List<PhotoFeed> feeds;
	
	private FragmentActivity activity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photogalery);
		
		activity = this;
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("Newest photos"); //TODO filter by newest, hotest
		
		viewPager = (ViewPager)findViewById(R.id.vpPhotoFeed);
		feeds = new ArrayList<PhotoFeed>();
		pagerAdapter = new CollectionPagerAdapter(getSupportFragmentManager(), feeds);
		viewPager.setAdapter(pagerAdapter);
		
		sm = new ServerManager();
		
		Bundle b = getIntent().getExtras();
		if(b != null){
			partyid = b.getString(AppBlackout.I_PARTYID);
		}
		
		imageLoader = new DownloadImageTask();
		imageLoader.execute(limit, end);
		Log.i(AppBlackout.TAG, "gallery1");
		
		
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if(item.getItemId() == android.R.id.home)
			NavUtils.navigateUpFromSameTask(this);
		
		return super.onMenuItemSelected(featureId, item);
	}
	
	
	public void like(final String photoid, final boolean like, int fragment){
		feeds.get(fragment).ranked = like;
		new AsyncTask<Void,Void,Void>(){

			@Override
			protected Void doInBackground(Void... params) {
				sm.likePhoto(photoid, like);
				return null;
			}
			
		}.execute();
	}


	public class CollectionPagerAdapter extends FragmentStatePagerAdapter{
		
		List<PhotoFeed> feeds;
		
		public CollectionPagerAdapter(FragmentManager fragmentManager, List<PhotoFeed> feeds) {
			super(fragmentManager);
			this.feeds = feeds;
		}
		
		public void setList(List<PhotoFeed> feeds){
			this.feeds = feeds;
			this.notifyDataSetChanged();
		}
		
		@Override
		public Fragment getItem(int i) {
			
			Fragment fragment = null;
			
			if(ReceiverNetworkState.haveNetworkConnection(activity)){
				if(i == getCount()-1){
					loadNextPhotos();
				}
				
				fragment = new FragmentPhotoFeed();
				Bundle args = new Bundle();
				PhotoFeed feed = feeds.get(i);
				args.putInt("position", i);
				args.putString("id", feed.id);
				args.putString("url", feed.url);
				args.putString("info", feed.info);
				args.putBoolean("ranked", feed.ranked);
				fragment.setArguments(args);
			}else{
				fragment = new FragmentNoCon();
			}
			
			return fragment;
		}

		@Override
		public int getCount() {
			return feeds.size();
		}
		
	}
	
	private void loadNextPhotos(){
		limit = end+1;
		end = limit + 5;
		new DownloadImageTask().execute(limit, end);
	}
	
	private class DownloadImageTask extends AsyncTask<Integer, Void, Void> {

		@Override
		protected void onPostExecute(Void result) {
			pagerAdapter.setList(feeds);
			super.onPostExecute(result);
		}

		@Override
		protected Void doInBackground(Integer... results) {
			feeds.addAll(sm.getPhotos(results[0], results[1], partyid));
			
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(!ReceiverNetworkState.haveNetworkConnection(activity)){
				
			}
		}
		
		
	}

	@Override
	public void notifyAdapter() {
		pagerAdapter.notifyDataSetChanged();
	}
}
