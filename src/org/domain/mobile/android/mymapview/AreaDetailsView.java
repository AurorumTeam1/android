package org.domain.mobile.android.mymapview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class AreaDetailsView extends LinearLayout {

	public AreaDetailsView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
    }

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}
	
	public void setFields(AreaOverlay areaOverlay) {
		((TextView)findViewById(R.id.area_details_name)).setText(areaOverlay.getName());
		((TextView)findViewById(R.id.area_details_owner)).setText(areaOverlay.getOwner());
		((TextView)findViewById(R.id.area_details_description)).setText(areaOverlay.getDescription());
	}
}
