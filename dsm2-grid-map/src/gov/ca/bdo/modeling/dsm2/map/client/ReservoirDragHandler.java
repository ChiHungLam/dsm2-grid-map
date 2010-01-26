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

import gov.ca.bdo.modeling.dsm2.map.client.model.ReservoirOverlayManager;
import gov.ca.dsm2.input.model.Reservoir;

import com.google.gwt.maps.client.event.MarkerDragEndHandler;
import com.google.gwt.maps.client.geom.LatLng;

public class ReservoirDragHandler implements MarkerDragEndHandler {

	private final Reservoir reservoir;
	private final ReservoirOverlayManager manager;

	public ReservoirDragHandler(ReservoirOverlayManager manager,
			Reservoir reservoir) {
		this.reservoir = reservoir;
		this.manager = manager;
	}

	public void onDragEnd(MarkerDragEndEvent event) {
		LatLng latLng = event.getSender().getLatLng();
		reservoir.setLatitude(latLng.getLatitude());
		reservoir.setLongitude(latLng.getLongitude());
		manager.removeReserviorConnectionLines(reservoir.getName());
		manager.addReservoirConnectionLines(reservoir);
	}

}
