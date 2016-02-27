package com.firkinofbrain.blackout;

import com.firkinofbrain.blackout.tools.ReceiverNetworkState;
import com.firkinofbrain.blackout.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class FragmentNoCon extends Fragment{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.nointernet, container, false);
		
		final ActivityParty activity = (ActivityParty)getActivity();
		view.findViewById(R.id.tvRefreshInternet).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(ReceiverNetworkState.haveNetworkConnection(activity)){
					activity.notifyAdapter();
				}
			}
			
		});
		
		return view;
	}
	
	
	
}
