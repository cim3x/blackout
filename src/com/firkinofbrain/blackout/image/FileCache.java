package com.firkinofbrain.blackout.image;

import java.io.File;

import android.content.Context;
import android.os.Environment;

public class FileCache {

	private File cacheDir;

	public FileCache(Context context){
    	
    	
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
        	String path = Environment.getExternalStorageDirectory().getName()
        			+ File.separatorChar
        			+ "BlackoutDiary";
            cacheDir = new File(path,"tempcache");
        }else
            cacheDir = context.getCacheDir();
        
        if(!cacheDir.exists())
            cacheDir.mkdirs();
    }

	public File getFile(String url) {
		String filename = String.valueOf(url.hashCode());
		File f = new File(cacheDir, filename);
		return f;

	}

	public void clear() {
		File[] files = cacheDir.listFiles();
		if (files == null)
			return;
		for (File f : files)
			f.delete();
	}

}
