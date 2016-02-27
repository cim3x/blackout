package com.firkinofbrain.blackout;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.firkinofbrain.blackout.server.ServerManager;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.NavUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

public class FragmentSearch extends ListFragment implements OnQueryTextListener{
	
	private ListView lvResults;
	private SearchView searchView;
	private ServerManager sm;
	private SearchAdapter adapter;
	private List<Result> list;
	private TextView title;
	private static final String FOLLOW = "Follow";
	private static final String UNFOLLOW = "Unfollow";
	private ActivitySearch activity;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
        View v = inflater.inflate(R.layout.activity_profile, container, false);
		
        activity = ((ActivitySearch)getActivity());
		ActionBar actionBar = activity.getActionBar();
		actionBar.setTitle("Search friends");
		actionBar.setIcon(R.drawable.icon_logo_white512);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.show();
		
		sm = new ServerManager();
		
		list = new ArrayList<Result>();
		adapter = new SearchAdapter(activity, list);
		setVictims();
		
		lvResults = (ListView)v.findViewById(R.id.lvSearchResults);
		lvResults.setAdapter(adapter);
		
		title = (TextView)v.findViewById(R.id.tvSearchTitle);
		
		
		return v;
	}

	private void setVictims() {
		new AsyncTask<Void,Void,Void>(){

			@Override
			protected Void doInBackground(Void... params) {
				JSONArray json = sm.getVictims();
				list.clear();
				setResults(json);
				
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				
				notifyList();
				adapter.notifyDataSetChanged();
			}
			
			
		}.execute();
		
	}
	
	private void setResults(JSONArray json){
    	for(int i=0; i<json.length();i++){
    		JSONObject obj;
			try {
				obj = json.getJSONObject(i);
				Result res = new Result();
	    		res.userId = obj.getString("id");
	    		res.userName = obj.getString("name");
	    		res.avatar = obj.getString("avatar");
	    		res.city = obj.getString("city");
	    		if(res.city.equals("null")) res.city = "";
	    		res.country = obj.getString("country");
	    		if(res.country.equals("null")) res.country = "";
	    		res.observe = obj.getInt("observe");
	    		
	    		list.add(res);
	    		
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
    }
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
	        NavUtils.navigateUpFromSameTask(activity);
	        return true;
		}

		return super.onOptionsItemSelected(item);
	}

    public boolean onQueryTextChange(String newText) {
    	Log.i(AppBlackout.TAG, newText);
    	final String query = newText;
    	Log.i(AppBlackout.TAG, "setVictims3 <" + newText +">");
    	if(newText != null && !newText.equals("")){
	    	new AsyncTask<Void,Void,Void>(){
	
				@Override
				protected Void doInBackground(Void... params) {
					
					JSONArray json = sm.getUsers(query);
			    	list.clear();
			    	
			    	setResults(json);
					
					
					return null;
				}
				
				@Override
				protected void onPostExecute(Void result) {
					super.onPostExecute(result);
					
					notifyList();
					adapter.notifyDataSetChanged();
				}
	    		
	    	}.execute();
    	}else{
    		Log.i(AppBlackout.TAG, "setVictims()");
			setVictims();
		}
    	
        return false;
    }

    protected void notifyList() {
		if(list.size() < 1)
			title.setText("Sorry, but we couldn't find anyone");
		else
			title.setText("");
	}

	public boolean onQueryTextSubmit(final String query) {
    	
    	
        return false;
    }
    
    public class Result{
    	public String userId;
    	public String avatar;
    	public String userName;
    	public String city;
    	public String country;
    	public int observe;
    }
	
    public class SearchAdapter extends BaseAdapter{

    	private Context context;
    	private List<Result> list;
    	private Button bObserve;
    	
    	public SearchAdapter(Context context, List<Result> list){
    		this.context = context;
    		this.list = list;
    	}
    	
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Result getItem(int id) {
			return list.get(id);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View v = convertView;
			
			if(v == null){
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.row_search,parent,false);
				bObserve = (Button)v.findViewById(R.id.bRowSearchObserve);
			}
			
			Result res = list.get(position);
			
			if(res.observe == 1){
				setUnfollow(bObserve);
				
			}else{
				setFollow(bObserve);
			}
			bObserve.setId(position);
			bObserve.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					
					
					Button b = (Button)v;
					int id = b.getId();
					final Result res = list.get(id);
					if(b.getText().toString().equals(FOLLOW)){
						res.observe = 1;
						setUnfollow(b);
					}else{
						res.observe = 0;
						setFollow(b);
					}
					list.set(id, res);
					new AsyncTask<Void,Void,Void>(){

						@Override
						protected void onPostExecute(Void result) {
							super.onPostExecute(result);
							activity.setProgressBarIndeterminateVisibility(false);
						}

						@Override
						protected void onPreExecute() {
							super.onPreExecute();
							activity.setProgressBarIndeterminateVisibility(true);
						}

						@Override
						protected Void doInBackground(Void... params) {
							sm.updateObserver(res.userId, res.observe);
							return null;
						}
						
					}.execute();
					
					adapter.notifyDataSetChanged();
					
				}
				
			});
			
			ImageView avatar = (ImageView)v.findViewById(R.id.ivRowSearchAvatar);
			byte[] base64 = null;
			base64 = Base64.decode(res.avatar, Base64.DEFAULT);
			avatar.setImageBitmap(BitmapFactory.decodeByteArray(base64, 0, base64.length));
			
			((TextView)v.findViewById(R.id.tvRowSearchUser)).setText(res.userName);
			((TextView)v.findViewById(R.id.tvRowSearchCity)).setText(res.city);
			((TextView)v.findViewById(R.id.tvRowSearchCountry)).setText(res.country);
			
			return v;
		}

		protected void setUnfollow(Button b) {
			b.setText(UNFOLLOW);
			b.setBackgroundColor(context.getResources().getColor(R.color.customTransBg));
			b.setTextColor(context.getResources().getColor(R.color.customDarkBlue));
			
		}
		
		protected void setFollow(Button b){
			b.setText(FOLLOW);
			b.setBackgroundColor(context.getResources().getColor(R.color.customDarkBlue));
			b.setTextColor(context.getResources().getColor(R.color.customWhite));
		}
    	
    }
}
