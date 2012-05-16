package org.domain.mobile.android.mymapview;

import java.io.Serializable;
import java.util.ArrayList;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class HelloItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	
	private ArrayList<OverlayItem> mOverlayItems = new ArrayList<OverlayItem>();
	private boolean isPinch;

	public HelloItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}
	
	public void addOverlay(OverlayItem overlayItem) {
		mOverlayItems.add(overlayItem);
		populate();
	}

	@Override
	protected OverlayItem createItem(int arg0) {
		return mOverlayItems.get(arg0);
	}

	@Override
	public int size() {
		return mOverlayItems.size();
	}
	
	@Override
	protected boolean onTap(int index) {
		return true; // Return true to avoid adding points on top of existing points
	}
	
	@Override
	public boolean onTap (final GeoPoint p, final MapView mapView) {
		if (isPinch) {
			return false;
		}
		if(super.onTap(p, mapView)) {
			// Do nothing here if overlay is tapped
            return true;
        }
		// Tap is outside overlay, add new point
        OverlayItem overlayItem = new OverlayItem(p, "", "");
        ((AreaOverlay)mapView.getOverlays().get(0)).addPoint(p);
        this.addOverlay(overlayItem);
        populate();
        return true;
    }
	
	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapView) {
		final int action = event.getAction();
		if (action == MotionEvent.ACTION_DOWN) {
			isPinch = false; // Touch DOWN, don't know if it's a pinch yet
		}
		if (action == MotionEvent.ACTION_MOVE && event.getPointerCount() == 2) {
			isPinch = true; // Two fingers, def a pinch
		}
		
		return super.onTouchEvent(event, mapView);
	}
}
