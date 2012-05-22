package org.domain.mobile.android.mymapview;

import java.util.ArrayList;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class MyMapViewActivity extends MapActivity implements OnTouchListener {

	private MapView mapView;
	private boolean isPinch = false;
	private boolean isDrag = false;

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
				// TODO: Save area
				findViewById(R.id.cancel_button).setEnabled(true);
				findViewById(R.id.ok_button).setEnabled(false);
				hideMarkers();
				saveArea();
				break;
			case R.id.cancel_button:
				// TODO: Cancel area editing
				findViewById(R.id.cancel_button).setEnabled(false);
				findViewById(R.id.ok_button).setEnabled(false);
				clearOverlays();
				break;
			}
		}
	};
	private boolean isEditable; // Temp boolean to enable working with just one area, remove when implementing multiple areas.

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

		FileWriter fileWriter = new FileWriter(getExternalFilesDir(null).getAbsolutePath(), "testarea1", (AreaOverlay)mapView.getOverlays().get(0));
		fileWriter.write();
	}
	
	private void loadArea() {
		if (mapView.getOverlays().size() > 0) { // Do not load new if one already exists.
			return;
		}
		
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
	}


	protected void hideMarkers() {
		if (mapView.getOverlays().size() > 1) { // remove the overlay with
												// markers
			mapView.getOverlays().remove(1);
			mapView.invalidate();
		}
	}

	private void addOverlay(GeoPoint p) {
		HelloItemizedOverlay itemizedOverlay = new HelloItemizedOverlay(this.getResources().getDrawable(R.drawable.marker_red_dot), (ImageView) findViewById(R.id.drag));
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

	    for( GeoPoint l : points ) {
	        minLat  = Math.min( l.getLatitudeE6(), minLat );
	        minLong = Math.min( l.getLongitudeE6(), minLong);
	        maxLat  = Math.max( l.getLatitudeE6(), maxLat );
	        maxLong = Math.max( l.getLongitudeE6(), maxLong );
	    }

	    mapView.getController().zoomToSpan(Math.abs( minLat - maxLat ), Math.abs( minLong - maxLong ));
	    mapView.getController().animateTo(new GeoPoint((maxLat + minLat) / 2, (maxLong + minLong) / 2));
	}

}
