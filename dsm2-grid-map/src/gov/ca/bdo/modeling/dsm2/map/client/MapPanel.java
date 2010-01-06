package gov.ca.bdo.modeling.dsm2.map.client;

import gov.ca.bdo.modeling.dsm2.map.client.model.ChannelLineDataManager;
import gov.ca.bdo.modeling.dsm2.map.client.model.GateOverlayManager;
import gov.ca.bdo.modeling.dsm2.map.client.model.NodeMarkerDataManager;
import gov.ca.bdo.modeling.dsm2.map.client.model.OutputMarkerDataManager;
import gov.ca.bdo.modeling.dsm2.map.client.model.ReservoirOverlayManager;
import gov.ca.bdo.modeling.dsm2.map.client.model.TextAnnotationsManager;
import gov.ca.bdo.modeling.dsm2.map.client.service.DSM2InputService;
import gov.ca.bdo.modeling.dsm2.map.client.service.DSM2InputServiceAsync;
import gov.ca.dsm2.input.model.Channel;
import gov.ca.dsm2.input.model.DSM2Model;
import gov.ca.dsm2.input.model.Gate;
import gov.ca.dsm2.input.model.Gates;
import gov.ca.dsm2.input.model.Node;
import gov.ca.dsm2.input.model.Reservoir;
import gov.ca.dsm2.input.model.ReservoirConnection;
import gov.ca.dsm2.input.model.Reservoirs;

import java.util.List;

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
import com.google.gwt.maps.client.overlay.PolyStyleOptions;
import com.google.gwt.maps.client.overlay.Polyline;
import com.google.gwt.maps.client.overlay.TileLayerOverlay;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MapPanel extends Composite {

	public static String CHANNEL_COLOR_PLAIN = "Plain";
	public static String CHANNEL_COLOR_MANNINGS = "Mannings";
	public static String CHANNEL_COLOR_DISPERSION = "Dispersion";
	private NodeMarkerDataManager nodeManager;
	private ChannelLineDataManager channelManager;
	private MapWidget map;
	private final DSM2InputServiceAsync dsm2InputService;
	private DSM2Model model;
	private final FlowPanel infoPanel;
	private String currentStudy;
	private OutputMarkerDataManager outputMarkerDataManager;
	private boolean editMode = false;
	protected String[] studyNames = new String[0];
	private final MapControlPanel controlPanel;
	private TileLayerOverlay bathymetryOverlay;
	private TextAnnotationsManager textAnnotationHandler;

	public MapPanel() {
		dsm2InputService = (DSM2InputServiceAsync) GWT
				.create(DSM2InputService.class);
		setMap(new MapWidget(LatLng.newInstance(38.15, -121.70), 10));
		setOptions();
		new ClearBackgroundLayer(getMap());
		visibilityControl = new GridVisibilityControl(this);
		getMap().addControl(visibilityControl);
		// layout top level things here
		controlPanel = new MapControlPanel(this);
		infoPanel = new FlowPanel();
		infoPanel.setStyleName("infoPanel");
		infoPanel.setWidth("646px");
		infoPanel.setHeight("400px");
		controlPanelContainer = new VerticalPanel();
		controlPanelContainer.add(controlPanel);
		controlPanelContainer.add(infoPanel);
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
		loadStudies();
	}

	public Panel getControlPanelContainer() {
		return controlPanelContainer;
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

	private void requestData() {
		dsm2InputService.getInputModel(currentStudy,
				new AsyncCallback<DSM2Model>() {

					public void onSuccess(DSM2Model result) {
						model = result;
						populateGrid();
					}

					public void onFailure(Throwable caught) {
						System.err.println(caught);
					}
				});
	}

	protected void populateGrid() {
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
				Icon icon = Icon.newInstance("images/postoffice-jp.png");
				icon.setShadowURL("images/postoffice-jp.shadow.png");
				icon.setIconSize(Size.newInstance(12, 20));
				icon.setShadowSize(Size.newInstance(22, 20));
				icon.setIconAnchor(Point.newInstance(6, 20));
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
		reservoirOverlayManager = new ReservoirOverlayManager();
		for (Reservoir reservoir : reservoirs.getReservoirs()) {
			if ((reservoir.getLatitude() == 0)
					|| (reservoir.getLongitude() == 0)) {
				List<ReservoirConnection> connections = reservoir
						.getReservoirConnections();
				double latitude = 0.0;
				double longitude = 0.0;
				for (ReservoirConnection reservoirConnection : connections) {
					String nodeId = reservoirConnection.nodeId;
					Node node = getNodeManager().getNodeData(nodeId);
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
			// Create our "tiny" marker icon
			Icon icon = Icon.newInstance("images/water.png");
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
					reservoir, this));
			reservoirMarker.addMarkerDragEndHandler(new ReservoirDragHandler(
					reservoir));
			reservoirOverlayManager.addReservoirMarker(reservoir.getName(),
					reservoirMarker);
			getMap().addOverlay(reservoirMarker);
		}
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

	public void setChannelColorScheme(String colorScheme) {
		for (Channel channelData : getChannelManager().getChannels()
				.getChannels()) {
			Polyline line = getChannelManager()
					.getPolyline(channelData.getId());
			String lineColor = getColorForScheme(colorScheme, channelData);
			PolyStyleOptions style = PolyStyleOptions.newInstance(lineColor);
			style.setOpacity(0.75);
			style.setWeight(3);
			line.setStrokeStyle(style);
		}
	}

	private String getColorForScheme(String colorScheme, Channel data) {
		if (colorScheme.equals(CHANNEL_COLOR_PLAIN)) {
			return "#110077";
		} else if (colorScheme.equals(CHANNEL_COLOR_MANNINGS)) {
			return getColorForRange(data.getMannings(), 0.01, 0.05);
		} else if (colorScheme.equals(CHANNEL_COLOR_DISPERSION)) {
			return getColorForRange(data.getDispersion(), 0.01, 1.5);
		} else {
			return "#110077";
		}
	}

	private String getColorForRange(double mannings, double min, double max) {
		int color = (int) Math.round((mannings - min) / (max - min) * 255);
		if (color < 0) {
			color = 0;
		} else if (color > 255) {
			color = 255;
		}
		return "#" + Integer.toHexString(color) + "0077";
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
		requestData();
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

	boolean SHOW_ON_CLICK = false;
	private final VerticalPanel controlPanelContainer;
	private GateOverlayManager gateOverlayManager;
	private ReservoirOverlayManager reservoirOverlayManager;
	private final GridVisibilityControl visibilityControl;

	public Panel getInfoPanel() {
		return infoPanel;
	}

	public String[] getStudyNames() {
		return studyNames;
	}

	private void loadStudies() {
		dsm2InputService.getStudyNames(new AsyncCallback<String[]>() {

			public void onSuccess(String[] result) {
				studyNames = result;
				controlPanel.setStudies(studyNames);
				if (studyNames.length > 0) {
					setStudy(studyNames[0]);
				}
			}

			public void onFailure(Throwable caught) {
				studyNames = new String[0];
			}
		});
	}

	public void saveCurrentStudy() {
		dsm2InputService.saveModel(currentStudy, model,
				new AsyncCallback<Void>() {

					public void onSuccess(Void result) {
						Window.setStatus("Saved study " + currentStudy);
					}

					public void onFailure(Throwable caught) {
						Window
								.setStatus("Could not save study "
										+ currentStudy);
					}
				});

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
}
