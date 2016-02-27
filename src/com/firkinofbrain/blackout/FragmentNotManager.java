package com.firkinofbrain.blackout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.firkinofbrain.blackout.adapters.NotAdapter;
import com.firkinofbrain.blackout.database.DataManager;
import com.firkinofbrain.blackout.database.notification.Not;
import com.firkinofbrain.blackout.database.notification.Not.NotType;
import com.firkinofbrain.blackout.database.party.Party;
import com.firkinofbrain.blackout.server.ServerManager;
import com.firkinofbrain.blackout.tools.HelpManager;
import com.firkinofbrain.blackout.R;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;

public class FragmentNotManager extends Fragment implements OnItemClickListener {

	private FragmentActivity activity;

	private ActionBar actionBar;

	private ListView lvNots;
	private Context context;
	private ServerManager sm;
	private DataManager dm;
	private AppBlackout app;

	private List<Not> nots;
	private NotAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		activity = getActivity();
		((ActivityMain) activity).setTitle("Notifications");
		View view = inflater.inflate(R.layout.activity_notemanager, container,
				false);

		app = ((AppBlackout) activity.getApplicationContext()).getInstance();
		sm = new ServerManager();
		Time time = new Time();
		time.setToNow();

		HelpManager hm = new HelpManager(activity);
		hm.getWindowHelp(HelpManager.NOTS, "Notifications", R.string.help_nots);

		nots = new ArrayList<Not>();
		adapter = new NotAdapter(activity.getApplicationContext(), nots);

		lvNots = (ListView) view.findViewById(R.id.lvNoteNotifications);
		lvNots.setAdapter(adapter);
		lvNots.setOnItemClickListener(this);

		// ActionBar
		setHasOptionsMenu(true);

		if (sm.isConnectingToInternet(activity)) {
			loadData();
		} else {
			noInternet();
		}

		return view;
	}

	private void noInternet() {
		((ActivityMain) activity).noConnection();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_notifications, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.action_addnew:

			if (sm.isConnectingToInternet(activity)) {
				(new NewNotMenuDialog(activity)).show();
			} else {
				noInternet();
			}
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void loadData() {
		if (sm.isConnectingToInternet(activity))
			new HttpConnect().execute();
		else {
			noInternet();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
		Log.i(AppBlackout.TAG, "Item clicked 128");
		if (sm.isConnectingToInternet(activity)) {
			
			final Not not = nots.get(pos);
			
			if (not.getId() > 0 && not.getRead() == 0) {
				Log.i(AppBlackout.TAG, "Item clicked 134");
				not.setRead(1);
				view.setBackgroundColor(this.getResources().getColor(
						R.color.customBlack));

				new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						sm.updateNotify(not.getId(), not.getRead());
						return null;
					}

				}.execute();
				adapter.notifyDataSetChanged();
			}
		} else {
			noInternet();
		}
	}

	private class HttpConnect extends AsyncTask<Void, Void, Void> {

		private int unreadNots = 0;

		@Override
		protected Void doInBackground(Void... params) {

			JSONArray json = sm.getNotifications(AppBlackout.USER_ID);

			for (int i = 0; i < json.length(); i++) {
				Not not = new Not();
				JSONObject obj;
				try {
					obj = json.getJSONObject(i);
					not.setId(Long.parseLong(obj.getString("id")));
					not.setType(obj.getString("type"));
					not.setDesc(obj.getString("note"));
					not.setWhere(obj.getString("place"));
					not.setWhen(obj.getString("date"));
					not.setUserName(obj.getString("username"));
					not.setRead(Integer.parseInt(obj.getString("seen")));
					Log.i(AppBlackout.TAG, "Seen: " + obj.getString("seen"));
				} catch (JSONException e) {
					e.printStackTrace();
				}

				nots.add(not);
				if (!not.isRead()) {
					unreadNots++;
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
			Collections.sort(nots, new Comparator<Not>(){

				@Override
				public int compare(Not n1, Not n2) {
					String d1 = n2.getWhen();
					String d2 = n1.getWhen();
					return d1.compareTo(d2);
				}
				
			});
			adapter.notifyDataSetChanged();

		}

	}

	private class NewNotMenuDialog extends AlertDialog {

		protected NewNotMenuDialog(final Context context) {
			super(context);

			final LayoutInflater inflater = this.getLayoutInflater();
			final View view = inflater
					.inflate(R.layout.dialog_newnotmenu, null);

			((Button) view.findViewById(R.id.bNewNotParty))
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							(new NewNotPartyDialog(context)).show();
							dismiss();
						}

					});

			((Button) view.findViewById(R.id.bNewNotSearch))
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							(new NewNotSearchDialog(context)).show();
							dismiss();
						}

					});

			setView(view);
		}

	}

	private class NewNotSearchDialog extends AlertDialog {

		protected NewNotSearchDialog(Context context) {
			super(context);

			final LayoutInflater inflater = this.getLayoutInflater();
			final View view = inflater.inflate(R.layout.dialog_newnotparty,
					null);

			final Time time = new Time();
			time.setToNow();

			final CalendarView cvDate = (CalendarView) view
					.findViewById(R.id.cvDialogNotWhen);
			cvDate.setDate(time.toMillis(true));

			((Button) view.findViewById(R.id.bDialogNewNotConfirm))
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {

							String where = ((EditText) view
									.findViewById(R.id.etDialogNotWhere))
									.getText().toString();

							final Not not = new Not();
							not.setType(NotType.SEARCH);
							not.setWhere(where);
							time.set(cvDate.getDate());
							not.setWhen(Party.getTimeYMD(time));
							not.setUserName(AppBlackout.USER_NAME);

							if (sm.isConnectingToInternet(activity))
								new AsyncTask<Void, Void, Void>() {

									@Override
									protected Void doInBackground(
											Void... params) {
										sm.newNotification(not);
										return null;
									}

								}.execute();
							else {
								noInternet();
							}

							dismiss();
						}

					});

			setView(view);
		}

	}

	private class NewNotPartyDialog extends AlertDialog {

		private String date;

		protected NewNotPartyDialog(final Context context) {
			super(context);

			final Time time = new Time();
			time.setToNow();

			final LayoutInflater inflater = this.getLayoutInflater();
			final View view = inflater.inflate(R.layout.dialog_newnotparty,
					null);

			final CalendarView cvDate = (CalendarView) view
					.findViewById(R.id.cvDialogNotWhen);
			cvDate.setDate(time.toMillis(true));

			((Button) view.findViewById(R.id.bDialogNewNotConfirm))
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							String name = ((EditText) view
									.findViewById(R.id.etDialogNotName))
									.getText().toString();
							String where = ((EditText) view
									.findViewById(R.id.etDialogNotWhere))
									.getText().toString();

							final Party entity = new Party();
							entity.setName(name);
							entity.setWhere(where);
							time.set(cvDate.getDate());
							entity.setWhen(Party.getTimeYMD(time));
							entity.setUserID(AppBlackout.USER_ID);

							if (sm.isConnectingToInternet(activity))
								new AsyncTask<Void, Void, Void>() {

									@Override
									protected Void doInBackground(
											Void... params) {
										sm.saveNewParty(entity, false);
										return null;
									}

								}.execute();
							else {
								noInternet();
							}
							dismiss();
						}
					});

			this.setView(view);
		}
	}
}
