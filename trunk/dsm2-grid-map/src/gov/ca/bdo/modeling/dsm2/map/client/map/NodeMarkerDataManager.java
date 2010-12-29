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

import gov.ca.dsm2.input.model.Channels;
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
import com.google.gwt.maps.client.overlay.Overlay;
import com.google.gwt.maps.utility.client.labeledmarker.LabeledMarker;
import com.google.gwt.maps.utility.client.labeledmarker.LabeledMarkerOptions;
import com.google.gwt.maps.utility.client.markerclusterer.MarkerClusterer;
import com.google.gwt.maps.utility.client.markerclusterer.MarkerClustererOptions;

public class NodeMarkerDataManager {
	private Nodes nodes;
	private final HashMap<String, Marker> markerMap;
	private MarkerClusterer markerClusterer;
	private MapPanel mapPanel;
	private MarkNewNodePosition dragEndHandler;
	private Icon labelIcon;
	private Icon editModeIcon;
	private NodeClickHandler nodeClickHandler;

	public NodeMarkerDataManager(MapPanel mapPanel, Nodes nodes) {
		this.nodes = nodes;
		this.mapPanel = mapPanel;
		dragEndHandler = new MarkNewNodePosition(this.mapPanel);
		nodeClickHandler = new NodeClickHandler(this.mapPanel);
		markerMap = new HashMap<String, Marker>();
		labelIcon = Icon.newInstance("images/greencirclemarker.png");
		labelIcon.setIconSize(Size.newInstance(22, 22));
		labelIcon.setIconAnchor(Point.newInstance(11, 11));
		labelIcon.setInfoWindowAnchor(Point.newInstance(11, 7));
		editModeIcon = Icon.newInstance("images/blue_MarkerN.png");
		editModeIcon.setIconSize(Size.newInstance(12, 20));
		editModeIcon.setShadowSize(Size.newInstance(22, 20));
		editModeIcon.setIconAnchor(Point.newInstance(6, 20));
		editModeIcon.setInfoWindowAnchor(Point.newInstance(5, 1));
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
		addMarkerForNode(node);
	}

	public void deleteNode(Node node) {
		nodes.removeNode(node);
		removeMarkerForNode(node);
	}

	public Node getNodeData(String id) {
		return nodes.getNode(id);
	}

	private void addMarker(Node mapMarkerData, Marker marker) {
		markerMap.put(mapMarkerData.getId(), marker);
	}

	public Marker getMarkerFor(String nodeId) {
		return markerMap.get(nodeId);
	}

	public Nodes getNodes() {
		return nodes;
	}

	private Icon getNodeIcon() {
		if (mapPanel.isInEditMode()) {
			return editModeIcon;
		} else {
			return labelIcon;
		}
	}

	/**
	 * adds node markers to map. Note: does not remove old markers @see
	 * clearNodeMarkers
	 * 
	 * @param mapPanel
	 */
	public void displayNodeMarkers() {
		for (Node node : getAllNodes()) {
			addMarkerForNode(node);
		}
		if (!mapPanel.isInEditMode()) {
			MarkerClustererOptions clusterOptions = MarkerClustererOptions
					.newInstance();
			clusterOptions.setGridSize(100);
			clusterOptions.setMaxZoom(10);
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
	 * Works when adding one node at a time in edit mode. for labeled mode,
	 * you'll have to call refresh
	 * 
	 * @param mapMarkerData
	 */
	private void addMarkerForNode(Node mapMarkerData) {
		MarkerOptions options = null;
		if (!mapPanel.isInEditMode()) {
			LabeledMarkerOptions opts = LabeledMarkerOptions.newInstance();
			opts.setLabelOffset(Size.newInstance(-5, -5));
			opts.setLabelText(mapMarkerData.getId());
			opts.setLabelClass("hm-marker-label");
			options = opts;
		} else {
			options = MarkerOptions.newInstance();
		}
		options.setTitle(mapMarkerData.getId());
		options.setIcon(getNodeIcon());
		// -- edit mode options and only for the marker being
		// manipulated --
		options.setDragCrossMove(mapPanel.isInEditMode());
		options.setDraggable(mapPanel.isInEditMode());
		options.setClickable(mapPanel.isInEditMode());
		options.setAutoPan(mapPanel.isInEditMode());
		// -- edit mode options
		Marker marker = null;
		LatLng point = LatLng.newInstance(mapMarkerData.getLatitude(),
				mapMarkerData.getLongitude());
		if (!mapPanel.isInEditMode()) {
			marker = new LabeledMarker(point, (LabeledMarkerOptions) options);
		} else {
			marker = new Marker(point, options);
		}
		if (mapPanel.isInEditMode()) {
			marker.addMarkerDragEndHandler(dragEndHandler);
			marker.addMarkerClickHandler(nodeClickHandler);
		}
		addMarker(mapMarkerData, marker);
		if (mapPanel.isInEditMode()) {
			mapPanel.getMap().addOverlay(marker);
		}
	}

	private void removeMarkerForNode(Node node) {
		if (mapPanel.isInEditMode()) {
			mapPanel.getMap().removeOverlay(getMarkerFor(node.getId()));
		} else {
			markerClusterer.removeMarker(getMarkerFor(node.getId()));
		}
	}

	/**
	 * clears node markers
	 * 
	 * @param mapPanel
	 */
	public void clearNodeMarkers() {
		if (markerClusterer != null) {
			markerClusterer.clearMarkers();
			markerClusterer = null;
		}
		for (Marker marker : markerMap.values()) {
			mapPanel.getMap().removeOverlay(marker);
		}
		markerMap.clear();
	}

	public String getNewNodeId() {
		return (nodes.calculateMaxNodeId() + 1) + "";
	}

	public Node getNodeForMarker(Overlay overlay) {
		if (overlay instanceof Marker) {
			Marker m = (Marker) overlay;
			String id = m.getTitle();
			Marker markerFor = getMarkerFor(id);
			if (markerFor == m) {
				return nodes.getNode(id);
			}
		}
		return null;
	}

	public void removeNode(String nodeId, Channels channels) {
		Node node = mapPanel.getNodeManager().getNodes().getNode(nodeId);
		if (node == null) {
			return;
		}
		String channelsConnectedTo = ModelUtils.getChannelsConnectedTo(
				channels, node);
		// check for channels connected or reservoir connections and
		// delete only if not connected
		if (channelsConnectedTo != null) {
			throw new RuntimeException(
					"Cannot delete node connected to channels: "
							+ channelsConnectedTo);
		}
		removeMarkerForNode(node);
		nodes.removeNode(node);
	}

	public void renameNodeId(String newValue, String previousValue) {
		nodes.renameNodeId(newValue, previousValue);
		mapPanel.getMap().removeOverlay(getMarkerFor(previousValue));
		addMarkerForNode(nodes.getNode(newValue));
		mapPanel.getChannelManager().getChannels().updateNodeId(newValue,
				previousValue);
		mapPanel.getReservoirManager().getReservoirs().updateNodeId(newValue,
				previousValue);
		mapPanel.getGateManager().getGates().updateNodeId(newValue,
				previousValue);
		mapPanel.getBoundaryManager().getBoundaryInputs().updateNodeId(
				newValue, previousValue);
		// FIXME: transfers also might need this
		// FIXME: this really should be fixed by having a proper network model
	}

}
