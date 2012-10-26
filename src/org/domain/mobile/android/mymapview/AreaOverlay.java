package org.domain.mobile.android.mymapview;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class AreaOverlay extends Overlay {

	private ArrayList<GeoPoint> points;
	private String Name; 

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public AreaOverlay() {
		this.points = new ArrayList<GeoPoint>();
		this.Name = "";
	}

	public AreaOverlay addPoint(GeoPoint point) {
		this.points.add(point);
		return this;
	}

	@Override
	public boolean onTap(GeoPoint geoPoint, MapView mapView) {
		if(isInside(geoPoint))
			Toast.makeText(mapView.getContext(), "Inside", Toast.LENGTH_SHORT).show();
		return false;
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

	public ArrayList<GeoPoint> getPoints() {
		return points;
	}

	protected boolean isInside (GeoPoint point) {
		/**
		 * Copyright (c) 1970-2003, Wm. Randolph Franklin
		 * http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
		 * 
		 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without 
		 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following 
		 * conditions:
		 * 	1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimers.
		 * 	2. Redistributions in binary form must reproduce the above copyright notice in the documentation and/or other materials provided with the distribution.
		 * 	3. The name of W. Randolph Franklin may not be used to endorse or promote products derived from this Software without specific prior written permission.
		 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A 
		 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN 
		 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
		 */
		int i, j;
		boolean isInside = false;
		for (i = 0, j = this.points.size()-1; i < this.points.size(); j = i++) {
			if (((this.points.get(i).getLatitudeE6()>point.getLatitudeE6()) != (this.points.get(j).getLatitudeE6()>point.getLatitudeE6())) && (point.getLongitudeE6() < (this.points.get(j).getLongitudeE6()-this.points.get(i).getLongitudeE6()) * (point.getLatitudeE6()-this.points.get(i).getLatitudeE6()) / (this.points.get(j).getLatitudeE6()-this.points.get(i).getLatitudeE6()) + this.points.get(i).getLongitudeE6()) )
				isInside = !isInside;
		}
		return isInside;
	}
}
