package org.domain.mobile.android.mymapview;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class AreaDetailsEditActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.area_details_edit);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.area_details_edit, menu);
		return true;
	}

}
