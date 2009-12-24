package gov.ca.bdo.modeling.dsm2.map.client.model;

import gov.ca.bdo.modeling.dsm2.map.client.MapPanel;
import gov.ca.bdo.modeling.dsm2.map.client.MarkNewNodePosition;
import gov.ca.dsm2.input.model.Node;
import gov.ca.dsm2.input.model.Nodes;

import java.util.Collection;
import java.util.HashMap;

import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.geom.Size;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.maps.utility.client.labeledmarker.LabeledMarker;
import com.google.gwt.maps.utility.client.labeledmarker.LabeledMarkerOptions;
import com.google.gwt.maps.utility.client.markerclusterer.MarkerClusterer;
import com.google.gwt.maps.utility.client.markerclusterer.MarkerClustererOptions;

public class NodeMarkerDataManager {
	private Nodes nodes;
	private final HashMap<String, Marker> markerMap;
	private boolean isLabeled = true;
	private MarkerClusterer markerClusterer;

	public NodeMarkerDataManager(Nodes nodes) {
		this.nodes = nodes;
		markerMap = new HashMap<String, Marker>();
	}

	public void clear() {
		nodes = new Nodes();
		markerMap.clear();
	}

	public Collection<Node> getAllNodes() {
		return nodes.getNodes();
	}

	public void addNode(Node node) {
		nodes.addNode(node);
	}

	public Node getNodeData(String id) {
		return nodes.getNode(id);
	}

	public void addMarker(Node mapMarkerData, Marker marker) {
		markerMap.put(mapMarkerData.getId(), marker);
	}

	public Marker getMarkerFor(String nodeId) {
		return markerMap.get(nodeId);
	}

	public Nodes getNodes() {
		return nodes;
	}

	private Icon createNodeIcon() {
		if (isLabeled) {
			Icon icon = Icon.newInstance("images/greencirclemarker.png");
			icon.setIconSize(Size.newInstance(22, 22));
			icon.setIconAnchor(Point.newInstance(11, 11));
			icon.setInfoWindowAnchor(Point.newInstance(11, 7));
			return icon;
		} else {
			// Create our "tiny" marker icon
			Icon icon = Icon
					.newInstance("http://labs.google.com/ridefinder/images/mm_20_blue.png");
			icon
					.setShadowURL("http://labs.google.com/ridefinder/images/mm_20_shadow.png");
			icon.setIconSize(Size.newInstance(12, 20));
			icon.setShadowSize(Size.newInstance(22, 20));
			icon.setIconAnchor(Point.newInstance(6, 20));
			icon.setInfoWindowAnchor(Point.newInstance(5, 1));
			return icon;
		}
	}

	/**
	 * adds node markers to map. Note: does not remove old markers @see
	 * clearNodeMarkers
	 * 
	 * @param mapPanel
	 */
	public void displayNodeMarkers(MapPanel mapPanel) {
		if (mapPanel.isInEditMode()) {
			isLabeled = false;
		} else {
			isLabeled = true;
		}
		Icon icon = createNodeIcon();
		MarkNewNodePosition dragEndHandler = new MarkNewNodePosition(mapPanel);
		for (Node mapMarkerData : getAllNodes()) {
			MarkerOptions options = null;
			if (isLabeled) {
				LabeledMarkerOptions opts = LabeledMarkerOptions.newInstance();
				opts.setLabelOffset(Size.newInstance(-5, -5));
				opts.setLabelText(mapMarkerData.getId());
				opts.setLabelClass("hm-marker-label");
				options = opts;
			} else {
				options = MarkerOptions.newInstance();
			}
			options.setTitle(mapMarkerData.getId());
			options.setIcon(icon);
			// -- edit mode options and only for the marker being
			// manipulated --
			if (mapPanel.isInEditMode()) {
				options.setDragCrossMove(mapPanel.isInEditMode());
				options.setDraggable(mapPanel.isInEditMode());
				options.setClickable(mapPanel.isInEditMode());
				options.setAutoPan(mapPanel.isInEditMode());
			}
			// -- edit mode options
			Marker marker = null;
			LatLng point = LatLng.newInstance(mapMarkerData.getLatitude(),
					mapMarkerData.getLongitude());
			if (isLabeled) {
				marker = new LabeledMarker(point,
						(LabeledMarkerOptions) options);
			} else {
				marker = new Marker(point, options);
			}
			if (mapPanel.isInEditMode()) {
				marker.addMarkerDragEndHandler(dragEndHandler);
			}
			addMarker(mapMarkerData, marker);
			if (!isLabeled) {
				mapPanel.getMap().addOverlay(marker);
			}
		}
		if (isLabeled) {
			MarkerClustererOptions clusterOptions = MarkerClustererOptions
					.newInstance();
			clusterOptions.setGridSize(100);
			clusterOptions.setMaxZoom(12);
			Marker[] markers = new Marker[getAllNodes().size()];
			int i = 0;
			for (Marker marker : markerMap.values()) {
				markers[i++] = marker;
			}
			markerClusterer = MarkerClusterer.newInstance(mapPanel.getMap(),
					markers, clusterOptions);
		}
	}

	/**
	 * clears node markers
	 * 
	 * @param mapPanel
	 */
	public void clearNodeMarkers(MapPanel mapPanel) {
		if (markerClusterer != null) {
			markerClusterer.clearMarkers();
			markerClusterer = null;
		}
		for (Marker marker : markerMap.values()) {
			mapPanel.getMap().removeOverlay(marker);
		}
	}

}
