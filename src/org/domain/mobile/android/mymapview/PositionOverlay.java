package org.domain.mobile.android.mymapview;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import android.content.Context;
import android.widget.Toast;

import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class PositionOverlay extends MyLocationOverlay {

	Context context;
	public PositionOverlay(Context arg0, MapView arg1) {
		super(arg0, arg1);

		context = arg0;
	}

	@Override
	protected boolean dispatchTap() {
		Toast.makeText(context, roundTwoDecimals(this.getMyLocation().getLatitudeE6()/1E6) + " : " + roundTwoDecimals(this.getMyLocation().getLongitudeE6()/1E6), Toast.LENGTH_LONG).show();
		return super.dispatchTap();
	}

	private double roundTwoDecimals(double d) {
		DecimalFormat twoDForm = new DecimalFormat("#*" + DecimalFormatSymbols.getInstance().getDecimalSeparator() + "##");
		return Double.valueOf(twoDForm.format(d));
	}

}
