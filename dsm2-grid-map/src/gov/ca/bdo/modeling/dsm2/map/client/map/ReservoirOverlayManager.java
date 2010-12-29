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

import gov.ca.dsm2.input.model.Node;
import gov.ca.dsm2.input.model.Reservoir;
import gov.ca.dsm2.input.model.ReservoirConnection;
import gov.ca.dsm2.input.model.Reservoirs;

import java.util.HashMap;
import java.util.List;

import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.geom.Size;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.maps.client.overlay.Polyline;

public class ReservoirOverlayManager {
	private Reservoirs reservoirs;
	private final HashMap<String, Marker> reservoirMarkerMap;
	private final MapPanel mapPanel;
	private final HashMap<String, Polyline> reservoirConnnectionLineMap;

	public ReservoirOverlayManager(MapPanel mapPanel) {
		this.mapPanel = mapPanel;
		reservoirMarkerMap = new HashMap<String, Marker>();
		reservoirConnnectionLineMap = new HashMap<String, Polyline>();
	}

	public void setReservoirs(Reservoirs reservoirs) {
		this.reservoirs = reservoirs;
		for (Reservoir reservoir : reservoirs.getReservoirs()) {
			if ((reservoir.getLatitude() == 0)
					|| (reservoir.getLongitude() == 0)) {
				List<ReservoirConnection> connections = reservoir
						.getReservoirConnections();
				double latitude = 0.0;
				double longitude = 0.0;
				for (ReservoirConnection reservoirConnection : connections) {
					String nodeId = reservoirConnection.nodeId;
					Node node = mapPanel.getNodeManager().getNodeData(nodeId);
					latitude += node.getLatitude();
					longitude += node.getLongitude();
				}
				int nconnections = connections.size();
				if (nconnections > 0) {
					latitude = latitude / nconnections;
					longitude = longitude / nconnections;
				}
				reservoir.setLatitude(latitude);
				reservoir.setLongitude(longitude);
			}
		}
		reservoirMarkerMap.clear();
		reservoirConnnectionLineMap.clear();
	}

	public void addReservoirMarker(String reservoirId, Marker overlay) {
		reservoirMarkerMap.put(reservoirId, overlay);
	}

	public Marker getReservoirMarker(String reservoirId) {
		return reservoirMarkerMap.get(reservoirId);
	}

	public void removeReservoirMarker(String reservoirId) {
		Marker marker = reservoirMarkerMap.remove(reservoirId);
		if (marker != null) {
			mapPanel.getMap().removeOverlay(marker);
		}
		removeReserviorConnectionLines(reservoirId);
	}

	public void hideMarkers(boolean hide) {
		for (String reservoirId : reservoirMarkerMap.keySet()) {
			reservoirMarkerMap.get(reservoirId).setVisible(!hide);
		}
		hideConnectionLines(hide);
	}

	public void hideConnectionLines(boolean hide) {
		for (Polyline line : reservoirConnnectionLineMap.values()) {
			line.setVisible(!hide);
		}
	}

	public void displayReservoirMarkers() {
		for (Reservoir reservoir : reservoirs.getReservoirs()) {
			addReservoirMarker(reservoir);
		}
	}

	public void addReservoirMarker(Reservoir reservoir) {
		Marker reservoirMarker = createReservoirMarker(reservoir);
		addReservoirMarker(reservoir.getName(), reservoirMarker);
		mapPanel.getMap().addOverlay(reservoirMarker);
		addReservoirConnectionLines(reservoir);
	}

	private Marker createReservoirMarker(Reservoir reservoir) {
		// Create our "tiny" marker icon
		Icon icon = Icon.newInstance("images/lake.png");
		icon.setShadowURL("images/water.shadow.png");
		icon.setIconSize(Size.newInstance(32, 32));
		icon.setShadowSize(Size.newInstance(22, 20));
		icon.setIconAnchor(Point.newInstance(16, 20));
		icon.setInfoWindowAnchor(Point.newInstance(5, 1));
		MarkerOptions options = MarkerOptions.newInstance();
		options.setTitle(reservoir.getName());
		options.setIcon(icon);
		// -- edit mode options and only for the marker being manipulated --
		options.setDragCrossMove(true);
		options.setDraggable(true);
		options.setClickable(true);
		options.setAutoPan(true);
		Marker reservoirMarker = new Marker(LatLng.newInstance(reservoir
				.getLatitude(), reservoir.getLongitude()), options);
		reservoirMarker.addMarkerClickHandler(new ReservoirClickHandler(
				reservoir, mapPanel));
		reservoirMarker.addMarkerDragEndHandler(new ReservoirDragHandler(this,
				reservoir));
		return reservoirMarker;
	}

	private void addReservoirConnectionLines(Reservoir reservoir) {
		// add connection lines
		LatLng reservoirPoint = LatLng.newInstance(reservoir.getLatitude(),
				reservoir.getLongitude());
		for (ReservoirConnection reservoirConnection : reservoir
				.getReservoirConnections()) {
			String nodeId = reservoirConnection.nodeId;
			Node node = mapPanel.getNodeManager().getNodeData(nodeId);
			LatLng nodePoint = LatLng.newInstance(node.getLatitude(), node
					.getLongitude());
			Polyline connectionLine = new Polyline(new LatLng[] { nodePoint,
					reservoirPoint }, "green", 4);
			mapPanel.getMap().addOverlay(connectionLine);
			reservoirConnnectionLineMap.put(reservoir.getName() + "_" + nodeId,
					connectionLine);
		}
	}

	private void removeReserviorConnectionLines(String reservoirId) {
		for (String key : reservoirConnnectionLineMap.keySet()) {
			String reservoirName = key.split("_")[0];
			if (reservoirName.equals(reservoirId)) {
				mapPanel.getMap().removeOverlay(
						reservoirConnnectionLineMap.get(key));
			}
		}
		reservoirConnnectionLineMap.clear();
	}

	public int getNumberOfReservoirs() {
		return reservoirs.getReservoirs().size();
	}

	public void addReservoir(Reservoir r) {
		reservoirs.addReservoir(r);
		addReservoirMarker(r);
	}

	public void refresh(Reservoir reservoir) {
		removeReserviorConnectionLines(reservoir.getName());
		addReservoirConnectionLines(reservoir);
	}

	public void removeReservoir(Reservoir reservoir) {
		String reservoirId = reservoir.getName();
		removeReservoirMarker(reservoirId);
		removeReserviorConnectionLines(reservoirId);
		reservoirs.removeReservoir(reservoir);
	}

	public Reservoirs getReservoirs() {
		return reservoirs;
	}
}
