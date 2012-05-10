package org.domain.mobile.android.mymapview;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

import android.app.ActionBar;
import android.content.ContentValues;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

public class MyMapViewActivity extends MapActivity implements OnTouchListener{
	
	private MapView mapView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        ActionBar actionBar = getActionBar();
        actionBar.hide(); // Hide the actionbar in the area editing mode! (Should be visible in main app when it is implemented.)
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mapView = (MapView) findViewById(R.id.mapview);
//        mapView.setBuiltInZoomControls(true);
        mapView.setOnTouchListener(this);
        View doneActionView = findViewById(R.id.action_done);
        doneActionView.setOnClickListener(customActionBarListener);
        View cancelActionView = findViewById(R.id.action_cancel);
        cancelActionView.setOnClickListener(customActionBarListener);
        
    }

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	private OnClickListener customActionBarListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
	        switch (v.getId()) {
            case R.id.action_done:
            	// TODO: Save area
            	saveArea();
                break;
            case R.id.action_cancel:
            	// TODO: Cancel area editing
            	clearOverlays();
                break;
	        }
		}
	};

	public boolean onTouch(View v, MotionEvent event) {
		GeoPoint point;
		if(mapView.getOverlays().size()==0) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if(event.getPointerCount() == 1) {
					point = mapView.getProjection().fromPixels((int)event.getX(), (int)event.getY());
					addOverlay(point);
				}
				break;
			}
		}
		return false;
	}

	protected void saveArea() {
		//TODO: Do this in asynctask
		ContentValues values = new ContentValues();
		values.put(AreaProvider.EXTERNALDIR, getExternalFilesDir(null).getAbsolutePath());
		values.put(AreaProvider.FILENAME, "testarea1");
		Uri uri = getContentResolver().insert(AreaProvider.CONTENT_URI, values);
	}

	private void addOverlay(GeoPoint p) {
		Drawable defaultMarker = this.getResources().getDrawable(R.drawable.marker_red_dot);
		HelloItemizedOverlay itemizedOverlay = new HelloItemizedOverlay(defaultMarker, this);
		OverlayItem overlayItem = new OverlayItem(p, null, null);
		itemizedOverlay.addOverlay(overlayItem);
		mapView.getOverlays().add(itemizedOverlay);
	}
	
	protected void clearOverlays() {
		mapView.getOverlays().clear();
		mapView.invalidate();
	}
	
}