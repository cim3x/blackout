package com.firkinofbrain.blackout;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.firkinofbrain.blackout.database.DataManager;
import com.firkinofbrain.blackout.database.geo.Geo;
import com.firkinofbrain.blackout.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ActivityMapShow extends FragmentActivity{
	
	private GoogleMap map;	
	private DataManager dbManager;
	private AppBlackout app;
	private String partyid;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_mapshow);
        
        getActionBar().hide();
        
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setMyLocationEnabled(true);
       
        app = ((AppBlackout)this.getApplicationContext()).getInstance();
        dbManager = app.getDataBaseManager();
        
        Bundle b = getIntent().getExtras();
        if(b != null){
        	partyid = b.getString(AppBlackout.I_PARTYID);
        }
        List<Geo> listLoc = dbManager.getAllGeoByHash(partyid);
        
        
        //add Markers till not end of events array
        LatLng loc = new LatLng(50.064192,19.942138);
        for(int i = 0; i < listLoc.size();i++){
        	Geo geo = listLoc.get(i);
        	loc = new LatLng(geo.getX(), geo.getY());
        	MarkerOptions marker = new MarkerOptions().position(loc);
        	
        	map.addMarker(new MarkerOptions()
        		.position(loc)
        		.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_not_pin_blue))
        	);
        	
        	Log.i(AppBlackout.TAG, geo.getViewString());
        }
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
	}
	
}

