package gov.ca.bdo.modeling.dsm2.map.client.model;

import gov.ca.bdo.modeling.dsm2.map.client.MapPanel;
import gov.ca.bdo.modeling.dsm2.map.client.ReservoirClickHandler;
import gov.ca.bdo.modeling.dsm2.map.client.ReservoirDragHandler;
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
		reservoirMarkerMap.remove(reservoirId);
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
			Marker reservoirMarker = createReservoirMarker(reservoir);
			addReservoirMarker(reservoir.getName(), reservoirMarker);
			mapPanel.getMap().addOverlay(reservoirMarker);
			addReservoirConnectionLines(reservoir);
		}
	}

	public Marker createReservoirMarker(Reservoir reservoir) {
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

	public void addReservoirConnectionLines(Reservoir reservoir) {
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

	public void removeReserviorConnectionLines(String reservoirId) {
		for (String key : reservoirConnnectionLineMap.keySet()) {
			String reservoirName = key.split("_")[0];
			if (reservoirName.equals(reservoirId)) {
				mapPanel.getMap().removeOverlay(
						reservoirConnnectionLineMap.get(key));
			}
		}
	}
}
