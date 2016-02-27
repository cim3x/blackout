package com.firkinofbrain.blackout;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.firkinofbrain.blackout.adapters.NavMenuAdapter;
import com.firkinofbrain.blackout.database.DataManager;
import com.firkinofbrain.blackout.database.user.User;
import com.firkinofbrain.blackout.navmenu.NavMenuItem;
import com.firkinofbrain.blackout.tools.AnimationManager;
import com.firkinofbrain.blackout.tools.NoConListener;
import com.firkinofbrain.blackout.R;

public class ActivityMain extends FragmentActivity implements NoConListener{

	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle drawerToggle;
	private ListView menuList;

	private String[] menuTitles;
	private TypedArray menuIcons;
	private List<NavMenuItem> menuItems;
	private NavMenuAdapter menuAdapter;
	private int lastPosition = 0;
	
	private CharSequence mTitle, mDrawerTitle;

	private AppBlackout app;
	private SharedPreferences sp;
	private DataManager dm;
	private AnimationManager am;

	private boolean LOGIN_STATE = false;
	private String partyName = "Party";

	private String no_conn;
	private String yes_conn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// INTERNET STATE

		app = ((AppBlackout) getApplicationContext()).getInstance();
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		dm = app.getDataBaseManager();
		am = new AnimationManager(this.getApplicationContext());

		no_conn = this.getResources().getString(R.string.noconnection);
		yes_conn = this.getResources().getString(R.string.yesconnection);

		LOGIN_STATE = AppBlackout.LOGIN = dm.isUser();

		if (!LOGIN_STATE)
			startActivity(new Intent(this, ActivityWelcome.class));

		User user = dm.getUser();
		sp.edit().putString(AppBlackout.P_USERID, user.getUserId()).commit();
		
		if(sp.getBoolean(FragmentSettings.SET_PARTYON, false))
			partyName = sp.getString(AppBlackout.P_PARTYNAME, partyName);

		// MENU LIST HERE
		menuTitles = getResources().getStringArray(R.array.nav_items);
		menuIcons = getResources().obtainTypedArray(R.array.nav_drawables);

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		menuList = (ListView) findViewById(R.id.lvSlidermenu);
		
		menuItems = new ArrayList<NavMenuItem>();
		menuItems.add(new NavMenuItem(user.getName(), am.getAvatar(user
				.getAvatar()), true));
		menuItems.add(new NavMenuItem(partyName, menuIcons.getResourceId(1,
				-1)));
		menuItems.add(new NavMenuItem(menuTitles[2], menuIcons.getResourceId(2,
				-1)));
		menuItems.add(new NavMenuItem(menuTitles[3], menuIcons.getResourceId(3,
				-1)));
		menuItems.add(new NavMenuItem(menuTitles[4], menuIcons.getResourceId(4,
				-1)));
		menuItems.add(new NavMenuItem(menuTitles[5], menuIcons.getResourceId(5,
				-1)));
		menuItems.add(new NavMenuItem(menuTitles[6], menuIcons.getResourceId(6,
				-1)));
		menuItems.add(new NavMenuItem(menuTitles[7], menuIcons.getResourceId(7,
				-1)));

		menuIcons.recycle();

		menuList.setOnItemClickListener(new ListView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				displayView(position);
			}

		});

		menuAdapter = new NavMenuAdapter(this, menuItems);
		menuList.setAdapter(menuAdapter);

		// ACTION BAR HERE
		ActionBar actionBar = getActionBar();
		actionBar.setIcon(new ColorDrawable(getResources().getColor(
				android.R.color.transparent)));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);

		mTitle = mDrawerTitle = getTitle();

		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
				R.drawable.ic_menu_nav, R.string.app_name, R.string.app_name) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu();
			}
		};
		drawerLayout.setDrawerListener(drawerToggle);
		
		//First open activity
		if (savedInstanceState == null) {
			if(getIntent().getExtras() != null){
				displayView(0);
				toggleMenu();
			}else
				displayView(1);
		}
	}

	private void displayView(int i) {
		Log.i(AppBlackout.TAG, "displayview");
		Fragment fragment = null;
		lastPosition = i;
		switch (i) {
		case 0:
			fragment = new FragmentProfile();
			break;
		case 1:
			
			if(sp.getBoolean(FragmentSettings.SET_PARTYON, false)){
				startActivity(new Intent(this, ActivityParty.class));
			}else{
				fragment = new FragmentSetupNew();
			}
			
			break;
		case 2:
			startActivity(new Intent(this, ActivityPhotoGallery.class));
			break;
		case 3:
			fragment = new FragmentNotManager();
			break;
		case 4:
			fragment = new FragmentUsuryManager();
			break;
		case 5:
			startActivity(new Intent(this, ActivitySearch.class));
			break;
		case 6:
			fragment = new FragmentSettings();
			break;
		case 7:
			fragment = new FragmentHelp();
			break;
		}
		
		if (fragment != null) {
			FragmentManager fm = getSupportFragmentManager();
			fm.beginTransaction().replace(R.id.flContainer, fragment).commit();
			drawerLayout.closeDrawer(menuList);
		}
		
		BACK_PRESSED = 0;
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!AppBlackout.LOGIN) {
			startActivity(new Intent(this, ActivityWelcome.class));
		}
	}
	
	private int BACK_PRESSED = 0;
	@Override
	public void onBackPressed() {
		BACK_PRESSED++;
		if(BACK_PRESSED > 1){
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}else{
			Toast.makeText(this, "To exit an app click once more", Toast.LENGTH_SHORT).show();
		}
	}

	public void noConnection(){
		Fragment fragment = new FragmentNoCon();
		this.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).commit();
	}
	
	@Override
	public void notifyAdapter(){
		displayView(menuList.getSelectedItemPosition());
	}
	
	public void notifyAdapterDataChanged(){
		menuAdapter.notifyDataSetChanged();
	}
	
	public void changePartyName(){
		if(!sp.getBoolean(FragmentSettings.SET_PARTYON, false)){
			menuItems.get(1).setTitle("Party");
			notifyAdapterDataChanged();
		}
	}
	
	public void toggleMenu(){
		drawerLayout.openDrawer(menuList);
	}

	public void setAvatar(String avatar) {
		menuItems.get(0).setDrawable(am.getAvatar(avatar));
		notifyAdapterDataChanged();
	}
}
