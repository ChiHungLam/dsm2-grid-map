/*******************************************************************************
 *     Copyright (C) 2009, 2010 Nicky Sandhu, State of California, Department of Water Resources.
 *
 *     DSM2 Grid Map : An online map centric tool to visualize, create and modify 
 *                               DSM2 input and output 
 *     Version 1.0
 *     by Nicky Sandhu
 *     California Dept. of Water Resources
 *     Modeling Support Branch
 *     1416 Ninth Street
 *     Sacramento, CA 95814
 *     psandhu@water.ca.gov
 *
 *     Send bug reports to psandhu@water.ca.gov
 *
 *     This file is part of DSM2 Grid Map
 *     The DSM2 Grid Map is free software and is licensed to you under the terms of the GNU 
 *     General Public License, version 3, as published by the Free Software Foundation.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program; if not, contact the 
 *     Free Software Foundation, 675 Mass Ave, Cambridge, MA
 *     02139, USA.
 *
 *     THIS SOFTWARE AND DOCUMENTATION ARE PROVIDED BY THE CALIFORNIA
 *     DEPARTMENT OF WATER RESOURCES AND CONTRIBUTORS "AS IS" AND ANY
 *     EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *     IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *     PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE CALIFORNIA
 *     DEPARTMENT OF WATER RESOURCES OR ITS CONTRIBUTORS BE LIABLE FOR
 *     ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *     CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 *     OR SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA OR PROFITS; OR
 *     BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *     LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *     (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 *     USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 *     DAMAGE.
 *******************************************************************************/
package gov.ca.bdo.modeling.dsm2.map.client.map;

import gov.ca.dsm2.input.model.Channel;
import gov.ca.dsm2.input.model.Channels;
import gov.ca.dsm2.input.model.DSM2Model;
import gov.ca.dsm2.input.model.Gates;
import gov.ca.dsm2.input.model.Node;
import gov.ca.dsm2.input.model.Reservoirs;
import gov.ca.dsm2.input.model.XSection;
import gov.ca.modeling.maps.widgets.client.ExpandContractMapControl;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.maps.client.Copyright;
import com.google.gwt.maps.client.CopyrightCollection;
import com.google.gwt.maps.client.MapType;
import com.google.gwt.maps.client.MapUIOptions;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.TileLayer;
import com.google.gwt.maps.client.event.MapZoomEndHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.LatLngBounds;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.geom.Size;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.Overlay;
import com.google.gwt.maps.client.overlay.PolyStyleOptions;
import com.google.gwt.maps.client.overlay.Polyline;
import com.google.gwt.maps.client.overlay.TileLayerOverlay;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ResizeComposite;

public class MapPanel extends ResizeComposite {

	static final int ZOOM_LEVEL = 10;
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
	private BoundaryMarkerDataManager boundaryOverlayManager;
	private GridVisibilityControl visibilityControl;
	private Panel infoPanel;
	private TransfersManager transfersManager;
	private ArrayList<Polyline> flowLines;
	private Timer timer;

	public MapPanel() {
		// FIXME: the center of the map should be configurable.
		setMap(new MapWidget(LatLng.newInstance(38.15, -121.70), 10));
		setOptions();
		visibilityControl = new GridVisibilityControl(this);
		getMap().addControl(visibilityControl);
		ExpandContractMapControl fullScreenControl = new ExpandContractMapControl();
		map.addControl(fullScreenControl);
		// add them all here
		initWidget(getMap());
		// add zoom handler to hide channels at a certain zoom level
		map.addMapZoomEndHandler(new MapZoomEndHandler() {

			public void onZoomEnd(MapZoomEndEvent event) {
				if ((event.getNewZoomLevel() <= ZOOM_LEVEL)
						&& (event.getOldZoomLevel() > ZOOM_LEVEL)) {
					hideNodeMarkers(true);
				}
				if ((event.getOldZoomLevel() <= ZOOM_LEVEL)
						&& (event.getNewZoomLevel() > ZOOM_LEVEL)) {
					hideNodeMarkers(visibilityControl.getHideNodes().getValue());
				}
			}
		});
		setStyleName("map-panel");

	}

