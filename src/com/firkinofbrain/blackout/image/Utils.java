package com.firkinofbrain.blackout.image;

import java.io.InputStream;
import java.io.OutputStream;

public class Utils {
	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			int count;
			do{
				count = is.read(bytes, 0, buffer_size);
				os.write(bytes, 0, count);
			}while(count != -1);
			
		} catch (Exception ex) {
		}
	}
}
