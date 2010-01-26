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
package gov.ca.bdo.modeling.dsm2.map.client.model;

import gov.ca.dsm2.input.model.Gates;

import java.util.HashMap;

import com.google.gwt.maps.client.overlay.Marker;

public class GateOverlayManager {
	private Gates gates;
	private final HashMap<String, Marker> gateMarkerMap;

	public GateOverlayManager() {
		gateMarkerMap = new HashMap<String, Marker>();

	}

	public void setGates(Gates gates) {
		this.gates = gates;
		gateMarkerMap.clear();
	}

	public void addGateMarker(String gateId, Marker overlay) {
		gateMarkerMap.put(gateId, overlay);
	}

	public Marker getGateMarker(String gateId) {
		return gateMarkerMap.get(gateId);
	}

	public void removeGateMarker(String gateId) {
		gateMarkerMap.remove(gateId);
	}

	public void hideMarkers(boolean hide) {
		for (Marker marker : gateMarkerMap.values()) {
			marker.setVisible(!hide);
		}
	}
}