	private void setOptions() {
		MapUIOptions options = MapUIOptions.newInstance(Size.newInstance(1000,
				800));
		options.setKeyboard(true);
		options.setMenuMapTypeControl(true);
		options.setScaleControl(true);
		options.setScrollwheel(true);
		getMap().setUI(options);
		map.addMapType(getTopoMapType());
	}

	private final native MapType getTopoMapType()/*-{
		var layer = new $wnd.USGSTopoTileLayer("http://orthoimage.er.usgs.gov/ogcmap.ashx?", "USGS Topo Maps", "Topo","DRG","EPSG:4326","1.1.1","","image/png",null,"0xFFFFFF");
		var o = new $wnd.GMapType([layer], $wnd.G_NORMAL_MAP.getProjection(), "Topo");
		return @com.google.gwt.maps.client.MapType::createPeer(Lcom/google/gwt/core/client/JavaScriptObject;)(o);
	}-*/;

	public void populateGrid() {
		clearAllMarkers();
		if (model == null) {
			return;
		}
		setNodeManager(new NodeMarkerDataManager(this, model.getNodes()));
		setChannelManager(new ChannelLineDataManager(this, model.getChannels()));
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
		populateBoundaryMarkers();
		populateTransfers();
	}

	private void populateTextAnnotationMarkers() {
		textAnnotationHandler = new TextAnnotationsManager(this);
	}

	private void populateOutputMarkers() {
		if (!visibilityControl.getHideOutputs().getValue()) {
			outputMarkerDataManager = new OutputMarkerDataManager();
			outputMarkerDataManager.setModel(model, this);
			outputMarkerDataManager.addMarkers(map);
		}
	}

	private void populateBoundaryMarkers() {
		if (!visibilityControl.getHideBoundaries().getValue()) {
			if (boundaryOverlayManager != null) {
				boundaryOverlayManager = new BoundaryMarkerDataManager();
				boundaryOverlayManager.setModel(model, this);
				boundaryOverlayManager.addMarkers(map);
			}
		}
	}

	private void populateTransfers() {
		transfersManager = new TransfersManager(map, model);
		transfersManager.addLines();
	}

	protected void clearAllMarkers() {
		if (getNodeManager() != null) {
			getNodeManager().clearNodeMarkers();
		}
		if (getMap() != null) {
			getMap().clearOverlays();
		}
	}

	protected void populateNodeMarkers() {
		if (getNodeManager() != null) {
			if (map.getZoomLevel() > ZOOM_LEVEL) {
				if (!visibilityControl.getHideNodes().getValue()) {
					getNodeManager().displayNodeMarkers();
				}
			}
		}
	}

	protected void populateChannelLines() {
		flowLines = null;
		if (!visibilityControl.getHideChannels().getValue()) {
			getChannelManager().addLines();
		}
	}

	protected void populateGateImages() {
		if (!visibilityControl.getHideGates().getValue()) {
			Gates gates = model.getGates();
			gateOverlayManager = new GateOverlayManager(this, gates);
			gateOverlayManager.addGates();
		}
	}

	protected void populateReservoirMarkers() {
		if (!visibilityControl.getHideReservoirs().getValue()) {
			Reservoirs reservoirs = model.getReservoirs();
			reservoirOverlayManager = new ReservoirOverlayManager(this);
			reservoirOverlayManager.setReservoirs(reservoirs);
			reservoirOverlayManager.displayReservoirMarkers();
		}
	}

	public void hideNodeMarkers(boolean hide) {
		if (getNodeManager() == null) {
			return;
		}
		if (hide) {
			getNodeManager().clearNodeMarkers();
		} else {
			getNodeManager().displayNodeMarkers();
		}
	}

	public void hideChannelLines(boolean hide) {
		if (getChannelManager() != null) {
			for (Polyline line : getChannelManager().getPolylines()) {
				line.setVisible(!hide);
			}
		}
	}

	public void hideGateMarkers(boolean hide) {
		if (!hide) {
			populateGateImages();
		}
		gateOverlayManager.hideMarkers(hide);
	}

	public void hideReservoirMarkers(boolean hide) {
		reservoirOverlayManager.hideMarkers(hide);
	}

