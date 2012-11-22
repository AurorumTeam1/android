package org.domain.mobile.android.mymapview;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MyMapViewActivity extends MapActivity implements OnTouchListener {

	private MapView mapView;
	private boolean isPinch = false;
	private boolean isDrag = false;
	private PositionOverlay mMyLocationOverlay;
	private boolean isEditMode = false;

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
		doneActionView.setOnClickListener(onClickListener);
		View cancelActionView = findViewById(R.id.cancel_button);
		cancelActionView.setOnClickListener(onClickListener);
		findViewById(R.id.new_button).setOnClickListener(onClickListener);
		findViewById(R.id.edit_button).setOnClickListener(onClickListener);
		findViewById(R.id.done_button).setOnClickListener(onClickListener);
		findViewById(R.id.details_overlay).setOnClickListener(onClickListener);
		findViewById(R.id.cancel_button).setEnabled(false);
		findViewById(R.id.ok_button).setEnabled(false);

		loadArea();
	}

	
//	TODO: Zoom in on last selected area(?)	
//	Save:
//    SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
//    SharedPreferences.Editor prefsEditor = myPrefs.edit();
//    prefsEditor.putString(MY_NAME, edittext.getText().toString());
//    prefsEditor.putString(MY_WALLPAPER, "f664.PNG");
//    prefsEditor.commit();	
//    Load:
//	  SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
//    String prefName = myPrefs.getString(MY_NAME, 0);
//    String wallPaper = myPrefs.getString(MY_WALLPAPER, null);
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	private OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.new_button:
				showEditActionbar();
				break;
			case R.id.edit_button:
			case R.id.details_overlay:
				showDetailsEditOverlay();
				break;
			case R.id.done_button:
				hideDetailsEditOverlay();
				break;
			case R.id.ok_button:
				findViewById(R.id.cancel_button).setEnabled(true);
				findViewById(R.id.ok_button).setEnabled(false);
				hideMarkers();
				saveArea();
				showMainActionBar();
				break;
			case R.id.cancel_button:
				findViewById(R.id.cancel_button).setEnabled(false);
				findViewById(R.id.ok_button).setEnabled(false);
				clearOverlays();
				showMainActionBar();
				break;
			}
		}
	};
	private AreaOverlay selectedArea;
	
	private int isEditing() {
		List<Overlay> overlays = mapView.getOverlays();
		for (int i = 0; i < overlays.size(); i++) {
			if(overlays.get(i) instanceof HelloItemizedOverlay) {
				return i;
			}
		}
		return -1;
	}

	public boolean onTouch(View v, MotionEvent event) {
		if (isEditing() < 0 && isEditMode) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				isPinch = false;
				isDrag = false;
				break;
			case MotionEvent.ACTION_MOVE:
				if (event.getPointerCount() > 1) {
					isPinch = true;
				}
				// From port_2.3.4 #8 Fine tuning the drag handling since xperia active has a less sensitive touch screen.
				else if(!isDrag && event.getHistorySize() > 0) { 
					  if(Math.abs(event.getHistoricalX(0) - event.getX()) > 0.5f || Math.abs(event.getHistoricalY(0) - event.getY()) > 0.5f){
						  isDrag = true;
					  }
				}
				break;
			case MotionEvent.ACTION_UP:
				if (!isPinch && !isDrag) {
					GeoPoint point = mapView.getProjection().fromPixels((int) event.getX(), (int) event.getY());
					addOverlay(point);
				}
				break;
			//SE Xperia Active often signals ACTION_CANCEL instead of ACTION_UP
			case MotionEvent.ACTION_CANCEL: 
				if (!isPinch && !isDrag) {
					GeoPoint point = mapView.getProjection().fromPixels((int) event.getX(), (int) event.getY());
					addOverlay(point);
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
		List<AreaOverlay> areas = new ArrayList<AreaOverlay>();
		List<Overlay> overlays = mapView.getOverlays();
		for(Overlay overlay:overlays) {
			if(overlay instanceof AreaOverlay) {
				areas.add((AreaOverlay) overlay);
			}
		}
		try {
			AreaWriter writer = new XMLFileHandler(this, getExternalFilesDir(null).getAbsolutePath(), "areas.xml");
			writer.write(areas);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadArea() {
		try {
			AreaParser parser = new XMLFileHandler(this, getExternalFilesDir(null).getAbsolutePath(), "areas.xml");
			List<AreaOverlay> areas = parser.parse();
			if (areas.size() > 0) {
				mapView.getOverlays().addAll(areas);
				centerOnOverlay(areas.get(areas.size() - 1).getPoints(), 1);
			}
			findViewById(R.id.cancel_button).setEnabled(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void hideMarkers() {		
		Overlay o;
		for (int i = mapView.getOverlays().size()-1; i >= 0; i--) {
			o = mapView.getOverlays().get(i);
			if(o instanceof HelloItemizedOverlay) {
				mapView.getOverlays().set(i, transform((HelloItemizedOverlay)o, "Area " + i));
				break;
			}
		}
		mapView.invalidate();
	}
	
	protected AreaOverlay transform(HelloItemizedOverlay hio, String name) {
		AreaOverlay ao = new AreaOverlay(this, name);
		for (int j = 0; j < hio.size(); j++) {
			ao.addPoint(hio.getItem(j).getPoint());
		}
		return ao;
	}

	private void addOverlay(GeoPoint p) {
		HelloItemizedOverlay itemizedOverlay = new HelloItemizedOverlay(this, this.getResources().getDrawable(
				R.drawable.marker_red_dot), (ImageView) findViewById(R.id.drag), findViewById(R.id.custom_actionbar),
				(Button) findViewById(R.id.button_remove));
		OverlayItem overlayItem = new OverlayItem(p, null, null);
		itemizedOverlay.addOverlay(overlayItem);
		//mapView.getOverlays().add(0, new AreaOverlay().addPoint(p));
		mapView.getOverlays().add(itemizedOverlay);
		findViewById(R.id.ok_button).setEnabled(true);
		findViewById(R.id.cancel_button).setEnabled(true);
	}

	protected void clearOverlays() {
		int index = isEditing();
		if(index >= 0) {
			mapView.getOverlays().remove(index);
		}
		mapView.invalidate();
	}

	public void centerOnOverlay(ArrayList<GeoPoint> points, double fitFactor) {
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

		mapView.getController().zoomToSpan((int) (Math.abs(minLat - maxLat) * fitFactor), (int) (Math.abs(minLong - maxLong) * fitFactor));
		mapView.getController().animateTo(new GeoPoint((int) ((maxLat + minLat) / 2 / fitFactor), (maxLong + minLong) / 2));
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
		case R.id.clear_all_areas:
			clearAllAreas();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	private void clearAllAreas() {
		File file = new File(getExternalFilesDir(null).getAbsolutePath(), "areas.xml");
		if (file.delete() == true) {
			Toast.makeText(mapView.getContext(), "Cleared all areas!", Toast.LENGTH_SHORT).show();
		}
		else {
			Toast.makeText(mapView.getContext(), "Failed to clear all areas!", Toast.LENGTH_LONG).show();
		}
		showAreaDetails(null);
		mapView.getOverlays().clear();
		mapView.invalidate();
	}

	private void getLocationUpdates() {
		if(mMyLocationOverlay == null)
		{
			mMyLocationOverlay = new PositionOverlay(this,mapView);
		}
		mapView.getOverlays().add(mMyLocationOverlay);
		mMyLocationOverlay.enableMyLocation();
		mMyLocationOverlay.runOnFirstFix(new Runnable() { public void run() {
			mapView.getController().animateTo(mMyLocationOverlay.getMyLocation());
		}});

	}

	public void showAreaDetails(AreaOverlay areaOverlay) {
		selectedArea = areaOverlay;
		showMainActionBar();
		if (areaOverlay != null) {
			((View) findViewById(R.id.details_edit_overlay)).setVisibility(View.GONE);
			((View) findViewById(R.id.details_overlay)).setVisibility(View.VISIBLE);
			((TextView) findViewById(R.id.area_details_name)).setText(areaOverlay.getName());
			((View) findViewById(R.id.new_button)).setVisibility(View.GONE);
			((View) findViewById(R.id.done_button)).setVisibility(View.GONE);
			((View) findViewById(R.id.edit_button)).setVisibility(View.VISIBLE);
		}
		else {
			((View) findViewById(R.id.details_edit_overlay)).setVisibility(View.GONE);
			((View) findViewById(R.id.details_overlay)).setVisibility(View.GONE);
			((View) findViewById(R.id.edit_button)).setVisibility(View.GONE);
			((View) findViewById(R.id.done_button)).setVisibility(View.GONE);
			((View) findViewById(R.id.new_button)).setVisibility(View.VISIBLE);
		}
	}


	
	private void showEditActionbar() {
		isEditMode = true;
		findViewById(R.id.main_actionbar).setVisibility(View.GONE);
		findViewById(R.id.edit_area_actionbar).setVisibility(View.VISIBLE);
	}

	private void showMainActionBar() {
		isEditMode = false;
		findViewById(R.id.edit_area_actionbar).setVisibility(View.GONE);
		findViewById(R.id.main_actionbar).setBackgroundColor(getResources().getColor(R.color.bar_background));
		((TextView)findViewById(R.id.actionbar_title)).setTextColor(getResources().getColor(R.color.text));
		((View) findViewById(R.id.new_button)).setVisibility(View.VISIBLE);
		findViewById(R.id.main_actionbar).setVisibility(View.VISIBLE);
	}

	protected void showDetailsEditOverlay() {

		if (selectedArea != null) {
			//TODO: Zoom in on selected area
			centerOnOverlay(selectedArea.getPoints(), 1.5);

			//Set colors and visibility
			findViewById(R.id.main_actionbar).setBackgroundColor(getResources().getColor(R.color.edit_overlay_background));
			((TextView)findViewById(R.id.actionbar_title)).setTextColor(getResources().getColor(R.color.text_dark));
			((View) findViewById(R.id.edit_button)).setVisibility(View.GONE);
			((View) findViewById(R.id.details_overlay)).setVisibility(View.GONE);
			((View) findViewById(R.id.done_button)).setVisibility(View.VISIBLE);
			((View) findViewById(R.id.details_edit_overlay)).setVisibility(View.VISIBLE);
			((TextView) findViewById(R.id.area_details_name_edit)).setText(selectedArea.getName());
		}
		else {
			Toast.makeText(mapView.getContext(), "No area selected", Toast.LENGTH_SHORT).show();
		}
	}

	protected void hideDetailsEditOverlay() {
		((View) findViewById(R.id.done_button)).setVisibility(View.GONE);
		((View) findViewById(R.id.edit_button)).setVisibility(View.GONE);
		((View) findViewById(R.id.details_edit_overlay)).setVisibility(View.GONE);
		((View) findViewById(R.id.details_overlay)).setVisibility(View.VISIBLE);
		showMainActionBar();
	}
}
