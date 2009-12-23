package gov.ca.bdo.modeling.dsm2.map.client;

import com.google.gwt.maps.client.event.MarkerDragEndHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Marker;

public class MarkNewPosition implements MarkerDragEndHandler {
	public static String GATE_TYPE = "gate";
	public static String RESERVOIR_TYPE = "res";
	private String type;

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
