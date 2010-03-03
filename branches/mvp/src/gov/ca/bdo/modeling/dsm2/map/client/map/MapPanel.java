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

import gov.ca.dsm2.input.model.DSM2Model;
import gov.ca.dsm2.input.model.Gate;
import gov.ca.dsm2.input.model.Gates;
import gov.ca.dsm2.input.model.Node;
import gov.ca.dsm2.input.model.Reservoirs;
import gov.ca.modeling.dsm2.widgets.client.ExpandContractMapControl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.maps.client.Copyright;
import com.google.gwt.maps.client.CopyrightCollection;
import com.google.gwt.maps.client.MapUIOptions;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.TileLayer;
import com.google.gwt.maps.client.event.MapZoomEndHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.LatLngBounds;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.geom.Size;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.maps.client.overlay.Polyline;
import com.google.gwt.maps.client.overlay.TileLayerOverlay;
import com.google.gwt.user.client.ui.Composite;

public class MapPanel extends Composite {

	private NodeMarkerDataManager nodeManager;
	private ChannelLineDataManager channelManager;
	private MapWidget map;
	private DSM2Model model;
	private String currentStudy;
	private OutputMarkerDataManager outputMarkerDataManager;
	private boolean editMode = false;
	protected String[] studyNames = new String[0];
	private TileLayerOverlay bathymetryOverlay;
	private TextAnnotationsManager textAnnotationHandler;
	boolean SHOW_ON_CLICK = false;
	private GateOverlayManager gateOverlayManager;
	private ReservoirOverlayManager reservoirOverlayManager;
	private final GridVisibilityControl visibilityControl;

	public MapPanel() {
		setMap(new MapWidget(LatLng.newInstance(38.15, -121.70), 10));
		setOptions();
		new ClearBackgroundLayer(getMap(), false);
		new ClearBackgroundLayer(getMap(), true);
		visibilityControl = new GridVisibilityControl(this);
		getMap().addControl(visibilityControl);
		ExpandContractMapControl fullScreenControl = new ExpandContractMapControl();
		map.addControl(fullScreenControl);
		// add them all here
		initWidget(getMap());
		// add zoom handler to hide channels at a certain zoom level
		map.addMapZoomEndHandler(new MapZoomEndHandler() {

			public void onZoomEnd(MapZoomEndEvent event) {
				if ((event.getNewZoomLevel() <= 10)
						&& (event.getOldZoomLevel() > 10)) {
					hideChannelLines(true);
				}
				if ((event.getOldZoomLevel() >= 10)
						&& (event.getNewZoomLevel() > 10)) {
					hideChannelLines(visibilityControl.getHideChannels());
				}
			}
		});
	}

	private void setOptions() {
		MapUIOptions options = MapUIOptions.newInstance(getMap().getSize());
		options.setKeyboard(true);
		options.setMapTypeControl(false);
		options.setMenuMapTypeControl(true);
		options.setScaleControl(true);
		options.setScrollwheel(true);
		options.setSmallZoomControl3d(true);
		getMap().setUI(options);
	}

	public void populateGrid() {
		clearAllMarkers();
		setNodeManager(new NodeMarkerDataManager(model.getNodes()));
		setChannelManager(new ChannelLineDataManager(getNodeManager(), model
				.getChannels()));
		refreshGrid();
	}

	protected void refreshGrid() {
		clearAllMarkers();
		populateNodeMarkers();
		populateChannelLines();
		populateGateImages();
		populateReservoirMarkers();
		populateOutputMarkers();
		populateTextAnnotationMarkers();
	}

	private void populateTextAnnotationMarkers() {
		textAnnotationHandler = new TextAnnotationsManager(this);
	}

	private void populateOutputMarkers() {
		outputMarkerDataManager = new OutputMarkerDataManager();
		outputMarkerDataManager.setModel(model, this);
		outputMarkerDataManager.addMarkers(map);

	}

	protected void clearAllMarkers() {
		if (getNodeManager() != null) {
			getNodeManager().clearNodeMarkers(this);
		}
		if (getMap() != null) {
			getMap().clearOverlays();
		}
	}

	protected void populateNodeMarkers() {
		getNodeManager().displayNodeMarkers(this);
	}

	protected void populateChannelLines() {
		getChannelManager().addLines(this);
	}

	protected void populateGateImages() {
		Gates gates = model.getGates();
		gateOverlayManager = new GateOverlayManager();
		for (Gate gate : gates.getGates()) {
			// String id = gate.getFromIdentifier();
			String objectType = gate.getFromObject();
			String toNode = gate.getToNode();
			if (objectType.equalsIgnoreCase("channel")) {
				Node node = model.getNodes().getNode(toNode);
				if (node == null) {
					GWT.log("Node " + toNode + " is not available for gate "
							+ gate.getName(), null);
					node = new Node();
					node.setId(toNode);
					node.setLatitude(38.5);
					node.setLongitude(-121.5);
				}
				LatLng nodePoint = LatLng.newInstance(node.getLatitude(), node
						.getLongitude());
				if ((gate.getLatitude() == 0) || (gate.getLongitude() == 0)) {
					gate.setLatitude(nodePoint.getLatitude());
					gate.setLongitude(nodePoint.getLongitude());
				}
				LatLng gatePoint = LatLng.newInstance(gate.getLatitude(), gate
						.getLongitude());
				// Create our "tiny" marker icon
				Icon icon = Icon.newInstance("images/dam.png");
				// icon.setShadowURL("images/water.shadow.png");
				icon.setIconSize(Size.newInstance(32, 32));
				icon.setShadowSize(Size.newInstance(22, 20));
				icon.setIconAnchor(Point.newInstance(16, 20));
				icon.setInfoWindowAnchor(Point.newInstance(5, 1));
				MarkerOptions options = MarkerOptions.newInstance();
				options.setTitle(gate.getName());
				options.setIcon(icon);
				// -- edit mode options and only for the marker being
				// manipulated --
				options.setDragCrossMove(true);
				options.setDraggable(true);
				options.setClickable(true);
				options.setAutoPan(true);
				Marker gateOverMarker = new Marker(gatePoint, options);
				gateOverMarker.addMarkerClickHandler(new GateClickHandler(gate,
						this));
				gateOverMarker
						.addMarkerDragEndHandler(new GateDragHandler(gate));
				gateOverlayManager
						.addGateMarker(gate.getName(), gateOverMarker);
				getMap().addOverlay(gateOverMarker);
			}
		}
	}

