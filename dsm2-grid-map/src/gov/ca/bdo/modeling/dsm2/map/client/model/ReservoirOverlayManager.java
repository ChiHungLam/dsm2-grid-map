package gov.ca.bdo.modeling.dsm2.map.client.model;

import gov.ca.dsm2.input.model.Reservoirs;

import java.util.HashMap;

import com.google.gwt.maps.client.overlay.Marker;

public class ReservoirOverlayManager {
	private Reservoirs reservoirs;
	private final HashMap<String, Marker> reservoirMarkerMap;

	public ReservoirOverlayManager() {
		reservoirMarkerMap = new HashMap<String, Marker>();

	}

	public void setReservoirs(Reservoirs reservoirs) {
		this.reservoirs = reservoirs;
		reservoirMarkerMap.clear();
	}

	public void addReservoirMarker(String reservoirId, Marker overlay) {
		reservoirMarkerMap.put(reservoirId, overlay);
	}

	public Marker getReservoirMarker(String reservoirId) {
		return reservoirMarkerMap.get(reservoirId);
	}

	public void removeReservoirMarker(String reservoirId) {
		reservoirMarkerMap.remove(reservoirId);
	}

	public void hideMarkers(boolean hide) {
		for (Marker marker : reservoirMarkerMap.values()) {
			marker.setVisible(!hide);
		}
	}
}
