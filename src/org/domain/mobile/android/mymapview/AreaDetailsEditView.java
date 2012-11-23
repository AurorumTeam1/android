package org.domain.mobile.android.mymapview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class AreaDetailsEditView extends LinearLayout {

	public AreaDetailsEditView(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	
	public void setFields(AreaOverlay areaOverlay) {
		((EditText)findViewById(R.id.area_details_name_edit)).setText(areaOverlay.getName());
		((EditText)findViewById(R.id.area_details_owner_edit)).setText(areaOverlay.getOwner());
		((EditText)findViewById(R.id.area_details_description_edit)).setText(areaOverlay.getDescription());
	}
	
	public void getFields(AreaOverlay areaOverlay) {
		areaOverlay.setName(((EditText)findViewById(R.id.area_details_name_edit)).getText().toString());
		areaOverlay.setOwner(((EditText)findViewById(R.id.area_details_owner_edit)).getText().toString());
		areaOverlay.setDescription(((EditText)findViewById(R.id.area_details_description_edit)).getText().toString());
	}

}
