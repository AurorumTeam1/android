package org.domain.mobile.android.mymapview;
import java.util.ArrayList;

import com.google.android.maps.GeoPoint;

import android.os.Parcel;
import android.os.Parcelable;

/***
 * A class representing an area with bounding points.
 * 
 * @author dave
 */
public class Area implements Parcelable {

	private long id = -1;
	private String name = "";
	private String description = "";
	private int numberOfPoints = 0;
	private ArrayList<GeoPoint> points = new ArrayList<GeoPoint>();

	
	/**
	 * Creator for an Area object.
	 */
	public static class Creator implements Parcelable.Creator<Area> {

		@Override
		public Area createFromParcel(Parcel source) {
			Area area = new Area();
			area.id = source.readLong();
			area.name = source.readString();
			area.description = source.readString();
			area.numberOfPoints = source.readInt();
			for (int i = 0; i < area.numberOfPoints; ++i) {
				area.points.add(new GeoPoint(source.readInt(), source.readInt()));
			}
			return area;
		}

		@Override
		public Area[] newArray(int size) {
			return new Area[size];
		}
	}

	
	public static final Creator CREATOR = new Creator();

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(name);
		dest.writeString(description);
		dest.writeInt(numberOfPoints);
		for (int i = 0; i < numberOfPoints; ++i) {
			dest.writeInt(points.get(i).getLatitudeE6());
			dest.writeInt(points.get(i).getLongitudeE6());
		}
	}

	
	// Getters and setters:
	// ---------------------
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ArrayList<GeoPoint> getPoints() {
		return points;
	}

	public void setPoints(ArrayList<GeoPoint> points) {
		this.points = points;
	}

	public void addPoint(GeoPoint p) {
		points.add(p);
		numberOfPoints++;
	}

	public int getNumberOfPoints() {
		return numberOfPoints;
	}

	public void setNumberOfPoints(int numberOfPoints) {
		this.numberOfPoints = numberOfPoints;
	}
}
