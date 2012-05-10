package org.domain.mobile.android.mymapview;

import java.io.Serializable;
import java.util.ArrayList;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class HelloItemizedOverlay extends ItemizedOverlay implements Serializable {
	
	private ArrayList<OverlayItem> mOverlayItems = new ArrayList<OverlayItem>();
	private Context mContext;
	private boolean isPinch;

	public HelloItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}
	
	public HelloItemizedOverlay(Drawable defaultMarker, Context context) {
		this(defaultMarker);
		mContext = context;
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
		// Removed functionality until we have designed for it.
//		OverlayItem item = mOverlayItems.get(index);
//		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
//		dialog.setTitle(item.getPoint().toString());
//		dialog.show();
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

	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		Paint paint = new Paint();
		paint.setStrokeWidth(2);
		paint.setStyle(Paint.Style.STROKE);
		paint.setAntiAlias(true);
		Point screenCoords1 = new Point();
		Point screenCoords2 = new Point();
		int i;
		for (i = 1; i < mOverlayItems.size(); i++) {
			mapView.getProjection().toPixels(mOverlayItems.get(i-1).getPoint(), screenCoords1);			
			mapView.getProjection().toPixels(mOverlayItems.get(i).getPoint(), screenCoords2);
			canvas.drawLine(screenCoords1.x, screenCoords1.y, screenCoords2.x, screenCoords2.y, paint);
		}
		if(--i > 1) {
			mapView.getProjection().toPixels(mOverlayItems.get(i).getPoint(), screenCoords1);			
			mapView.getProjection().toPixels(mOverlayItems.get(0).getPoint(), screenCoords2);
			canvas.drawLine(screenCoords1.x, screenCoords1.y, screenCoords2.x, screenCoords2.y, paint);
		}
		populate();
	}
}
