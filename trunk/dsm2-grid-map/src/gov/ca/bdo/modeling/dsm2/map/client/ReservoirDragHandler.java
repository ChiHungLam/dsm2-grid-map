package gov.ca.bdo.modeling.dsm2.map.client;

import gov.ca.dsm2.input.model.Reservoir;

import com.google.gwt.maps.client.event.MarkerDragEndHandler;
import com.google.gwt.maps.client.geom.LatLng;

public class ReservoirDragHandler implements MarkerDragEndHandler {

	private Reservoir reservoir;

	public ReservoirDragHandler(Reservoir reservoir) {
		this.reservoir = reservoir;
	}

	public void onDragEnd(MarkerDragEndEvent event) {
		LatLng latLng = event.getSender().getLatLng();
		reservoir.setLatitude(latLng.getLatitude());
		reservoir.setLongitude(latLng.getLongitude());
	}

}
