package com.firkinofbrain.blackout.tools;

import com.firkinofbrain.blackout.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class HelpManager {
	
	private Context context;
	private SharedPreferences sp;
	
	public static final String LOCKPAD = "help_lockpad";
	public static final String NOTS = "help_nots";
	public static final String PARTYLOG = "help_partylog";
	public static final String PROFILE = "help_profile";
	public static final String NEW = "help_new";
	public static final String LOAN = "help_loan";
	
	public HelpManager(Context context){
		this.context = context;
		sp = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
	}
	
	public void getWindowHelp(String window, String title, int resInfo){
		if(sp.getBoolean(window, true)){
			new HelpDialog(context, title, resInfo, window).show();
		}
	}
	
	public void turnOffWindowHelp(String window){
		sp.edit().putBoolean(window, false).commit();
	}
	
	public void turnOnHelp(){
		Editor edit = sp.edit();
		edit.putBoolean(LOCKPAD, true);
		edit.putBoolean(NOTS, true);
		edit.putBoolean(PARTYLOG, true);
		edit.putBoolean(PROFILE, true);
		edit.putBoolean(NEW, true);
		edit.putBoolean(LOAN, true);
		
		edit.commit();
	}
	
	public void turnOffHelp(){
		Editor edit = sp.edit();
		edit.putBoolean(LOCKPAD, false);
		edit.putBoolean(NOTS, false);
		edit.putBoolean(PARTYLOG, false);
		edit.putBoolean(PROFILE, false);
		edit.putBoolean(NEW, false);
		edit.putBoolean(LOAN, false);
		
		edit.commit();
	}
	
	public class HelpDialog extends AlertDialog{
		
		protected HelpDialog(Context context, String title, int resInfo, final String window) {
			super(context);

			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.dialog_help, null);
			setView(view);
			
			((TextView)view.findViewById(R.id.tvDialogHelpTitle)).setText(title);
			((TextView)view.findViewById(R.id.tvDialogHelpContent)).setText(context.getResources().getString(resInfo));
			view.findViewById(R.id.bDialogHelpButton).setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View v) {
					
					turnOffWindowHelp(window);
					dismiss();
				}
				
			});
		}
		
	}
	
	
}
