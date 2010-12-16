/**
 *   Copyright (C) 2009, 2010 
 *    Nicky Sandhu
 *    State of California,
 *    Department of Water Resources.
 *    This file is part of DSM2 Grid Map
 *    The DSM2 Grid Map is free software: 
 *    you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *    DSM2 Grid Map is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.

 *    You should have received a copy of the GNU General Public License
 *    along with DSM2 Grid Map.  If not, see <http://www.gnu.org/licenses>.
 */
package gov.ca.bdo.modeling.dsm2.map.client.map;

import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.event.PolygonCancelLineHandler;
import com.google.gwt.maps.client.event.PolygonClickHandler;
import com.google.gwt.maps.client.event.PolygonEndLineHandler;
import com.google.gwt.maps.client.event.PolygonLineUpdatedHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.PolyEditingOptions;
import com.google.gwt.maps.client.overlay.PolyStyleOptions;
import com.google.gwt.maps.client.overlay.Polygon;

/**
 * Creates a polyline and displays distance as polyline is drawn
 * 
 * @author psandhu
 * 
 */
public class MeasuringAreaInPolygon {
	private final MapWidget map;
	private Polygon polygon;
	private final String color = "#FF0000";
	private final int weight = 5;
	private final double opacity = 0.75;

	public MeasuringAreaInPolygon(MapWidget map) {
		this.map = map;
	}

	public void addPolyline() {
		PolyStyleOptions style = PolyStyleOptions.newInstance(color, weight,
				opacity);
		polygon = new Polygon(new LatLng[0]);
		map.addOverlay(polygon);
		polygon.setDrawingEnabled();
		polygon.setStrokeStyle(style);
		polygon.addPolygonClickHandler(new PolygonClickHandler() {

			public void onClick(PolygonClickEvent event) {
				displayArea();
			}
		});
		polygon.addPolygonLineUpdatedHandler(new PolygonLineUpdatedHandler() {

			public void onUpdate(PolygonLineUpdatedEvent event) {
				displayArea();
			}
		});

		polygon.addPolygonCancelLineHandler(new PolygonCancelLineHandler() {

			public void onCancel(PolygonCancelLineEvent event) {
				displayArea();
			}
		});

		polygon.addPolygonEndLineHandler(new PolygonEndLineHandler() {

			public void onEnd(PolygonEndLineEvent event) {
				displayArea();
			}
		});
	}

	public void displayArea() {
		LatLng vertex = polygon.getVertex(polygon.getVertexCount() - 1);
		if (vertex != null) {
			map.getInfoWindow().open(
					vertex,
					new InfoWindowContent("Area : " + getAreaInSquareFeet()
							+ " ft"));
		}
	}

	public double getAreaInSquareFeet() {
		return Math.round(polygon.getArea() * 3.2808399 * 3.2808399 * 100) / 100;
	}

	public void editPolygon() {
		if (polygon == null) {
			return;
		}
		// allow up to 10 vertices to exist in the line.
		polygon.setEditingEnabled(PolyEditingOptions.newInstance(10));
	}

	public void clearOverlay() {
		if (polygon != null) {
			map.removeOverlay(polygon);
		}
	}
}
