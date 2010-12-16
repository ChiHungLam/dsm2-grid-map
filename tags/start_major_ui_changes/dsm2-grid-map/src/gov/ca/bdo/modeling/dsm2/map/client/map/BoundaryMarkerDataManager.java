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

import gov.ca.dsm2.input.model.BoundaryInput;
import gov.ca.dsm2.input.model.BoundaryInputs;
import gov.ca.dsm2.input.model.DSM2Model;
import gov.ca.dsm2.input.model.Node;

import java.util.HashMap;

import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.geom.Size;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;

public class BoundaryMarkerDataManager {
	private DSM2Model model;
	private final HashMap<String, Marker> markerMap;
	private final HashMap<String, String> nameMap;
	private MapPanel mapPanel;

	public BoundaryMarkerDataManager() {
		nameMap = new HashMap<String, String>();
		markerMap = new HashMap<String, Marker>();
	}

	public void setModel(DSM2Model model, MapPanel mapPanel) {
		this.model = model;
		this.mapPanel = mapPanel;
	}

	public void addMarkers(MapWidget map) {
		BoundaryInputs boundaryInputs = model.getInputs();
		// FIXME: add a type field to boundary input to avoid the three loops
		// below
		for (BoundaryInput flowInput : boundaryInputs.getFlowInputs()) {
			String nodeId = flowInput.nodeId;
			Node node = model.getNodes().getNode(nodeId);
			if (node == null) {
				continue;
			}
			LatLng position = LatLng.newInstance(node.getLatitude(), node
					.getLongitude());
			addMarkerToMap(mapPanel.getMap(), position, flowInput, "flow");
		}
		for (BoundaryInput flowInput : boundaryInputs.getSourceFlowInputs()) {
			String nodeId = flowInput.nodeId;
			Node node = model.getNodes().getNode(nodeId);
			if (node == null) {
				continue;
			}
			LatLng position = LatLng.newInstance(node.getLatitude(), node
					.getLongitude());
			addMarkerToMap(mapPanel.getMap(), position, flowInput, "sourcesink");
		}
		for (BoundaryInput stageInput : boundaryInputs.getStageInputs()) {
			String nodeId = stageInput.nodeId;
			Node node = model.getNodes().getNode(nodeId);
			if (node == null) {
				continue;
			}
			LatLng position = LatLng.newInstance(node.getLatitude(), node
					.getLongitude());
			addMarkerToMap(mapPanel.getMap(), position, stageInput, "stage");
		}
	}

	private void addMarkerToMap(MapWidget map, LatLng position,
			BoundaryInput input, String type) {
		final String inputName = input.name;
		if (nameMap.containsKey(inputName)) {
			String newValue = nameMap.get(inputName);
			nameMap.put(inputName, newValue);
		} else {
			nameMap.put(inputName, type);
		}
		if (markerMap.containsKey(inputName)) {
			return;
		}
		// Create our "tiny" marker icon
		String imageURL = "images/source.png";
		if (type.equals("sourcesink")) {
			if (input.sign == 1) {
				imageURL = "images/source.png";
			} else {
				imageURL = "images/sink.png";
			}
		} else if (type.equals("flow")) {
			imageURL = "images/boundary_flow.png";
		} else if (type.equals("stage")) {
			imageURL = "images/boundary_stage.png";
		} else {
			imageURL = "images/boundary_generic.png";
		}
		Icon icon = Icon.newInstance(imageURL);
		icon.setShadowURL("images/shadow-marker.png");
		if (type.equals("sourcesink")) {
			icon.setIconSize(Size.newInstance(16, 16));
			icon.setShadowSize(Size.newInstance(16, 16));
			if (input.name.contains("div")) {
				icon.setIconAnchor(Point.newInstance(24, 17));
			} else if (input.name.contains("seep")) {
				icon.setIconAnchor(Point.newInstance(-8, 17));
			} else if (input.name.contains("drain")) {
				icon.setIconAnchor(Point.newInstance(-8, 0));
			}
			icon.setInfoWindowAnchor(Point.newInstance(38, 34));
		} else {
			icon.setIconSize(Size.newInstance(40, 40));
			icon.setShadowSize(Size.newInstance(38, 34));
			icon.setIconAnchor(Point.newInstance(50, 25));
			icon.setInfoWindowAnchor(Point.newInstance(30, 15));
		}
		MarkerOptions options = MarkerOptions.newInstance();
		options.setTitle(input.name);
		options.setIcon(icon);
		options.setClickable(true);
		Marker marker = new Marker(position, options);
		markerMap.put(inputName, marker);
		map.addOverlay(marker);
	}

	public void hideMarkers(boolean hide) {
		for (String name : markerMap.keySet()) {
			if (hide) {
				mapPanel.getMap().removeOverlay(markerMap.get(name));
			} else {
				mapPanel.getMap().addOverlay(markerMap.get(name));
			}
		}
	}

}
