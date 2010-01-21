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
import gov.ca.dsm2.input.model.Reservoirs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
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
import com.google.gwt.user.client.ui.Grid;
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
	private final HeaderPanel headerPanel;

	public MapPanel(HeaderPanel headerPanel) {
		this.headerPanel = headerPanel;
		dsm2InputService = (DSM2InputServiceAsync) GWT
				.create(DSM2InputService.class);
		setMap(new MapWidget(LatLng.newInstance(38.15, -121.70), 10));
		setOptions();
		new ClearBackgroundLayer(getMap(), false);
		new ClearBackgroundLayer(getMap(), true);
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
						headerPanel.showMessage(true, "Drawing...");
						populateGrid();
						headerPanel.showMessage(false, null);
					}

					public void onFailure(Throwable caught) {
						headerPanel.showError(true,
								"Oops, an error occurred. Try again.");
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

	public void setChannelColorScheme(String colorScheme,
			String colorArrayScheme) {
		for (Channel channelData : getChannelManager().getChannels()
				.getChannels()) {
			Polyline line = getChannelManager()
					.getPolyline(channelData.getId());
			String lineColor = getColorForScheme(colorScheme, channelData,
					colorArrayScheme);
			PolyStyleOptions style = PolyStyleOptions.newInstance(lineColor);
			style.setOpacity(0.75);
			style.setWeight(3);
			line.setStrokeStyle(style);
		}
	}

	private String getColorForScheme(String colorScheme, Channel data,
			String colorArrayScheme) {
		if (colorScheme.equals(CHANNEL_COLOR_PLAIN)) {
			return "#110077";
		} else if (colorScheme.equals(CHANNEL_COLOR_MANNINGS)) {
			return getColorForRange(data.getMannings(), 0.01, 0.04,
					colorArrayScheme);
		} else if (colorScheme.equals(CHANNEL_COLOR_DISPERSION)) {
			return getColorForRange(data.getDispersion(), 0.01, 1.5,
					colorArrayScheme);
		} else {
			return "#110077";
		}
	}

	private static String[] divergingColorsArray = new String[] { "#5e3c99",
			"#b2abd2", "#ff99ff", "#fdb863", "#e66101" };
	private static String[] qualitativeColorsArray = new String[] { "#6600cc",
			"#0000ff", "#006633", "#ff6600", "#ff3399" };
	private static String[] sequentialColorsArray = new String[] { "#fee5d9",
			"#fcae91", "#fb6a4a", "#de2d26", "#a50f15" };

	private String getColorForRange(double value, double min, double max,
			String colorArrayScheme) {
		String[] colorsArray = getColorArray(colorArrayScheme);
		int ncolors = colorsArray.length;
		int colorIndex = 0;
		if (value < min) {
			colorIndex = 0;
		} else if (value > max) {
			colorIndex = ncolors - 1;
		} else {
			double colorSlope = (ncolors - 2) / (max - min + 1e-6);
			colorIndex = (int) Math.floor((value - min) * colorSlope) + 1;
		}
		controlPanel.setColorPanel(getColorArraySchemePanel(colorArrayScheme,
				min, max));
		return colorsArray[colorIndex];
	}

	String[] getColorArray(String colorArrayScheme) {
		String[] colorsArray = sequentialColorsArray;
		if (colorArrayScheme == null) {
			colorsArray = sequentialColorsArray;
		} else if (colorArrayScheme.startsWith("dive")) {
			colorsArray = divergingColorsArray;
		} else if (colorArrayScheme.startsWith("qual")) {
			colorsArray = qualitativeColorsArray;
		}
		return colorsArray;
	}

	private static NumberFormat formatter = NumberFormat.getFormat("0.000");

	public Panel getColorArraySchemePanel(String scheme, double min, double max) {
		String[] colorsArray = getColorArray(scheme);
		int ncolors = colorsArray.length;
		Grid panel = new Grid(ncolors, 2);
		double step = (max - min) / (ncolors - 2);
		String html = "<p style=\"background-color: "
				+ colorsArray[0]
				+ ";\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p>";
		panel.setHTML(0, 0, " < " + min);
		panel.setHTML(0, 1, html);
		for (int i = 1; i < ncolors - 1; i++) {
			double value = i * step + min;
			panel.setHTML(i, 0, formatter.format(value - step) + " - "
					+ formatter.format(value));
			html = "<p style=\"background-color: " + colorsArray[i]
					+ ";\">&nbsp;&nbsp;</p>";
			panel.setHTML(i, 1, html);
		}
		html = "<p style=\"background-color: " + colorsArray[ncolors - 1]
				+ ";\">&nbsp;&nbsp;</p>";
		panel.setHTML(ncolors - 1, 0, " > " + max);
		panel.setHTML(ncolors - 1, 1, html);
		return panel;
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
				headerPanel.showMessage(false, null);
				studyNames = result;
				controlPanel.setStudies(studyNames);
				if (studyNames.length > 0) {
					setStudy(studyNames[0]);
				}

			}

			public void onFailure(Throwable caught) {
				headerPanel.showError(true, "Oops an error occurred");
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
