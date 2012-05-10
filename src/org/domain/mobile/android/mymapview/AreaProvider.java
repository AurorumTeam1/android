package org.domain.mobile.android.mymapview;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class AreaProvider extends ContentProvider {
	public static final String PROVIDER_NAME = "org.domain.mobile.android.mymapview.AreaProvider";
	public static final Uri CONTENT_URI = Uri.parse("content://"+ PROVIDER_NAME + "/areas");
	public static final String EXTERNALDIR = "dir";
	public static final String FILENAME = "filename";

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
			Log.w("FileUtils", "Storage not available or read only");
			return null;
		}

		// Create a path where we will place our List of objects on external
		// storage
		File file = new File(values.getAsString(EXTERNALDIR), values.getAsString(FILENAME));
		ObjectOutputStream oos = null;
		boolean success = false;

		try {
			OutputStream os = new FileOutputStream(file);
			oos = new ObjectOutputStream(os);
			oos.writeObject("TEST"); //TODO: write itemizedoverlay object 
			success = true;
		} catch (IOException e) {
			Log.w("FileUtils", "Error writing " + file, e);
		} catch (Exception e) {
			Log.w("FileUtils", "Failed to save file", e);
		} finally {
			try {
				if (null != oos)
					oos.close();
			} catch (IOException ex) {
			}
		}

		return null;
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

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
