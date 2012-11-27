package org.domain.mobile.android.mymapview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AreaDetailsView extends LinearLayout {

	public AreaDetailsView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
    }

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}
	
	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		if (visibility == View.VISIBLE)
		{
			AreaOverlay area = ((MyMapViewActivity)getContext()).getSelectedArea();
			if (area != null) {
				((TextView)findViewById(R.id.area_details_name)).setText(area.getName());
				((TextView)findViewById(R.id.area_details_owner)).setText(area.getOwner());
				((TextView)findViewById(R.id.area_details_description)).setText(area.getDescription());
			}
		}
	}
}
