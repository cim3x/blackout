package com.firkinofbrain.blackout;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.firkinofbrain.blackout.R;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class FragmentPhotoFeed extends Fragment {
	
	private String photoid;
	private ActivityPhotoGallery activity;
	private TextView legend;
	private boolean like = false;
	private int position = 0;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_photofeed, container,
				false);
		
		activity = (ActivityPhotoGallery)getActivity();
		legend = (TextView) view.findViewById(R.id.tvGalleryLegend);
		final ImageView ivPhoto = (ImageView) view.findViewById(R.id.ivGalleryView);
		Point screen = new Point();
		getActivity().getWindowManager().getDefaultDisplay().getSize(screen);
		
		final int dstWidth = screen.x, dstHeight = screen.y;
		Bundle args = getArguments();
		if (args != null) {
			photoid = args.getString("id");
			like = args.getBoolean("ranked");
			position = args.getInt("position");
			String url = args.getString("url");
			if (url != null) {
				Log.i(AppBlackout.TAG, url);

				new AsyncTask<String, Void, Bitmap>() {
					
					@Override
					protected Bitmap doInBackground(String... params) {
						Bitmap bmp = null;
						try {
							InputStream in = openHttpConnection(params[0]);
							bmp = decodeSampledBitmap(in, dstWidth, dstHeight);

						} catch (Exception e) {
							e.printStackTrace();
						}
						return bmp;
					}

					@Override
					protected void onPostExecute(Bitmap bmp) {
						if (bmp != null){
							ivPhoto.setImageBitmap(bmp);
						}
					}

				}.execute(url);
				
			}
			
			((TextView)view.findViewById(R.id.tvGalleryInfo)).setText(args.getString("info"));
			
			view.findViewById(R.id.bGalleryLike).setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					like = !like;
					activity.like(photoid, like, position);
					if(like){
						setLike();
					}else{
						setUnlike();
					}
				}
				
			});
			
		}
		
		if(like){
			setLike();
		}else{
			setUnlike();
		}
		
		return view;
	}
	
	private void setUnlike() {
		legend.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.ic_menu_legend, 0);
	}

	private void setLike() {
		legend.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.ic_menu_legend_blue, 0);
	}

	public InputStream openHttpConnection(String strURL) {

        try {
            URL url = new URL(strURL);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(connection.getInputStream());
            return in;
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

	public Bitmap decodeSampledBitmap(InputStream is,
	        int reqWidth, int reqHeight) {

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    Bitmap bmp = BitmapFactory.decodeStream(is, null, options);
	    if(bmp == null){
	    	Log.i(AppBlackout.TAG, "decode 1");
	    }
	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	    Log.i(AppBlackout.TAG, "decode" + options.inSampleSize);
	    
	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    try {
			is.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    bmp = BitmapFactory.decodeStream(is, null, options);
	    if(bmp == null){
	    	Log.i(AppBlackout.TAG, "decode 2");
	    }
	    return bmp;
	}
	
	public int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;
			
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

}
