package com.firkinofbrain.blackout.tools;

import com.firkinofbrain.blackout.R;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.widget.TextView;

public class AnimationManager {
	
	private Context context;
	
	public AnimationManager(Context context){
		this.context = context;
	}
	
	public void statusAnimate(TextView v, int RBgColor, String text){
		v.animate().cancel();
		v.setAlpha(1);
		v.setBackgroundColor(context.getResources()
				.getColor(RBgColor));
		v.setTextColor(context.getResources()
				.getColor(R.color.customWhite));
		v.setText(text);
		v.animate().setDuration(2000).alpha(0)
		.setListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				animation.cancel();
			}
		});
		
	}
	
	public void statusStill(final TextView v, int RBgColor, String text){
		v.animate().cancel();
		v.setAlpha(1);
		v.setBackgroundColor(context.getResources()
				.getColor(RBgColor));
		v.setTextColor(context.getResources()
				.getColor(R.color.customWhite));
		v.setText(text);
	}
	
	public Drawable getAvatar(String avatar){
		byte[] base64 = null;
		base64 = Base64.decode(avatar, Base64.DEFAULT);
		Bitmap bmp = BitmapFactory.decodeByteArray(base64, 0, base64.length);
		Drawable icon = new BitmapDrawable(context.getResources(), bmp);
		return icon;
	}
}
