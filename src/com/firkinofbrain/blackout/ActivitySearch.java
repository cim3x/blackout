package com.firkinofbrain.blackout;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.firkinofbrain.blackout.server.ServerManager;
import com.firkinofbrain.blackout.tools.NoConListener;
import com.firkinofbrain.blackout.R;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.SearchView.OnQueryTextListener;

public class ActivitySearch extends FragmentActivity implements NoConListener{
	
	private FragmentSearch fragment;
	private FragmentTransaction ft;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_search);
		
		if (savedInstanceState == null) {
            fragment = new FragmentSearch();
            ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.FragmentSearch, fragment).commit();
        }
		
	}
	
	private void setFragment(Fragment f){
		ft.replace(R.id.FragmentSearch, f);
	}

	@Override
	public void notifyAdapter() {
		setFragment(fragment);
	}
}
