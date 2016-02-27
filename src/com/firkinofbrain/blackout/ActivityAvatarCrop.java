package com.firkinofbrain.blackout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.firkinofbrain.blackout.server.ServerManager;
import com.firkinofbrain.blackout.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;

public class ActivityAvatarCrop extends Activity {

	private static final int FILE_MANAGER_INTENT = 1;
	private ImageView ivPhoto, ivCrop;
	private Button bConfirm, bCancel;

	private boolean CROPPER_MOVE = false;
	PointF down = new PointF();
	PointF start = new PointF();

	private Point screen = new Point();
	private PointF sizeCrop, sizeBg;
	private float bgScale;

	private Uri uri = null;

	private ServerManager sm;
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sm = new ServerManager();

		setContentView(R.layout.activity_avatarcrop);

		getWindowManager().getDefaultDisplay().getSize(screen);
		context = this.getApplicationContext();

		ivPhoto = (ImageView) findViewById(R.id.ivAvatarCropPhotoView);
		ivCrop = (ImageView) findViewById(R.id.ivAvatarCropCropper);

		if (uri == null) {
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
			startActivityForResult(intent, FILE_MANAGER_INTENT);
		}

		Bitmap cropBmp = ((BitmapDrawable) ivCrop.getDrawable()).getBitmap();
		sizeCrop = new PointF(cropBmp.getWidth(), cropBmp.getHeight());
		getWindowManager().getDefaultDisplay().getSize(screen);

		ivCrop.setOnTouchListener(new OnTouchListener() {

			Matrix matrix = new Matrix();
			Matrix savedMatrix = new Matrix();

			// We can be in one of these 3 states
			static final int NONE = 0;
			static final int DRAG = 1;
			static final int ZOOM = 2;
			int mode = NONE;

			// Remember some things for zooming
			PointF start = new PointF();
			PointF mid = new PointF();
			float oldDist = 1f;
			String savedItemClicked;

			PointF pos = new PointF(0, 0);

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				ImageView view = (ImageView) v;
				// dumpEvent(event);

				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:
					savedMatrix.set(matrix);
					start.set(event.getX(), event.getY());
					mode = DRAG;
					break;
				case MotionEvent.ACTION_POINTER_DOWN:
					oldDist = spacing(event);
					if (oldDist > 10f) {
						savedMatrix.set(matrix);
						midPoint(mid, event);
						mode = ZOOM;
					}
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:

					mode = NONE;
					break;
				case MotionEvent.ACTION_MOVE:
					if (mode == DRAG) {
						matrix.set(savedMatrix);
						float x = event.getX() - start.x;
						float y = event.getY() - start.y;
						matrix.postTranslate(x, y);

					} else if (mode == ZOOM) {
						float newDist = spacing(event);
						if (newDist > 10f) {
							matrix.set(savedMatrix);
							float scale = newDist / oldDist;
							matrix.postScale(scale, scale, mid.x, mid.y);
						}
					}
					break;
				}

				float[] m = new float[9];
				matrix.getValues(m);

				pos.set(m[Matrix.MTRANS_X], m[Matrix.MTRANS_Y]);
				float cropW = sizeCrop.x * m[Matrix.MSCALE_X];
				float cropH = sizeCrop.y * m[Matrix.MSCALE_Y];
				if (pos.x > 0 && pos.x + cropW < screen.x)
					if (pos.y > 0 && pos.y + cropH < sizeBg.y)
						view.setImageMatrix(matrix);

				return true;
			}

			private void dumpEvent(MotionEvent event) {
				String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
						"POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
				int action = event.getAction();
				int actionCode = action & MotionEvent.ACTION_MASK;
				if (actionCode == MotionEvent.ACTION_POINTER_DOWN
						|| actionCode == MotionEvent.ACTION_POINTER_UP) {

				}

			}

			/** Determine the space between the first two fingers */
			private float spacing(MotionEvent event) {
				float x = event.getX(0) - event.getX(1);
				float y = event.getY(0) - event.getY(1);
				return FloatMath.sqrt(x * x + y * y);
			}

