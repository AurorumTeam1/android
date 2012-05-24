package org.domain.mobile.android.mymapview;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class HelloItemizedOverlay extends ItemizedOverlay<OverlayItem> {

	private Context mContext;
	private ArrayList<OverlayItem> mOverlayItems = new ArrayList<OverlayItem>();
	private boolean isPinch;
	private ImageView dragImage;
	private Button removeButton;
	private View customActionBar;
	private Drawable defaultMarker;
	private int xDragImageOffset;
	private int yDragImageOffset;
	private int xDragTouchOffset;
	private int yDragTouchOffset;
	private OverlayItem inDrag;
	private int oldIndex;
	private int[] mCoordBuffer = {0,0};

	public HelloItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}

	public HelloItemizedOverlay(Context context, Drawable defaultMarker, ImageView dragImage, View customActionBar,
			Button removeButton) {
		this(defaultMarker);
		this.mContext = context;
		this.dragImage = dragImage;
		this.customActionBar = customActionBar;
		this.removeButton = removeButton;
		this.defaultMarker = defaultMarker;
		xDragImageOffset = dragImage.getDrawable().getIntrinsicWidth() / 2;
		yDragImageOffset = dragImage.getDrawable().getIntrinsicHeight();
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
		return true; // Return true to avoid adding points on top of existing
						// points
	}

	@Override
	public boolean onTap(final GeoPoint p, final MapView mapView) {
		if (isPinch) {
			return false;
		}
		if (this.size() != 0 && super.onTap(p, mapView)) {
			// Do nothing here if overlay is tapped
			return true;
		}
		// Tap is outside overlay, add new point
		OverlayItem overlayItem = new OverlayItem(p, "", "");
		((AreaOverlay) mapView.getOverlays().get(0)).addPoint(p);
		this.addOverlay(overlayItem);
		populate();
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapView) {
		AreaOverlay area = (AreaOverlay) mapView.getOverlays().get(0);

		final int action = event.getAction();
		if (action == MotionEvent.ACTION_DOWN) {
			isPinch = false; // Touch DOWN, don't know if it's a pinch yet
		}
		if (action == MotionEvent.ACTION_MOVE && event.getPointerCount() == 2) {
			isPinch = true; // Two fingers, def a pinch
		}

		final int x = (int) event.getX();
		final int y = (int) event.getY();
		boolean result = false;

		if (action == MotionEvent.ACTION_DOWN) {
			for (OverlayItem item : mOverlayItems) {
				Point p = new Point(0, 0);

				mapView.getProjection().toPixels(item.getPoint(), p);

				if (hitTest(item, defaultMarker, x - p.x, y - p.y)) {
					result = true;
					inDrag = item;
					oldIndex = mOverlayItems.indexOf(inDrag);
					mOverlayItems.remove(inDrag);
					area.getPoints().remove(oldIndex);
					populate();

					xDragTouchOffset = 0;
					yDragTouchOffset = 0;

					setDragImagePosition(p.x, p.y);
					dragImage.setVisibility(View.VISIBLE);

					removeButton.setVisibility(View.VISIBLE);
					Animation fadeInAnimation = AnimationUtils.loadAnimation(mContext , R.anim.fade_in);
					removeButton.startAnimation(fadeInAnimation);
					customActionBar.setVisibility(View.INVISIBLE);
					Animation fadeOutAnimation = AnimationUtils.loadAnimation(mContext, R.anim.fade_out);
					customActionBar.startAnimation(fadeOutAnimation);

					xDragTouchOffset = x - p.x;
					yDragTouchOffset = y - p.y;

					break;
				}
			}
		} else if (action == MotionEvent.ACTION_MOVE && inDrag != null) {
			setDragImagePosition(x, y);

			boolean insideRemoveButton = inRegion(event.getRawX(), event.getRawY(), removeButton);
			//TODO: optimize so handling this only on enter and leave button, not every move event inside button.
			if (insideRemoveButton) {
				if (removeButton.getCurrentTextColor() == Color.BLACK) { // Quick and dirty optimization 
					removeButton.setTextColor(Color.RED);
					Drawable removeDrawable = mContext.getResources().getDrawable(R.drawable.ic_menu_remove_field_holo_light_red);
					removeButton.setCompoundDrawablesWithIntrinsicBounds(removeDrawable , null, null, null);
				}
			} else {
				if (removeButton.getCurrentTextColor() == Color.RED) {
					removeButton.setTextColor(Color.BLACK);
					Drawable removeDrawable = mContext.getResources().getDrawable(R.drawable.ic_menu_remove_field_holo_light);
					removeButton.setCompoundDrawablesWithIntrinsicBounds(removeDrawable , null, null, null);
				}
			}
			result = true;
			
		} else if (action == MotionEvent.ACTION_UP && inDrag != null) {
			dragImage.setVisibility(View.GONE);

			Animation fadeOutAnimation = AnimationUtils.loadAnimation(mContext, R.anim.fade_out);
			removeButton.startAnimation(fadeOutAnimation);
			removeButton.setVisibility(View.INVISIBLE);
			customActionBar.setVisibility(View.VISIBLE);
			Animation fadeInAnimation = AnimationUtils.loadAnimation(mContext, R.anim.fade_in);
			customActionBar.startAnimation(fadeInAnimation);

			boolean insideRemoveButton = inRegion(event.getRawX(), event.getRawY(), removeButton);
			if (!insideRemoveButton) {
				GeoPoint pt = mapView.getProjection().fromPixels(x - xDragTouchOffset, y - yDragTouchOffset);
				OverlayItem toDrop = new OverlayItem(pt, inDrag.getTitle(), inDrag.getSnippet());

				mOverlayItems.add(oldIndex, toDrop);
				area.getPoints().add(oldIndex, toDrop.getPoint());
				populate();
			}

			inDrag = null;
			result = true;
		}

		return (result || super.onTouchEvent(event, mapView));
	}
	
	
	private boolean inRegion(float x, float y, View v) {
		v.getLocationOnScreen(mCoordBuffer );
        return mCoordBuffer[0] + v.getWidth() > x &&    // right edge
               mCoordBuffer[1] + v.getHeight() > y &&   // bottom edge
               mCoordBuffer[0] < x &&                   // left edge
               mCoordBuffer[1] < y;                     // top edge
    }

	
	private void setDragImagePosition(int x, int y) {
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) dragImage.getLayoutParams();
		lp.setMargins(x - xDragImageOffset - xDragTouchOffset, y - yDragImageOffset - yDragTouchOffset, 0, 0);
		dragImage.setLayoutParams(lp);
	}

}
