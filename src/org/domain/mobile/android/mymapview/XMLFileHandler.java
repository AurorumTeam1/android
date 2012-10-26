package org.domain.mobile.android.mymapview;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.util.Xml;
import android.util.Xml.Encoding;

import com.google.android.maps.GeoPoint;

public class XMLFileHandler extends BaseFileHandler {

	static final String AREA = "area";
	static final String AREAS = AREA + "s";
	static final String NAME = "name";
//	static final String DESCRIPTION = "description";
	static final String POINT = "point";
	static final String POINTS = POINT + "s";
	static final String LATITUDE = "latitude";
	static final String LONGITUDE = "longitude";
//	static final String POINTORDER = "order";
	private Context myContext;

	public XMLFileHandler(Context context, String filePath, String fileName) {
		super(filePath, fileName);
		this.myContext = context;
	}

	@Override
	public List<AreaOverlay> parse() throws RuntimeException, IOException {
		List<AreaOverlay> areas = new ArrayList<AreaOverlay>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(this.getInputStream());
			Element root = dom.getDocumentElement();
			NodeList items = root.getElementsByTagName(AREA);
			for (int i = 0; i < items.getLength(); i++) {
				AreaOverlay area = new AreaOverlay(myContext, "Area " + i);
				Node item = items.item(i);
				NodeList properties = item.getChildNodes();
				for (int j = 0; j < properties.getLength(); j++) {
					Node property = properties.item(j);
					String name = property.getNodeName();
					if (name.equalsIgnoreCase(POINTS)) {
						NodeList points = property.getChildNodes();
						for (int k = 0; k < points.getLength(); k++) {
							Node point = points.item(k);
							if (point.getNodeName().equalsIgnoreCase(POINT)) {
								NodeList longitudeAndLatitude = point
										.getChildNodes();
								Node position;
								int lon = -1, lat = -1;
								for (int l = 0; l < longitudeAndLatitude
										.getLength(); l++) {
									position = longitudeAndLatitude.item(l);
									if (position.getNodeName()
											.equalsIgnoreCase(LONGITUDE)) {
										lon = Integer.parseInt(position.getTextContent());
									} else if (position.getNodeName()
											.equalsIgnoreCase(LATITUDE)) {
										lat = Integer.parseInt(position.getTextContent());
//									} else if(position.hasAttributes()) {
//										point.setOrder(position.getAttributes().getNamedItem(POINTORDER));
									}
								}
								area.addPoint(new GeoPoint(lat, lon));
							}
						}
					} else if (name.equalsIgnoreCase(NAME)) {
						area.setName(property.getTextContent());
//					} else if (name.equalsIgnoreCase(DESCRIPTION)) {
//						area.setDescription(property.getTextContent());
					}
				}
				areas.add(area);
			}
			return areas;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void write(List<AreaOverlay> areas) throws RuntimeException, IOException{
		XmlSerializer serializer = Xml.newSerializer();
		OutputStreamWriter writer = null;
		try {
			writer = new OutputStreamWriter(this.getOutputStream());
			serializer.setOutput(writer);
			serializer.startDocument(Encoding.UTF_8.name(), true);
			serializer.startTag("", AREAS);
			List<GeoPoint> points;
			for (AreaOverlay area:areas) {
				serializer.startTag("", AREA);
				serializer.startTag("", NAME);
				serializer.text(area.getName());
				serializer.endTag("", NAME);
//				serializer.startTag("", DESCRIPTION);
//				serializer.text(area.getDescription());
//				serializer.endTag("", DESCRIPTION);
				serializer.startTag("", POINTS);
				points = area.getPoints();
				for (int i = 0; i < points.size(); i++) {
					serializer.startTag("", POINT);
//					serializer.attribute("", POINTORDER, i);
					serializer.startTag("", LATITUDE);
					serializer.text("" + points.get(i).getLatitudeE6());
					serializer.endTag("", LATITUDE);
					serializer.startTag("", LONGITUDE);
					serializer.text("" + points.get(i).getLongitudeE6());
					serializer.endTag("", LONGITUDE);
					serializer.endTag("", POINT);
				}
				serializer.endTag("", POINTS);
				serializer.endTag("", AREA);
			}
			serializer.endTag("", AREAS);
			serializer.endDocument();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
				}
			}
		}
	}

}
