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
