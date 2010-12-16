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
import com.google.gwt.maps.client.event.PolylineCancelLineHandler;
import com.google.gwt.maps.client.event.PolylineClickHandler;
import com.google.gwt.maps.client.event.PolylineEndLineHandler;
import com.google.gwt.maps.client.event.PolylineLineUpdatedHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.PolyStyleOptions;
import com.google.gwt.maps.client.overlay.Polyline;

/**
 * Creates a polyline and displays distance as polyline is drawn
 * 
 * @author psandhu
 * 
 */
public class MeasuringDistanceAlongLine {
	private final MapWidget map;
	private Polyline line;
	private final String color = "#FF0000";
	private final int weight = 5;
	private final double opacity = 0.75;

	public MeasuringDistanceAlongLine(MapWidget map) {
		this.map = map;
	}

	public void addPolyline() {
		PolyStyleOptions style = PolyStyleOptions.newInstance(color, weight,
				opacity);
		line = new Polyline(new LatLng[0]);
		map.addOverlay(line);
		line.setDrawingEnabled();
		line.setStrokeStyle(style);
		line.addPolylineClickHandler(new PolylineClickHandler() {

			public void onClick(PolylineClickEvent event) {
				displayLength();
			}
		});
		line.addPolylineLineUpdatedHandler(new PolylineLineUpdatedHandler() {

			public void onUpdate(PolylineLineUpdatedEvent event) {
				displayLength();
			}
		});

		line.addPolylineCancelLineHandler(new PolylineCancelLineHandler() {

			public void onCancel(PolylineCancelLineEvent event) {
				displayLength();
			}
		});

		line.addPolylineEndLineHandler(new PolylineEndLineHandler() {

			public void onEnd(PolylineEndLineEvent event) {
				displayLength();
			}
		});
	}

	public double getLengthInFeet() {
		return Math.round(line.getLength() * 3.2808399 * 100) / 100;
	}

	public void clearOverlay() {
		if (line != null) {
			map.removeOverlay(line);
		}
	}

	public void displayLength() {
		LatLng vertex = line.getVertex(line.getVertexCount() - 1);
		map.getInfoWindow().open(vertex,
				new InfoWindowContent("Length: " + getLengthInFeet() + " ft"));
	}
}
