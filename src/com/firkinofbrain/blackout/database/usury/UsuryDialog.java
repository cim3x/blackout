package com.firkinofbrain.blackout.database.usury;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;

import com.firkinofbrain.blackout.AppBlackout;
import com.firkinofbrain.blackout.adapters.UsuryAdapter;
import com.firkinofbrain.blackout.database.DataManager;
import com.firkinofbrain.blackout.R;

public class UsuryDialog extends AlertDialog{

	public UsuryDialog(Context context) {
		super(context);
		
		final DataManager dbManager = ((AppBlackout)context.getApplicationContext()).getInstance().getDataBaseManager();
		final LayoutInflater inflater = this.getLayoutInflater();
		final View view = inflater.inflate(R.layout.dialog_add_usury, null);
		final EditText etAmount = (EditText)view.findViewById(R.id.etPrice);
		final EditText etWho = (EditText)view.findViewById(R.id.etWho);
		final CheckedTextView ctvBorrow = (CheckedTextView)view.findViewById(R.id.ctvDirect);
		ctvBorrow.setOnClickListener(checkedListener);
		
		Button bSave = (Button)view.findViewById(R.id.bDialogUsurySave);
		bSave.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				Usury usury = new Usury();
				usury.setPrice(etAmount.getText().toString());
				usury.setWho(etWho.getText()
						.toString());
				usury.setDirection(ctvBorrow.isChecked() ? 1 : 0);

				dbManager.saveUsury(usury);
				dismiss();
			}
		});
		
		Button bCancel = (Button)view.findViewById(R.id.bDialogUsuryCancel);
		bCancel.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				dismiss();
			}
			
		});
		
		setView(view);
	}
	
	public UsuryDialog(Context context, final UsuryAdapter adapter) {
		super(context);
		
		final DataManager dbManager = ((AppBlackout)context.getApplicationContext()).getInstance().getDataBaseManager();
		final LayoutInflater inflater = this.getLayoutInflater();
		final View view = inflater.inflate(R.layout.dialog_add_usury, null);
		final EditText etAmount = (EditText)view.findViewById(R.id.etPrice);
		final EditText etWho = (EditText)view.findViewById(R.id.etWho);
		final CheckedTextView ctvBorrow = (CheckedTextView)view.findViewById(R.id.ctvDirect);
		ctvBorrow.setOnClickListener(checkedListener);
		
		Button bSave = (Button)view.findViewById(R.id.bDialogUsurySave);
		bSave.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				Usury usury = new Usury();
				usury.setPrice(etAmount.getText().toString());
				usury.setWho(etWho.getText()
						.toString());
				usury.setDirection(ctvBorrow.isChecked() ? 1 : 0);

				dbManager.saveUsury(usury);
				adapter.update(dbManager.getAllUsury());
				dismiss();
			}
		});
		
		Button bCancel = (Button)view.findViewById(R.id.bDialogUsuryCancel);
		bCancel.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				dismiss();
			}
			
		});
		
		setView(view);
	}
	
	private View.OnClickListener checkedListener = new View.OnClickListener(){

		@Override
		public void onClick(View v) {
			CheckedTextView ctv = (CheckedTextView)v;
			ctv.setChecked(!ctv.isChecked());
		}

		
	};
	
}
