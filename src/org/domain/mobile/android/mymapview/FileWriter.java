package org.domain.mobile.android.mymapview;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import android.os.Environment;
import android.util.Log;

import com.google.android.maps.GeoPoint;

public class FileWriter {
	
	private String dir;
	private String fileName;
	private AreaOverlay area;

	public FileWriter(String dir, String filename, AreaOverlay area) {
		this.dir = dir;
		this.fileName = filename;
		this.area = area;
	}

	public boolean write() {
		if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
			Log.w("FileUtils", "Storage not available or read only");
			return false;
		}

		File file = new File(dir, fileName);
		OutputStreamWriter oos = null;
		boolean status = false;

		try {

			OutputStream os = new FileOutputStream(file);
			oos = new OutputStreamWriter(os);
			writeArea(oos);
			status = true;
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
		
		return status;
	}

	private void writeArea(OutputStreamWriter os) {
		ArrayList<GeoPoint> array = area.getPoints();
		for(GeoPoint p:array) {
			try {
				os.write(Integer.toString(p.getLatitudeE6()) + ", ");
				os.write(Integer.toString(p.getLongitudeE6()) + "\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
