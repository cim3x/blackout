package com.firkinofbrain.blackout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.firkinofbrain.blackout.database.DataManager;
import com.firkinofbrain.blackout.database.notification.Not;
import com.firkinofbrain.blackout.database.party.Party;
import com.firkinofbrain.blackout.server.ServerManager;
import com.firkinofbrain.blackout.tools.HelpManager;
import com.firkinofbrain.blackout.tools.ReceiverNetworkState;
import com.firkinofbrain.blackout.R;

public class FragmentSetupNew extends Fragment implements OnClickListener {

	private boolean ready;
	private String name;

	private EditText etWhere;
	private AutoCompleteTextView etName;
	private Button bSetup;
	private ImageView ivIcon;

	private ServerManager sm;
	private DataManager dm;
	private HintAdapter adapter;
	private List<Not> list;
	private int chosenPartyId = -1;

	private AppBlackout app;
	private ActivityMain activity;

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		activity = ((ActivityMain)getActivity());
		View view = inflater.inflate(R.layout.activity_setupnew, container, false);
		activity.setTitle(getResources().getString(R.string.setupnew_header));
		
		ready = false;
		name = "";

		app = ((AppBlackout) activity.getApplicationContext()).getInstance();
		dm = app.getDataBaseManager();
		sm = new ServerManager();
		
		ivIcon = (ImageView) view.findViewById(R.id.ivSetupNewIcon);
		bSetup = (Button) view.findViewById(R.id.bSetupNew);
		bSetup.setOnClickListener(this);
		etName = (AutoCompleteTextView) view.findViewById(R.id.etSetupNewName);
		
