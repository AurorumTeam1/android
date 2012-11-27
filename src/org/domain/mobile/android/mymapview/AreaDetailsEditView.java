package org.domain.mobile.android.mymapview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
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

	
	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		if (changedView == this && visibility == View.GONE)
		{
			((MyMapViewActivity)getContext()).enableMapView();
			InputMethodManager imm = (InputMethodManager) getContext().getSystemService(
				      Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(this.getWindowToken(), 0);

				AreaOverlay area = ((MyMapViewActivity)getContext()).getSelectedArea();
				if (area != null) {
					area.setName(((EditText)findViewById(R.id.area_details_name_edit)).getText().toString());
					area.setOwner(((EditText)findViewById(R.id.area_details_owner_edit)).getText().toString());
					area.setDescription(((EditText)findViewById(R.id.area_details_description_edit)).getText().toString());
				}
		}
		else if (changedView == this && visibility == View.VISIBLE) {
			((MyMapViewActivity)getContext()).disableMapView();

			AreaOverlay area = ((MyMapViewActivity)getContext()).getSelectedArea();
			if (area != null) {
				((EditText)findViewById(R.id.area_details_name_edit)).setText(area.getName());
				((EditText)findViewById(R.id.area_details_owner_edit)).setText(area.getOwner());
				((EditText)findViewById(R.id.area_details_description_edit)).setText(area.getDescription());
				// TODO: Fix color array handling...
//				Spinner colorSpinner = (Spinner) this.findViewById(R.id.color_spinner);
//				ArrayAdapter myAdap = (ArrayAdapter) colorSpinner.getAdapter();
//				int spinnerPosition = myAdap.getPosition(String.valueOf(areaOverlay.getColor()));
//				colorSpinner.setSelection(spinnerPosition);
			}
		}
		
		super.onVisibilityChanged(changedView, visibility);
	}

}