			/** Calculate the mid point of the first two fingers */
			private void midPoint(PointF point, MotionEvent event) {
				float x = event.getX(0) + event.getX(1);
				float y = event.getY(0) + event.getY(1);
				point.set(x / 2, y / 2);
			}

		});

		bConfirm = (Button) findViewById(R.id.bAvatarCropConfirm);
		bConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				float[] m = new float[9];
				ivCrop.getImageMatrix().getValues(m);

				float x = m[Matrix.MTRANS_X];
				float y = m[Matrix.MTRANS_Y];

				Log.i(AppBlackout.TAG, x + " xy " + y);
				Log.i(AppBlackout.TAG, "width " + ivPhoto.getWidth()
						+ " screen " + screen.x);
				Log.i(AppBlackout.TAG, "height " + ivPhoto.getHeight()
						+ " screen " + screen.y);

				int srcX = (int) (x - (ivPhoto.getWidth() - sizeBg.x) / 2);
				int srcY = (int) (y - (ivPhoto.getHeight() - sizeBg.y) / 2);
				Log.i(AppBlackout.TAG, srcX + " xSRCy " + srcY);

				int size = (int) (sizeCrop.x * m[Matrix.MSCALE_X]);

				// retrieve bitmap from imageview
				Bitmap photo = ((BitmapDrawable) ivPhoto.getDrawable())
						.getBitmap();
				// crop bitmap to rectangle (square here)
				photo = Bitmap.createBitmap(photo, srcX, srcY, size, size);
				// scale bitmap to new size
				size = 200;
				photo = Bitmap.createScaledBitmap(photo, size, size, false);
				photo.setHasAlpha(true);

				Canvas c = new Canvas(photo);
				Path p = new Path();
				p.moveTo(size / 2, 0);
				p.lineTo(size, size / 2);
				p.lineTo(size / 2, size);
				p.lineTo(0, size / 2);
				p.lineTo(size / 2, 0);

				c.clipPath(p, Region.Op.DIFFERENCE);
				c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

				// save photo
					String path = getAbsoluteFile("/BlackoutDiary/avatar/avatar.png", context);
					File photoFile = new File(path);

					try {
						if (!photoFile.exists()) {
							photoFile.getParentFile().mkdirs();
							photoFile.createNewFile();
						}
					} catch (IOException e) {
						Log.e(AppBlackout.TAG, "Could not create file " + e);
					}

					try {
						FileOutputStream fos = new FileOutputStream(photoFile);
						photo.compress(Bitmap.CompressFormat.PNG, 90, fos);
						fos.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

					new HttpConnect().execute(path);
					finish();
				}

		});

		bCancel = (Button) findViewById(R.id.bAvatarCropCancel);
		bCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}

		});
	}

	private String getAbsoluteFile(String relativePath, Context context) {
		String path = null;
	    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
	        path = context.getExternalFilesDir(null) + relativePath;
	    } else {
	        path = context.getFilesDir() + relativePath;
	    }
	    
	    return path;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case FILE_MANAGER_INTENT:
			if (resultCode == RESULT_OK) {
				uri = data.getData();
				try {
					Bitmap bmp = MediaStore.Images.Media.getBitmap(
							getContentResolver(), uri);

					float width, height;
					Display display = getWindowManager().getDefaultDisplay();
					Point size = new Point();
					display.getSize(size);

					bgScale = 1;

					if (bmp.getWidth() > bmp.getHeight()) {
						width = size.y;
						bgScale = width / bmp.getWidth();
						height = bgScale * bmp.getHeight();
					} else {
						height = size.y;
						bgScale = height / bmp.getHeight();
						width = bgScale * bmp.getWidth();
					}
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

					Log.i(AppBlackout.TAG,
							"BMP: " + bmp.getWidth() + " " + bmp.getHeight());
					Log.i(AppBlackout.TAG, "SCREEN: " + size.x + " " + size.y);
					Log.i(AppBlackout.TAG, "NEW BMP: " + width + " " + height);

					sizeBg = new PointF(width, height);
					bmp = Bitmap.createScaledBitmap(bmp, (int) width,
							(int) height, false);
					ivPhoto.setImageBitmap(bmp);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				finish();
			}

			break;

		}
	}

	private class HttpConnect extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... path) {

			sm.uploadAvatar(path[0]);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

		}

	}

}
