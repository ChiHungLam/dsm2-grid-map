package gov.ca.bdo.modeling.dsm2.map.client;

import gov.ca.dsm2.input.model.Gate;

import com.google.gwt.maps.client.event.MarkerDragEndHandler;
import com.google.gwt.maps.client.geom.LatLng;

public class GateDragHandler implements MarkerDragEndHandler {

	private Gate gate;

	public GateDragHandler(Gate gate) {
		this.gate = gate;
	}

	public void onDragEnd(MarkerDragEndEvent event) {
		LatLng latLng = event.getSender().getLatLng();
		gate.setLatitude(latLng.getLatitude());
		gate.setLongitude(latLng.getLongitude());
	}

}
