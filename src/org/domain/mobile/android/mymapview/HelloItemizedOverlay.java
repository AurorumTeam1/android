package org.domain.mobile.android.mymapview;

import java.util.ArrayList;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
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

	private ArrayList<OverlayItem> mOverlayItems = new ArrayList<OverlayItem>();
	private boolean isPinch;
	private ImageView dragImage;
	private View removeButton;
	private View customActionbar;
	private Drawable defaultMarker;
	private int xDragImageOffset;
	private int yDragImageOffset;
	private int xDragTouchOffset;
	private int yDragTouchOffset;
	private OverlayItem inDrag;
	private int oldIndex;
	private Context mContext;

	public HelloItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}

	public HelloItemizedOverlay(Context context, Drawable defaultMarker, ImageView dragImage, View customActionbar,
			View removeButton) {
		this(defaultMarker);
		this.mContext = context;
		this.dragImage = dragImage;
		this.customActionbar = customActionbar;
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
		if (super.onTap(p, mapView)) {
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

					// Start drag operation
					dragImage.setTag(oldIndex);
					ClipData.Item clipItem = new ClipData.Item((CharSequence) dragImage.getTag().toString());
					ClipDescription NOTE_STREAM_TYPES = new ClipDescription((CharSequence) dragImage.getTag().toString(), new String[] { ClipDescription.MIMETYPE_TEXT_PLAIN });
					ClipData data = new ClipData(NOTE_STREAM_TYPES, clipItem);


					dragImage.startDrag(data, new DragShadowBuilder(dragImage), null, 0);

					
					
					removeButton.setVisibility(View.VISIBLE);
					Animation fadeInAnimation = AnimationUtils.loadAnimation(mContext, R.anim.fade_in);
					removeButton.startAnimation(fadeInAnimation);
					customActionbar.setVisibility(View.INVISIBLE);
					Animation fadeOutAnimation = AnimationUtils.loadAnimation(mContext, R.anim.fade_out);
					customActionbar.startAnimation(fadeOutAnimation);

					xDragTouchOffset = x - p.x;
					yDragTouchOffset = y - p.y;

					break;
				}
			}
		} else if (action == MotionEvent.ACTION_MOVE && inDrag != null) {
			setDragImagePosition(x, y);
			result = true;
		} else if (action == MotionEvent.ACTION_UP && inDrag != null) {
			dragImage.setVisibility(View.GONE);
			removeButton.setVisibility(View.INVISIBLE);
			Animation fadeOutAnimation = AnimationUtils.loadAnimation(mContext, R.anim.fade_out);
			removeButton.startAnimation(fadeOutAnimation);
			customActionbar.setVisibility(View.VISIBLE);
			Animation fadeInAnimation = AnimationUtils.loadAnimation(mContext, R.anim.fade_in);
			customActionbar.startAnimation(fadeInAnimation);

			GeoPoint pt = mapView.getProjection().fromPixels(x - xDragTouchOffset, y - yDragTouchOffset);
			OverlayItem toDrop = new OverlayItem(pt, inDrag.getTitle(), inDrag.getSnippet());

			mOverlayItems.add(oldIndex, toDrop);
			area.getPoints().add(oldIndex, toDrop.getPoint());
			populate();

			inDrag = null;
			result = true;
		}

		return (result || super.onTouchEvent(event, mapView));
	}

	private void setDragImagePosition(int x, int y) {
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) dragImage.getLayoutParams();
		lp.setMargins(x - xDragImageOffset - xDragTouchOffset, y - yDragImageOffset - yDragTouchOffset, 0, 0);
		dragImage.setLayoutParams(lp);
	}

}