	protected void populateReservoirMarkers() {
		Reservoirs reservoirs = model.getReservoirs();
		reservoirOverlayManager = new ReservoirOverlayManager(this);
		reservoirOverlayManager.setReservoirs(reservoirs);
		reservoirOverlayManager.displayReservoirMarkers();
	}

	public void hideMarkers(boolean hide) {
		if (hide) {
			getNodeManager().clearNodeMarkers(this);
		} else {
			getNodeManager().displayNodeMarkers(this);
		}
	}

	public void hideChannelLines(boolean hide) {
		for (Polyline line : getChannelManager().getPolylines()) {
			line.setVisible(!hide);
		}
	}

	public void hideGateMarkers(boolean hide) {
		gateOverlayManager.hideMarkers(hide);
	}

	public void hideReservoirMarkers(boolean hide) {
		reservoirOverlayManager.hideMarkers(hide);
	}

	public void hideOutputMarkers(boolean hide) {
		outputMarkerDataManager.hideMarkers(hide);
	}

	public void showBathymetry(boolean show) {
		if (bathymetryOverlay == null) {
			createBathymetryOverlay();
		}
		if (show) {
			map.addOverlay(bathymetryOverlay);
		} else {
			map.removeOverlay(bathymetryOverlay);
		}
	}

	private void createBathymetryOverlay() {
		CopyrightCollection myCopyright = new CopyrightCollection(
				"@ California DWR 2009");
		LatLng southWest = LatLng.newInstance(36.5, -123.0);
		LatLng northEast = LatLng.newInstance(39.5, -120.5);
		myCopyright.addCopyright(new Copyright(1, LatLngBounds.newInstance(
				southWest, northEast), 10, "@ Copyright California DWR"));
		TileLayer tileLayer = new TileLayer(myCopyright, 10, 17) {

			public double getOpacity() {
				return 0.6;
			}

			public String getTileURL(Point tile, int zoomLevel) {
				int version = (tile.getX() + tile.getY()) % 4 + 1;
				String uniqueValue = tile.getX() + "" + tile.getY() + ""
						+ zoomLevel;
				int hashCode = uniqueValue.hashCode();
				return "http://" + version
						+ ".latest.dsm2bathymetry.appspot.com/tiles/"
						+ hashCode + "_tile" + tile.getX() + "_" + tile.getY()
						+ "_" + zoomLevel + ".png";
			}

			public boolean isPng() {
				return true;
			}
		};
		bathymetryOverlay = new TileLayerOverlay(tileLayer);
	}

	public void centerAndZoomOnNode(String nodeId) {
		Marker marker = getNodeManager().getMarkerFor(nodeId);
		if (marker == null) {
			return;
		}
		LatLng point = marker.getLatLng();
		getMap().panTo(point);
		if (getMap().getZoomLevel() < 13) {
			getMap().setZoomLevel(13);
		}
	}

	public void centerAndZoomOnChannel(String channelId) {
		Polyline line = getChannelManager().getPolyline(channelId);
		if (line == null) {
			return;
		}
		LatLngBounds bounds = line.getBounds();
		getMap().panTo(bounds.getCenter());
		while (!getMap().getBounds().containsBounds(bounds)) {
			getMap().setZoomLevel(getMap().getZoomLevel() - 1);
		}
		new ChannelClickHandler(getChannelManager().getChannels().getChannel(
				channelId), this).onClick(null);
	}

	public MapWidget getMapWidget() {
		return getMap();
	}

	public void setNodeManager(NodeMarkerDataManager nodeManager) {
		this.nodeManager = nodeManager;
	}

	public NodeMarkerDataManager getNodeManager() {
		return nodeManager;
	}

	public void setChannelManager(ChannelLineDataManager channelManager) {
		this.channelManager = channelManager;
	}

	public ChannelLineDataManager getChannelManager() {
		return channelManager;
	}

	public void setMap(MapWidget map) {
		this.map = map;
	}

	public MapWidget getMap() {
		return map;
	}

	public void setStudy(String studyName) {
		currentStudy = studyName;
	}

	public String getCurrentStudy() {
		return currentStudy;
	}

	public boolean isInEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
		refreshGrid();
	}

	public String[] getStudyNames() {
		return studyNames;
	}

	public void turnOnTextAnnotation() {
		textAnnotationHandler.startAddingText();
	}

	public void turnOffTextAnnotation() {
		if (textAnnotationHandler != null) {
			textAnnotationHandler.stopAddingText();
		}
	}

	public void onResize() {
		map.checkResizeAndCenter();
	}

	public DSM2Model getModel() {
		return model;
	}

	public void setModel(DSM2Model model) {
		this.model = model;
	}
}
