package org.domain.mobile.android.mymapview;

import java.util.ArrayList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class AreaOverlay extends Overlay {
	
	private ArrayList<GeoPoint> points;

	public AreaOverlay() {
		this.points = new ArrayList<GeoPoint>();
	}
	
	public AreaOverlay addPoint(GeoPoint point) {
		this.points.add(point);
		return this;
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {

		Point[] pxPoints = new Point[this.points.size()];
		Path area = new Path();
		area.setFillType(Path.FillType.EVEN_ODD);
		for (int i = 0; i < pxPoints.length; i++) {
			mapView.getProjection().toPixels(this.points.get(i), pxPoints[i] = new Point());
			if(i == 0) {
				area.moveTo(pxPoints[i].x, pxPoints[i].y);
			} else {
				area.lineTo(pxPoints[i].x, pxPoints[i].y);
			}
		}
		area.close();

		Paint areaPaint = new Paint();
		areaPaint.setColor(android.graphics.Color.GREEN);
		areaPaint.setStyle(Paint.Style.FILL);
		areaPaint.setAntiAlias(true);
		areaPaint.setAlpha(70);
		canvas.drawPath(area, areaPaint);

		areaPaint.setStrokeWidth(2);
		areaPaint.setColor(android.graphics.Color.BLUE);
		areaPaint.setStyle(Paint.Style.STROKE);
		areaPaint.setAlpha(90);
		canvas.drawPath(area, areaPaint);
	}

}