	public void hideOutputMarkers(boolean hide) {
		outputMarkerDataManager.hideMarkers(hide);
	}

	public void hideTransfers(boolean hide) {
		transfersManager.hideTransfers(hide);
	}

	public void hideBoundaryMarkers(boolean hide) {
		if ((hide == false) && (boundaryOverlayManager == null)) {
			boundaryOverlayManager = new BoundaryMarkerDataManager();
			boundaryOverlayManager.setModel(model, this);
			boundaryOverlayManager.addMarkers(map);
		}
		boundaryOverlayManager.hideMarkers(hide);
		boundaryOverlayManager = null;
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
						+ hashCode + "_itile" + tile.getX() + "_" + tile.getY()
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

	public DSM2Model getModel() {
		return model;
	}

	public void setModel(DSM2Model model) {
		this.model = model;
	}

	public void setInfoPanel(Panel panel) {
		infoPanel = panel;
	}

	public Panel getInfoPanel() {
		return infoPanel;
	}

	public void showFlowLines() {
		if (flowLines == null) {
			NodeMarkerDataManager nm = getNodeManager();
			DSM2Model model = getModel();
			Channels channels = model.getChannels();
			flowLines = new ArrayList<Polyline>();
			final PolyStyleOptions style = PolyStyleOptions.newInstance(
					"#FF0000", 4, 1.0);
			for (Channel channel : channels.getChannels()) {
				Node upNode = nm.getNodeData(channel.getUpNodeId());
				Node downNode = nm.getNodeData(channel.getDownNodeId());
				LatLng[] points = ModelUtils.getPointsForChannel(channel,
						upNode, downNode);
				Polyline line = new Polyline(points);
				line.setStrokeStyle(style);
				flowLines.add(line);
			}
			timer = new Timer() {

				@Override
				public void run() {
					int r = Random.nextInt(255);
					int g = Random.nextInt(255);
					int b = Random.nextInt(255);
					String colorSpec = "#" + toHex(r) + toHex(g) + toHex(b);
					style.setColor(colorSpec);
					GWT.log("Setting color to " + colorSpec);
					for (Polyline line : flowLines) {
						line.setVisible(false);
					}
					for (Polyline line : flowLines) {
						line.setStrokeStyle(style);
					}
					for (Polyline line : flowLines) {
						line.setVisible(true);
					}

				}

				public String toHex(int val) {
					String hex = Integer.toHexString(val).toUpperCase();
					return hex.length() == 1 ? "0" + hex : hex;
				}
			};
		}
		for (Polyline line : flowLines) {
			map.addOverlay(line);
		}
		timer.scheduleRepeating(1000);
	}

	public void hideFlowLines() {
		if (flowLines != null) {
			timer.cancel();
			for (Polyline line : flowLines) {
				map.removeOverlay(line);
			}
		}
	}

	public GateOverlayManager getGateManager() {
		return gateOverlayManager;
	}

	public ReservoirOverlayManager getReservoirManager() {
		return reservoirOverlayManager;
	}

	public void deleteElementForOverlay(Overlay overlay) {
		if (overlay instanceof Marker) {
			if (nodeManager != null) {
				Node nodeForMarker = nodeManager.getNodeForMarker(overlay);
				String channelsConnectedTo = ModelUtils.getChannelsConnectedTo(
						getModel().getChannels(), nodeForMarker);
				// check for channels connected or reservoir connections and
				// delete only if not connected
				if (channelsConnectedTo != null) {
					Window.alert("Cannot delete node connected to channels: "
							+ channelsConnectedTo);
					return;
				}
				nodeManager.removeNode(nodeForMarker);
			}
		} else if (overlay instanceof Polyline) {
			if (channelManager != null) {
				String channelId = channelManager.getChannelId(overlay);
				if (channelId == null) { // its a cross section, maybe?
					XSection xSection = channelManager.getXSectionFor(overlay);
					channelManager.removeXSection(xSection);
					return;
				}
				Channel channel = getModel().getChannels()
						.getChannel(channelId);
				getModel().getChannels().removeChannel(channel);
				channelManager.removePolyline(channelId);
			}
		}
	}

}
