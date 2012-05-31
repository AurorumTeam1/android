package org.domain.mobile.android.mymapview;

import java.util.ArrayList;

import android.app.ActionBar;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class MyMapViewActivity extends MapActivity implements OnTouchListener, LocationListener {

	private static final int UPDATE_LOCATION = 0;
	private MapView mapView;
	private boolean isPinch = false;
	private boolean isDrag = false;

	private double mLongitude = 0;
	private double mLatitude = 0;

	private LocationManager mLocationManager;
	Thread mThread;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		ActionBar actionBar = getActionBar();
		actionBar.hide(); // Hide the actionbar in the area editing mode!
		// (Should be visible in main app when it is
		// implemented.)
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setOnTouchListener(this);
		View doneActionView = findViewById(R.id.ok_button);
		doneActionView.setOnClickListener(customActionBarListener);
		View cancelActionView = findViewById(R.id.cancel_button);
		cancelActionView.setOnClickListener(customActionBarListener);
		findViewById(R.id.cancel_button).setEnabled(false);
		findViewById(R.id.ok_button).setEnabled(false);

		loadArea();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	private OnClickListener customActionBarListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.ok_button:
				findViewById(R.id.cancel_button).setEnabled(true);
				findViewById(R.id.ok_button).setEnabled(false);
				hideMarkers();
				saveArea();
				break;
			case R.id.cancel_button:
				findViewById(R.id.cancel_button).setEnabled(false);
				findViewById(R.id.ok_button).setEnabled(false);
				clearOverlays();
				break;
			}
		}
	};
	private boolean isEditable; // Temp boolean to enable working with just one
	// area, remove when implementing multiple
	// areas.

	public boolean onTouch(View v, MotionEvent event) {
		if (mapView.getOverlays().size() <= 1) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				isPinch = false;
				isDrag = false;
				break;
			case MotionEvent.ACTION_MOVE:
				if (event.getPointerCount() > 1) {
					isPinch = true;
				} else {
					isDrag = true;
				}
				break;
			case MotionEvent.ACTION_UP:
				if (!isPinch && !isDrag) {
					GeoPoint point = mapView.getProjection().fromPixels((int) event.getX(), (int) event.getY());
					if (isEditable) {
						addOverlay(point);
					}
				}
				break;
			}
		}
		return false;
	}

	protected void saveArea() {
		if (mapView.getOverlays().size() == 0) {
			return;
		}

		FileWriter fileWriter = new FileWriter(getExternalFilesDir(null).getAbsolutePath(), "testarea1",
				(AreaOverlay) mapView.getOverlays().get(0));
		fileWriter.write();
	}

	private void loadArea() {
		if (mapView.getOverlays().size() > 0) { // Do not load new if one
			// already exists.
			return;
		}

		try {
			FileWriter fileWriter = new FileWriter(getExternalFilesDir(null).getAbsolutePath(), "testarea1");
			AreaOverlay loadadArea = fileWriter.read();
			if (loadadArea == null || loadadArea.getPoints().size() == 0) {
				return;
			}

			mapView.getOverlays().add(0, loadadArea);
			isEditable = false;
			findViewById(R.id.ok_button).setEnabled(false);
			findViewById(R.id.cancel_button).setEnabled(true);

			centerOnOverlay(loadadArea.getPoints());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void hideMarkers() {
		if (mapView.getOverlays().size() > 1) { // remove the overlay with
			// markers
			mapView.getOverlays().remove(1);
			mapView.invalidate();
		}
	}

	private void addOverlay(GeoPoint p) {
		HelloItemizedOverlay itemizedOverlay = new HelloItemizedOverlay(this, this.getResources().getDrawable(
				R.drawable.marker_red_dot), (ImageView) findViewById(R.id.drag), findViewById(R.id.custom_actionbar),
				(Button) findViewById(R.id.button_remove));
		OverlayItem overlayItem = new OverlayItem(p, null, null);
		itemizedOverlay.addOverlay(overlayItem);
		mapView.getOverlays().add(0, new AreaOverlay().addPoint(p));
		mapView.getOverlays().add(itemizedOverlay);
		findViewById(R.id.ok_button).setEnabled(true);
		findViewById(R.id.cancel_button).setEnabled(true);
	}

	protected void clearOverlays() {
		mapView.getOverlays().clear();
		mapView.invalidate();
		isEditable = true;
	}

	public void centerOnOverlay(ArrayList<GeoPoint> points) {
		int minLat = Integer.MAX_VALUE;
		int minLong = Integer.MAX_VALUE;
		int maxLat = Integer.MIN_VALUE;
		int maxLong = Integer.MIN_VALUE;

		for (GeoPoint l : points) {
			minLat = Math.min(l.getLatitudeE6(), minLat);
			minLong = Math.min(l.getLongitudeE6(), minLong);
			maxLat = Math.max(l.getLatitudeE6(), maxLat);
			maxLong = Math.max(l.getLongitudeE6(), maxLong);
		}

		mapView.getController().zoomToSpan(Math.abs(minLat - maxLat), Math.abs(minLong - maxLong));
		mapView.getController().animateTo(new GeoPoint((maxLat + minLat) / 2, (maxLong + minLong) / 2));
	}

	/**
	 * Creates menu items from a resource file.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Handles the creation of a new marker on the map.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.add_new_marker:
			getLocationUpdates();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}



	/**
	 * Invoked by the location service when phone's location changes.
	 */
	public void onLocationChanged(Location newLocation) {
		GeoPoint point = new GeoPoint((int)(newLocation.getLatitude()*1E6), (int)(newLocation.getLongitude()*1E6));
		HelloItemizedOverlay itemizedOverlay = new HelloItemizedOverlay(this.getResources().getDrawable(R.drawable.marker_start));
		OverlayItem overlayitem = new OverlayItem(point, "Me", "Hello ");
		itemizedOverlay.addOverlay(overlayitem);
		mapView.getOverlays().add(itemizedOverlay);
		mLocationManager.removeUpdates(this);
	}

	public void onProviderEnabled(String provider) {
	}
	public void onProviderDisabled(String provider) {
	}
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}


	private void getLocationUpdates() {
		mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, this); // Every 10000 msecs			
	}
}
