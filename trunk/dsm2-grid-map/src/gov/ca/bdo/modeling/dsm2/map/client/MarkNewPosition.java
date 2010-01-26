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
package gov.ca.bdo.modeling.dsm2.map.client;

import com.google.gwt.maps.client.event.MarkerDragEndHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Marker;

public class MarkNewPosition implements MarkerDragEndHandler {
	public static String GATE_TYPE = "gate";
	public static String RESERVOIR_TYPE = "res";
	private final String type;

	public MarkNewPosition(String type) {
		this.type = type;
	}

	public void onDragEnd(MarkerDragEndEvent event) {
		Marker marker = event.getSender();
		LatLng latLng = marker.getLatLng();
		String id = marker.getTitle();
		if (type.equals(GATE_TYPE)) {
			System.out.println("gate: " + id + " lat:" + latLng.getLatitude()
					+ " lng:" + latLng.getLongitude());
		} else if (type.equals(RESERVOIR_TYPE)) {
			System.out.println("reservoir: " + id + " lat:"
					+ latLng.getLatitude() + " lng:" + latLng.getLongitude());

		}
	}

}
