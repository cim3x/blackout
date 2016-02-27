package com.firkinofbrain.blackout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.firkinofbrain.blackout.database.DataManager;
import com.firkinofbrain.blackout.database.event.EventType;
import com.firkinofbrain.blackout.database.event.Events;
import com.firkinofbrain.blackout.database.tag.Tag;
import com.firkinofbrain.blackout.server.ServerManager;
import com.firkinofbrain.blackout.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class ActivityPhotoPreview extends Activity implements OnClickListener {

	private static final int REQUEST_IMAGE_CAPTURE = 1;
	private String filename, date, time;
	private long timeMilis;
	private double alcohol;
	private String path;
	private String partyid, userid;

	private ImageView ivPreview;
	private RelativeLayout wrapper;

	private boolean NO_TAG = true;

	private AppBlackout app;
	private Activity context;

	private List<ViewGroup> rowList;
	private List<Tag> tagList;
	private Point screen, photo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		this.context = this;
		app = (AppBlackout) getApplicationContext();
		screen = new Point();
		photo = new Point();
		getWindowManager().getDefaultDisplay().getSize(screen);

		setContentView(R.layout.activity_camera);

		String[] extras = this.getIntent().getExtras()
				.getStringArray(AppBlackout.I_FILENAME);
		filename = extras[0];
		date = extras[1];
		time = extras[2];
		alcohol = Double.parseDouble(extras[3]);

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		AppBlackout.PARTY_ID = partyid = sp.getString(AppBlackout.P_PARTYID,
				"partyid");
		AppBlackout.USER_ID = userid = sp.getString(AppBlackout.P_USERID,
				"userid");

		tagList = new ArrayList<Tag>();
		rowList = new ArrayList<ViewGroup>();

		wrapper = (RelativeLayout) findViewById(R.id.rlPhotoWrapper);
		ivPreview = (ImageView) findViewById(R.id.ivPhotoPreview);

		ivPreview.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:

					int x = (int) (event.getRawX());
					int y = (int) (event.getRawY());

					NO_TAG = true;
					for (int i = 0; i < rowList.size() && NO_TAG; i++) {
						View rowTag = rowList.get(i);
						RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rowTag
								.getLayoutParams();
						if (x >= lp.leftMargin && x <= lp.leftMargin + lp.width)
							if (y >= lp.topMargin
									&& y <= lp.topMargin + lp.height - 100)
								NO_TAG = false;
					}

					if (NO_TAG) {

						Tag tag = new Tag();
						tag.setPhotoID(app.getPhotoId(filename));
						tag.setX(x * photo.x / screen.x);
						tag.setY(y * photo.y / screen.y);

						LinearLayout llWrap = new LinearLayout(context);

						RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT);
						lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
						lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
						lp.topMargin = y;
						lp.leftMargin = x;
						llWrap.setBackgroundColor(context.getResources()
								.getColor(R.color.customTransBg));
						llWrap.setOrientation(LinearLayout.HORIZONTAL);
						llWrap.setLayoutParams(lp);

						ImageView ivIcon = new ImageView(context);
						ivIcon.setBackgroundResource(R.drawable.ic_menu_tag);
						ivIcon.setScaleType(ScaleType.FIT_XY);
						LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
								48, 48);
						ivIcon.setLayoutParams(llp);
						llWrap.addView(ivIcon, 0);

						EditText etName = new EditText(context);
						llp = new LinearLayout.LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT);
						llp.width = 200;
						etName.setLayoutParams(llp);
						etName.setTextColor(context.getResources().getColor(
								R.color.customWhite));
						etName.setOnLongClickListener(new OnLongClickListener() {

							@Override
							public boolean onLongClick(View v) {
								wrapper.removeView(v);
								int ind = rowList.indexOf(v);
								rowList.remove(ind);
								tagList.remove(ind);

								return true;
							}

						});
						llWrap.addView(etName, 1);

						tagList.add(tag);
						rowList.add(llWrap);
						wrapper.addView(llWrap);
					}

					break;
				}

				return false;
			}

		});

		pictureIntent();
		findViewById(R.id.bCameraSave).setOnClickListener(this);
		findViewById(R.id.bCameraCancel).setOnClickListener(this);
	}

	private String getAbsoluteFile(String relativePath, Context context) {
		String path = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			path = context.getExternalFilesDir(null) + relativePath;
		} else {
			path = context.getFilesDir() + relativePath;
		}

		return path;
	}

	private void pictureIntent() {

		path = getAbsoluteFile("/BlackoutDiary/photos/" + filename, context.getApplicationContext());
		File photoFile = new File(path);
		try {
			if (!photoFile.exists()) {
				photoFile.getParentFile().mkdirs();
				photoFile.createNewFile();
			}
		} catch (IOException e) {
			Log.e(AppBlackout.TAG, "Could not create file " + e);
		}
		Log.i(AppBlackout.TAG, path);
		Intent imgIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		imgIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
		startActivityForResult(imgIntent, REQUEST_IMAGE_CAPTURE);

		galleryAddPic(path);
	}

	private void galleryAddPic(String imagePath) {
		Intent mediaScanIntent = new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File f = new File(imagePath);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		this.sendBroadcast(mediaScanIntent);
	}

	private void setPic() {

		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, bmOptions);
		photo.x = bmOptions.outWidth;
		photo.y = bmOptions.outHeight;

		int scaleFactor = Math.min(photo.x / screen.x, photo.y / screen.y);

		// Decode the image file into a Bitmap sized to fill the screen
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
		ivPreview.setImageBitmap(bitmap);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_IMAGE_CAPTURE:
			if (resultCode == Activity.RESULT_OK) {
				setPic();
			}

		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.bCameraSave:

			DataManager db = app.getDataBaseManager();
			for (int i = 0; i < tagList.size(); i++) {
				EditText etName = (EditText) rowList.get(i).getChildAt(1);
				tagList.get(i).setDesc(etName.getText().toString());
			}
			db.saveTagList(tagList);
			Events event = new Events();
			event.setType(EventType.PHOTO);
			event.setDescription(filename);
			timeMilis = System.currentTimeMillis();
			event.setDate(String.valueOf(timeMilis));
			event.setTime(time);
			event.setAlcohol(alcohol);
			event.setPartyID(partyid);
			event.setUserID(userid);
			db.saveEvent(event);

			showDialog();
			break;
		case R.id.bCameraCancel:
			new File(path).delete();
			startActivity(new Intent(this, ActivityParty.class));
			break;
		}
	}

	private void showDialog() {

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(
				context.getResources().getLayout(R.layout.dialog_photosend),
				null);

		wrapper.removeAllViews();
		wrapper.addView(view);

		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.CENTER_IN_PARENT);
		view.setLayoutParams(lp);

		final ImageView ivDest = (ImageView) view
				.findViewById(R.id.ivDialogPhotoDest);

		final ImageView photo = (ImageView) view
				.findViewById(R.id.ivDialogPhoto);
		wrapper.setOnTouchListener(new OnTouchListener() {

			PointF click = new PointF();
			PointF start = new PointF();
			boolean DOWN = false;

			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					click.x = event.getRawX();
					click.y = event.getRawY();

					int[] loc = new int[2];
					photo.getLocationOnScreen(loc);
					start.x = loc[0];
					start.y = loc[1];

					if (click.x > start.x
							&& click.x < start.x + photo.getWidth())
						if (click.y > start.y
								&& click.y < start.y + photo.getHeight())
							DOWN = true;

					break;
				case MotionEvent.ACTION_MOVE:

					if (DOWN)
						photo.setX(start.x + event.getRawX() - click.x);
					break;
				case MotionEvent.ACTION_UP:

					click.x = event.getRawX();
					click.y = event.getRawY();

					int[] dst = new int[2];
					ivDest.getLocationOnScreen(dst);

					Log.i(AppBlackout.TAG, "TOuch event: " + click.x + " "
							+ click.y);
					Log.i(AppBlackout.TAG, "TOuch event: " + photo.getWidth()
							+ " " + photo.getHeight());
					Log.i(AppBlackout.TAG, "TOuch event: " + start.x + " "
							+ start.y);
					Log.i(AppBlackout.TAG, "TOuch event: " + DOWN);

					if (click.x > start.x
							&& click.x < start.x + photo.getWidth()
							&& click.y > start.y
							&& click.y < start.y + photo.getHeight()) {

						context.startActivity(new Intent(context,
								ActivityParty.class));

					} else if (click.x > dst[0]
							&& click.x < dst[0] + ivDest.getWidth()
							&& click.y > dst[1]
							&& click.y < dst[1] + ivDest.getHeight()) {

						new AsyncTask<Void, Void, Void>() {

							ServerManager sm;

							@Override
							protected void onPreExecute() {
								super.onPreExecute();
								sm = new ServerManager();
							}

							@Override
							protected Void doInBackground(Void... voids) {

								JSONObject json = sm.uploadPhoto(path,
										timeMilis);

								try {
									int success = Integer.parseInt(json
											.getString("success"));
									if (success == 1) {
										String photoid = json
												.getString("photoid");
										AppBlackout app = (AppBlackout) context
												.getApplicationContext();
										String localid = app
												.getPhotoId(filename);
										DataManager dm = app
												.getDataBaseManager();

										List<Tag> tags = dm
												.getAllTagByPhoto(localid);
										for (int i = 0; i < tags.size(); i++) {
											sm.upadateTag(tags.get(i), photoid);
										}

									}
								} catch (NumberFormatException e) {
									e.printStackTrace();
								} catch (JSONException e) {
									e.printStackTrace();
								}

								return null;
							}

							@Override
							protected void onPostExecute(Void result) {
								super.onPostExecute(result);

								Notification.Builder builder = new Notification.Builder(
										context)
										.setLargeIcon(
												BitmapFactory.decodeResource(
														context.getResources(),
														R.drawable.ic_not_upload))
										.setAutoCancel(true)
										.setContentTitle("Photo was uploaded")
										.setContentText("Blackout Diary");

								Notification noti;

								// Check API level
								if (android.os.Build.VERSION.SDK_INT > 15)
									noti = builder.build();
								else
									noti = builder.getNotification();

								int NOTIFICATION_ID = R.string.dialog_photosend;
								NotificationManager nm = (NotificationManager) context
										.getSystemService(Context.NOTIFICATION_SERVICE);
								nm.notify(NOTIFICATION_ID, noti);
							}
						}.execute();

						context.startActivity(new Intent(context,
								ActivityParty.class));
					} else {
						photo.setX(0);
						photo.setY(0);
						DOWN = false;
					}

					break;
				}

				return true;
			}
		});

	}

}
