package com.firkinofbrain.blackout.navmenu;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class NavMenuItem {
	private String title;
	private int icon;
	private String count = "0";
	private boolean countVisible = false;
	private Drawable drawable;
	private boolean hasBitmap = false;

	public NavMenuItem() {

	}

	public NavMenuItem(String title, int icon) {
		this.setTitle(title);
		this.setIcon(icon);
	}

	public NavMenuItem(String title, int icon, boolean visible, String count) {
		this.setTitle(title);
		this.setIcon(icon);
		this.setCount(count);
		this.setVisible(visible);
	}
	
	public NavMenuItem(String title, Drawable drawable, boolean hasBitmap){
		this.setTitle(title);
		this.setDrawable(drawable);
		this.setHasBitmap(hasBitmap);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public boolean isVisible() {
		return countVisible;
	}

	public void setVisible(boolean visible) {
		this.countVisible = visible;
	}

	public boolean hasBitmap() {
		return hasBitmap;
	}

	public void setHasBitmap(boolean hasBitmap) {
		this.hasBitmap = hasBitmap;
	}

	public Drawable getDrawable() {
		return drawable;
	}

	public void setDrawable(Drawable drawable) {
		this.drawable = drawable;
	}
}