		if(ReceiverNetworkState.haveNetworkConnection(activity))
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				list = sm.getPartiesToday();
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				adapter = new HintAdapter(activity, R.layout.row_partyhint,
						new ArrayList<Not>(list));
				etName.setAdapter(adapter);
				etName.showDropDown();
			}

		}.execute();
		
		etName.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int pos,
					long id) {
				Not not = list.get((int) id);
				etName.setText(not.getDesc());
				etWhere.setText(not.getWhere());
				chosenPartyId = (int) id;
				addUserToParty();
			}

		});

		etName.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				name = s.toString();
				if (name.equals("") || name == "") {
					ivIcon.setImageResource(R.drawable.ic_help);
					createNewParty();
					ready = false;
					etName.showDropDown();
				} else {
					ivIcon.setImageResource(R.drawable.ic_launcher);
					ready = true;

				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void onTextChanged(CharSequence seq, int arg1, int arg2,
					int arg3) {

			}

		});

		etWhere = (EditText) view.findViewById(R.id.etSetupNewWhere);
		etWhere.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				if(chosenPartyId > 0)
					createNewParty();
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}

		});
		
		HelpManager hm = new HelpManager(activity);
		hm.getWindowHelp(HelpManager.NEW, "Setup new party", R.string.help_new);
		
		return view;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.bSetupNew && ready) {
			if (ReceiverNetworkState.haveNetworkConnection(activity)) {
				SharedPreferences sp = PreferenceManager
						.getDefaultSharedPreferences(activity);
				final Editor edit = sp.edit();
				edit.putBoolean(FragmentSettings.SET_PARTYON, true);
				edit.commit();

				final Party party = new Party();
				party.setName(etName.getText().toString());
				party.setWhere(etWhere.getText().toString());
				Time date = new Time();
				date.setToNow();
				party.setTime(date);
				party.setSync(0);
				AppBlackout.USER_ID = sp.getString(AppBlackout.P_USERID, dm
						.getUser().getUserId());
				party.setUserID(AppBlackout.USER_ID);

				if (chosenPartyId < 0)
					new AsyncTask<Party, Void, JSONObject>() {

						@Override
						protected JSONObject doInBackground(Party... parties) {

							return sm.saveNewParty(parties[0], true);
						}

						@Override
						protected void onPostExecute(JSONObject json) {
							super.onPostExecute(json);

							try {
								int success = Integer.parseInt(json
										.getString("success"));
								if (success == 1) {
									String partyid = json.getString("partyid");
									party.setPartyID(partyid);
									dm.saveParty(party);
									AppBlackout.PARTY_NAME = party.getName();
									edit.putString(AppBlackout.P_PARTYID,
											partyid);
									edit.putString(AppBlackout.P_PARTYNAME,
											party.getName());
									Log.i(AppBlackout.TAG, party.toString());
									edit.commit();

									activity.startActivity(new Intent(activity, ActivityParty.class));

								}
							} catch (NumberFormatException e) {
								e.printStackTrace();
							} catch (JSONException e) {
								e.printStackTrace();
							}

						}

					}.execute(party);
				else {

					final String partyid = list.get(chosenPartyId).getItemid();
					new AsyncTask<String, Void, JSONObject>() {

						@Override
						protected JSONObject doInBackground(String... partiesid) {

							return sm.addUserToParty(partiesid[0],
									party.getUserID());
						}

						@Override
						protected void onPostExecute(JSONObject json) {
							super.onPostExecute(json);
							Log.i(AppBlackout.TAG, "Add user to party");
							try {
								int success = Integer.parseInt(json
										.getString("success"));
								if (success == 1) {

									party.setPartyID(partyid);
									if(dm.getParty(partyid) == null)
										dm.saveParty(party);
									AppBlackout.PARTY_NAME = party.getName();
									edit.putString(AppBlackout.P_PARTYID,
											partyid);
									edit.putString(AppBlackout.P_PARTYNAME,
											party.getName());
									Log.i(AppBlackout.TAG, party.toString());
									edit.commit();

									activity.startActivity(new Intent(activity, ActivityParty.class));

								}
							} catch (NumberFormatException e) {
								e.printStackTrace();
							} catch (JSONException e) {
								e.printStackTrace();
							}

						}

					}.execute(partyid);
				}
			}

		}
	}

	private void addUserToParty() {
		bSetup.setText("Join to party");
	}

	private void createNewParty() {
		bSetup.setText("Create new");
		chosenPartyId = -1;
		etName.showDropDown();
	}

	private class HintAdapter extends ArrayAdapter<Not> implements Filterable {

		List<Not> list;
		List<Not> suggestions;
		Context context;

		public HintAdapter(Context context, int layout, List<Not> list) {
			super(context, layout, list);

			this.list = list;
			this.suggestions = new ArrayList<Not>();
			this.context = context;
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
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;

			if (v == null) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.row_partyhint, null);
			}

			Not not = list.get(position);
			((TextView) v.findViewById(R.id.tvPartyHintName)).setText(not
					.getDesc());
			((TextView) v.findViewById(R.id.tvPartyHintInfo)).setText("in "
					+ not.getWhere() + " • by: " + not.getUserName());

			return v;
		}

		@Override
		public Filter getFilter() {
			return nameFilter;
		}

		Filter nameFilter = new Filter() {

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				if (constraint != null) {
					suggestions.clear();
					for (Not not : list) {
						if (not.getDesc()
								.toLowerCase()
								.startsWith(constraint.toString().toLowerCase())) {
							suggestions.add(not);
						}
					}
					FilterResults filterResults = new FilterResults();
					filterResults.values = suggestions;
					filterResults.count = suggestions.size();
					return filterResults;
				} else {
					return new FilterResults();
				}
			}

			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {

				ArrayList<Not> filteredList = (ArrayList<Not>) results.values;
				ArrayList<Not> NotList = new ArrayList<Not>();
				if (results != null && results.count > 0) {
					clear();

					try {
						for (Not c : filteredList) {
							NotList.add(c);
						}
					} catch (Exception e) {
						Log.e(AppBlackout.TAG,
								"Arrayadapter error" + e.getMessage());
					}

					Iterator<Not> NotIterator = NotList.iterator();
					while (NotIterator.hasNext()) {
						Not not = NotIterator.next();
						add(not);
					}
					notifyDataSetChanged();
				}
			}

		};

	}

}
