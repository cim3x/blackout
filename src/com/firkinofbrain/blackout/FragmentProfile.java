package com.firkinofbrain.blackout;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.firkinofbrain.blackout.adapters.PartyAdapter;
import com.firkinofbrain.blackout.database.DataManager;
import com.firkinofbrain.blackout.database.party.Party;
import com.firkinofbrain.blackout.database.user.User;
import com.firkinofbrain.blackout.server.ServerManager;
import com.firkinofbrain.blackout.tools.AnimationManager;
import com.firkinofbrain.blackout.tools.HelpManager;
import com.firkinofbrain.blackout.R;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class FragmentProfile extends Fragment implements OnItemClickListener,
		OnLongClickListener {
	
	private ActivityMain activity;
	
	private DataManager dbManager;
	private List<Party> partyList;
	private PartyAdapter adapter;

	private static ServerManager sm;
	private AppBlackout app;
	private AnimationManager am;
	private Context context;

	private TextView tvStatus;
	private TextView tvName;
	private TextView tvCity;
	private TextView tvCountry;
	private TextView tvAge;
	private TextView tvWeight;
	private TextView tvSex;
	private ImageView avatar;

	private LinearLayout llWrap;
	private ListView lvHistory;
	private int maxTop;

	private String userid;
	private String status = "your status";

	private final static String CITY = "City:";
	private final static String COUNTRY = "Country:";
	private final static String AGE = "Age:";
	private final static String WEIGHT = "Weight:";
	private final static String SEX = "Sex: ";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
  
        View rootView = inflater.inflate(R.layout.activity_profile, container, false);
		
		activity = ((ActivityMain)getActivity());
		
		activity.setTitle("Profile");
		setHasOptionsMenu(true);

		sm = new ServerManager();
		app = (AppBlackout) activity.getApplicationContext();
		am = new AnimationManager(activity);
		dbManager = app.getDataBaseManager();
		context = activity.getApplicationContext();
		
		HelpManager hm = new HelpManager(activity);
		hm.getWindowHelp(HelpManager.PROFILE, "Profile", R.string.help_profile);
		
		avatar = (ImageView) rootView.findViewById(R.id.ivProfileAvatar);
		
		TextView tvTitle = (TextView) rootView.findViewById(R.id.tvProfileListViewTitle);
		llWrap = (LinearLayout) rootView.findViewById(R.id.llProfileListWrap);

		Display display = activity.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		lvHistory = (ListView) rootView.findViewById(R.id.lvProfileHistory);
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) lvHistory
				.getLayoutParams();
		RelativeLayout.LayoutParams rp = (RelativeLayout.LayoutParams) llWrap
				.getLayoutParams();
		rp.height = size.y - tvTitle.getLayoutParams().height - dpToPx(48);
		llWrap.setLayoutParams(rp);
		maxTop = 200;
		llWrap.setTop(maxTop);
		
		lp.height = size.y - dpToPx(120) - tvTitle.getLayoutParams().height;
		
		lvHistory.setLayoutParams(lp);
		
		lvHistory.setOnItemClickListener(this);
		lvHistory.setOnTouchListener(new ListViewScroll());

		tvName = (TextView) rootView.findViewById(R.id.tvLogin);
		
		tvCity = (TextView) rootView.findViewById(R.id.tvProfileCity);
		tvCity.setOnLongClickListener(this);
		tvCountry = (TextView) rootView.findViewById(R.id.tvProfileCountry);
		tvCountry.setOnLongClickListener(this);
		tvAge = (TextView) rootView.findViewById(R.id.tvProfileAge);
		tvAge.setOnLongClickListener(this);
		tvWeight = (TextView) rootView.findViewById(R.id.tvProfileWeight);
		tvWeight.setOnLongClickListener(this);
		tvSex = (TextView) rootView.findViewById(R.id.tvProfileSex);
		tvSex.setOnLongClickListener(this);

		tvStatus = (TextView) rootView.findViewById(R.id.tvUserStatus);
		
		Bundle b = getArguments();

		if (b == null) {
			userid = AppBlackout.USER_ID;
			partyList = dbManager.getAllParty();
			User user = dbManager.getUser();
			loadUserPreview(user);
			avatar.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					activity.startActivity(new Intent(context, ActivityAvatarCrop.class));
				}
				
			});
		} else {
			userid = b.getString(AppBlackout.I_USERID);
			partyList = sm.getParties(userid);
			updateUser();
		}
		
		adapter = new PartyAdapter(partyList, activity);
		lvHistory.setAdapter(adapter);
		
		
		return rootView;
	}
	
	public int pxToDp(int px) {
	    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
	    int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
	    return dp;
	}
	
	private int dpToPx(int dp) {
	    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
	    int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));       
	    return px;
	}
	
	private void loadUserPreview(User user){
		
		avatar.setImageDrawable(am.getAvatar(user.getAvatar()));

		tvName.setText(user.getName());
		
		tvCity.setText(user.getCity().equals("null") ? "set your city"
						: user.getCity());
		tvCountry
				.setText(user.getCountry().equals("null") ? "set your country"
								: user.getCountry());
		tvAge.setText(String.valueOf(user.getAge()));
		tvWeight.setText( String.valueOf(user.getWeight()));
		tvSex.setText(String.valueOf(user.getSex()));
	}
	
	private void updateUser() {

		new AsyncTask<Void, Void, User>() {

			@Override
			protected User doInBackground(Void... params) {
				User user = null;
				if (sm.isConnectingToInternet(activity)) {
					JSONObject obj = sm.getUserPreview(userid);
					
					try {
						user = new User();
						user.setUserId(obj.getString("unique_id"));
						user.setName(obj.getString("name"));
						user.setAvatar(obj.getString("avatar"));
						user.setCity(obj.getString("city"));
						user.setCountry(obj.getString("country"));
						user.setAge(Integer.parseInt(obj.getString("age")));
						user.setWeight(Integer.parseInt(obj.getString("weight")));
						user.setSex(obj.getString("sex"));
						
						if(dbManager.getUser().getUserId().equals(user.getUserId()))
							dbManager.updateUser(user);

						status = obj.getString("status") ;
						status = status == "null" ? "Set status":status;
					} catch (JSONException e) {
						e.printStackTrace();
					}

				} else {
					
				}

				return user;
			}

			@Override
			protected void onPostExecute(User user) {
				tvStatus.setText(status);
				((ActivityMain)activity).setAvatar(user.getAvatar());
				loadUserPreview(user);
			}

		}.execute();

	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	    inflater.inflate(R.menu.menu_profile, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.action_status:
			(new StatusDialog(activity)).show();
			break;
		case R.id.action_updateuser:
			updateUser();
			break;
		case android.R.id.home:
			activity.startActivity(new Intent(activity, ActivityMain.class));
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
		app.setHashToView(partyList.get(pos).getPartyID());
		activity.startActivity(new Intent(activity, ActivityHistoryDetails.class)
				.putExtra(AppBlackout.I_PARTYID, partyList.get(pos)
						.getPartyID()));
	}

	@Override
	public boolean onLongClick(View v) {

		switch (v.getId()) {
		case R.id.tvProfileCity:
			(new UpdateDialog(activity, CITY, ((TextView) v).getText().toString()))
					.show();
			break;
		case R.id.tvProfileCountry:
			(new UpdateDialog(activity, COUNTRY, ((TextView) v).getText()
					.toString())).show();
			break;
		case R.id.tvProfileAge:
			(new UpdateDialog(activity, AGE, ((TextView) v).getText().toString()))
					.show();
			break;
		case R.id.tvProfileWeight:
			(new UpdateDialog(activity, WEIGHT, ((TextView) v).getText().toString()))
					.show();
			break;
		}

		return false;
	}
	
	private class ListViewScroll implements ListView.OnTouchListener {

		float startY = 0;
		@Override
		public boolean onTouch(View v, MotionEvent event) {

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				startY = event.getY();
				break;
			case MotionEvent.ACTION_UP:
				break;
			case MotionEvent.ACTION_MOVE:
				
				
				float scrollY = startY - event.getY();
				startY = event.getY();
				double wrapTop = (llWrap.getTop() - scrollY * 0.8);
				
				if (scrollY > 0) {
					llWrap.setTop((int) Math.max(0, wrapTop));
				} else if (scrollY < 0 && lvHistory.getFirstVisiblePosition() == 0) {
					llWrap.setTop((int) Math.min(maxTop, wrapTop));
					return false;
				}
				
				break;
			}
			
			lvHistory.onTouchEvent(event);
			return true;
		}

	}

	private class StatusDialog extends AlertDialog {

		protected StatusDialog(Context context) {
			super(context);

			final LayoutInflater inflater = getLayoutInflater();
			View v = inflater.inflate(R.layout.dialog_status, null);
			setView(v);

			final EditText text = (EditText) v
					.findViewById(R.id.etDialogStatusText);

			v.findViewById(R.id.bDialogStatusConfirm).setOnClickListener(
					new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							final String status = text.getText().toString();

							new AsyncTask<Void, Void, Void>() {

								@Override
								protected Void doInBackground(Void... params) {
									sm.setStatus(status);
									return null;
								}
							}.execute();

							dismiss();
						}

					});
		}

	}

	protected class UpdateDialog extends AlertDialog {

		protected UpdateDialog(Context context, final String title, String hint) {
			super(context);

			final LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = inflater.inflate(R.layout.dialog_updateperson, null);
			setView(v);

			final TextView tvTitle = (TextView) v
					.findViewById(R.id.tvDialogUpdateTitle);
			tvTitle.setText(title);
			final EditText data = (EditText) v
					.findViewById(R.id.etDialogUpdatePerson);
			data.setHint(hint);

			v.findViewById(R.id.bDialogUpdateConfirm).setOnClickListener(
					new View.OnClickListener() {

						@Override
						public void onClick(View v) {

							new AsyncTask<Void, Void, Void>() {

								String changed;
								String city;
								String country;
								String age;
								String weight;

								@Override
								protected void onPreExecute() {
									changed = data.getText().toString();
									city = tvCity.getText().toString();
									country = tvCountry.getText().toString();
									age = tvAge.getText().toString();
									weight = tvWeight.getText().toString();

									if (title.equals(CITY)) {
										tvCity.setText(changed);
										city = changed;
									} else if (title.equals(COUNTRY)) {
										tvCountry.setText(changed);
										country = changed;
									} else if (title.equals(AGE)) {
										tvAge.setText(changed);
										age = changed;
									} else if (title.equals(WEIGHT)) {
										tvWeight.setText(changed);
										weight = changed;
									}

									Log.i(AppBlackout.TAG, "Changed: "
											+ changed);
								}

								@Override
								protected Void doInBackground(Void... params) {
									sm.updateUser(city, country, age, weight);
									return null;
								}

							}.execute();

							dismiss();
						}

					});

		}

	}

}
