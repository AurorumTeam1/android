package org.domain.mobile.android.mymapview;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;

public abstract class BaseFileHandler implements AreaParser, AreaWriter {
	
	final String filePath;
	final String fileName;
	
	protected BaseFileHandler(String filePath, String fileName){
		this.filePath = filePath;
		this.fileName = fileName;
	}
	
	protected InputStream getInputStream() throws IOException {
		if(isExternalStorageAvailable()) {
			return new FileInputStream(new File(filePath, fileName));
		} else {
			throw new IOException("External storage is not available");
		}
	}
		
	protected OutputStream getOutputStream() throws IOException {
		if (isExternalStorageAvailable()) {
			if (!isExternalStorageReadOnly()) {
				return new FileOutputStream(new File(filePath, fileName));
			} else {
				throw new IOException("External storage is read only");
			}
		} else {
			throw new IOException("External storage is not available");
		}
	}
		
	private static boolean isExternalStorageReadOnly() {
		String extStorageState = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
			return true;
		}
		return false;
	}

	private static boolean isExternalStorageAvailable() {
		String extStorageState = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
			return true;
		}
		return false;
	}
}
