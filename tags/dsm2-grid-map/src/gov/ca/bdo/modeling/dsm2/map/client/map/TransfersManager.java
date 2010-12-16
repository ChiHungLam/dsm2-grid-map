package gov.ca.bdo.modeling.dsm2.map.client.map;

import gov.ca.dsm2.input.model.DSM2Model;
import gov.ca.dsm2.input.model.Node;
import gov.ca.dsm2.input.model.Reservoir;
import gov.ca.dsm2.input.model.Transfer;

import java.util.HashMap;

import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Overlay;

public class TransfersManager {
	private MapWidget map;
	private DSM2Model model;
	private HashMap<String, Overlay> lineMap;

	public TransfersManager(MapWidget map, DSM2Model model) {
		this.map = map;
		this.model = model;
		lineMap = new HashMap<String, Overlay>();
	}

	public void addLines() {
		for (Transfer transfer : model.getTransfers().getTransfers()) {
			Object objectFromTypeAndIdentifier = model
					.getObjectFromTypeAndIdentifier(transfer.fromObject,
							transfer.fromIdentifier);
			Object objectToTypeAndIdentifier = model
					.getObjectFromTypeAndIdentifier(transfer.toObject,
							transfer.toIdentifier);
			LatLng fromPoint = getLatLngFromObject(objectFromTypeAndIdentifier);
			LatLng toPoint = getLatLngFromObject(objectToTypeAndIdentifier);

			DirectionalPolyline dline = new DirectionalPolyline(transfer.name,
					fromPoint, toPoint);
			lineMap.put(transfer.name, dline);
			map.addOverlay(dline);
		}
	}

	public void hideTransfers(boolean hide) {
		for (Overlay overlay : lineMap.values()) {
			if (hide) {
				map.removeOverlay(overlay);
			} else {
				map.addOverlay(overlay);
			}
		}
	}

	private LatLng getLatLngFromObject(Object obj) {
		double latitude = 0;
		double longitude = 0;
		if (obj instanceof Node) {
			Node node = (Node) obj;
			latitude = node.getLatitude();
			longitude = node.getLongitude();
		} else if (obj instanceof Reservoir) {
			Reservoir reservoir = (Reservoir) obj;
			latitude = reservoir.getLatitude();
			longitude = reservoir.getLongitude();
		} else {
			// do nothing?
		}
		return LatLng.newInstance(latitude, longitude);
	}
}
