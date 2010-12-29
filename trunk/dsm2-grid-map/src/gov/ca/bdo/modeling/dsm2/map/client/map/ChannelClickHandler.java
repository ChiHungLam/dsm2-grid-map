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

import gov.ca.bdo.modeling.dsm2.map.client.WindowUtils;
import gov.ca.dsm2.input.model.Channel;
import gov.ca.dsm2.input.model.Node;
import gov.ca.dsm2.input.model.Nodes;
import gov.ca.dsm2.input.model.XSection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.maps.client.event.PolylineClickHandler;
import com.google.gwt.maps.client.event.PolylineLineUpdatedHandler;
import com.google.gwt.maps.client.event.PolylineMouseOverHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.PolyEditingOptions;
import com.google.gwt.maps.client.overlay.PolyStyleOptions;
import com.google.gwt.maps.client.overlay.Polyline;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.ScatterChart;

public class ChannelClickHandler implements PolylineClickHandler {
	private final MapPanel mapPanel;
	private final String color = "#FF0000";
	private final int weight = 5;
	private final double opacity = 0.75;
	private Polyline line;
	private ChannelInfoPanel infoPanel;
	public static final int MAX_VERTEX_FLOWLINE = 50;

	public ChannelClickHandler(MapPanel mapPanel) {
		this.mapPanel = mapPanel;
	}

	public void onClick(final PolylineClickEvent event) {
		Runnable visualizationLoadCallback = new Runnable() {
			public void run() {
				doOnClick(event);
			}
		};
		// Load the visualization api, passing the onLoadCallback to be called
		// when loading is done.
		VisualizationUtils.loadVisualizationApi(visualizationLoadCallback,
				ScatterChart.PACKAGE);
	}

	public void doOnClick(PolylineClickEvent event) {
		Polyline channelLine = event.getSender();
		String channelId = mapPanel.getChannelManager().getChannelId(
				channelLine);
		if (channelId == null) {
			return;
		}
		final Channel channel = mapPanel.getChannelManager().getChannels()
				.getChannel(channelId);
		if (channel == null) {
			mapPanel.showMessage("No channel found for " + channelId);
			return;
		}
		infoPanel = new ChannelInfoPanel(channel, mapPanel);
		mapPanel.getInfoPanel().clear();
		if (mapPanel.isInEditMode() && mapPanel.isInDeletingMode()) {
			mapPanel.getChannelManager().removeChannel(channel);
			if (line != null) {
				mapPanel.getMap().removeOverlay(line);
				line = null;
			}
			return;
		}
		mapPanel.getInfoPanel().add(infoPanel);
		NodeMarkerDataManager nodeManager = mapPanel.getNodeManager();
		Node upNode = nodeManager.getNodeData(channel.getUpNodeId());
		Node downNode = nodeManager.getNodeData(channel.getDownNodeId());
		// clear previous line
		if (line != null) {
			clearOverlays();
		}
		// add new line
		PolyStyleOptions style = PolyStyleOptions.newInstance(color, weight,
				opacity);
		LatLng[] points = ModelUtils.getPointsForChannel(channel, upNode,
				downNode);
		line = new Polyline(points);
		mapPanel.getMap().addOverlay(line);
		line.setStrokeStyle(style);
		if (mapPanel.isInEditMode()) {
			// allow up to MAX_VERTEX_FLOWLINE vertices to exist in the line.
			line.setEditingEnabled(PolyEditingOptions
					.newInstance(MAX_VERTEX_FLOWLINE));
			line.addPolylineClickHandler(new PolylineClickHandler() {
				public void onClick(PolylineClickEvent event) {
					updateChannelLengthLatLng(channel);
					line.setEditingEnabled(false);
					clearOverlays();
					updateDisplay(channel);
				}

			});
			line
					.addPolylineLineUpdatedHandler(new PolylineLineUpdatedHandler() {

						public void onUpdate(PolylineLineUpdatedEvent event) {
							updateChannelLengthLatLng(channel);
							updateDisplay(channel);
						}
					});
			drawXSectionLines(channel);
		} else {
			line.addPolylineClickHandler(new PolylineClickHandler() {

				public void onClick(PolylineClickEvent event) {
					clearOverlays();
					updateDisplay(channel);
				}
			});
			line.addPolylineMouseOverHandler(new PolylineMouseOverHandler() {

				public void onMouseOver(PolylineMouseOverEvent event) {
					WindowUtils.changeCursor("pointer");
				}

			});
			drawXSectionLines(channel);
		}
	}

	public void drawXSectionLines(Channel channel) {
		mapPanel.getChannelManager().clearXSectionLines();
		Node upNode = mapPanel.getNodeManager().getNodes().getNode(
				channel.getUpNodeId());
		Node downNode = mapPanel.getNodeManager().getNodes().getNode(
				channel.getDownNodeId());
		LatLng[] channelOutlinePoints = ModelUtils.getPointsForChannel(channel,
				upNode, downNode);
		ArrayList<XSection> xsections = channel.getXsections();
		int xSectionIndex = 0;
		for (final XSection xSection : xsections) {
			double distance = xSection.getDistance();
			distance = channel.getLength() * distance;
			LatLng[] latLngs = null;
			if (xSection.getProfile() == null) {
				latLngs = ModelUtils.calculateEndPoints(xSection, channel,
						upNode, downNode);
			} else {
				List<double[]> endPoints = xSection.getProfile().getEndPoints();
				latLngs = new LatLng[] {
						LatLng.newInstance(endPoints.get(0)[0], endPoints
								.get(0)[1]),
						LatLng.newInstance(endPoints.get(1)[0], endPoints
								.get(1)[1]) };
			}
			final Polyline line = new Polyline(latLngs, "green", 4);

			line.addPolylineClickHandler(new XSectionLineClickHandler(mapPanel,
					infoPanel, channel, xSection, xSectionIndex, mapPanel
							.isInEditMode()));
			line.addPolylineMouseOverHandler(new PolylineMouseOverHandler() {

				public void onMouseOver(PolylineMouseOverEvent event) {
					WindowUtils.changeCursor("pointer");
				}

			});
			mapPanel.getChannelManager().addXSectionLine(xSection, line);
			mapPanel.getMap().addOverlay(line);

			xSectionIndex++;
		}
	}

	public void updateChannelLengthLatLng(Channel channel) {
		double oldLength = channel.getLength();
		channel.setLength((int) ModelUtils.getLengthInFeet(line.getLength()));
		int vcount = line.getVertexCount();
		ArrayList<double[]> points = new ArrayList<double[]>();
		for (int i = 1; i < vcount - 1; i++) {
			LatLng vertex = line.getVertex(i);
			double[] point = new double[] { vertex.getLatitude(),
					vertex.getLongitude() };
			points.add(point);
		}
		channel.setLatLngPoints(points);
		for (XSection xSection : channel.getXsections()) {
			updateXSectionPosition(channel, xSection, oldLength);
		}
	}

	public void updateXSectionPosition(Channel channel, XSection xSection,
			double oldLength) {
		Nodes nodes = mapPanel.getNodeManager().getNodes();
		ModelUtils.updateXSectionPosition(channel, nodes, xSection, oldLength);
	}

	public void updateDisplay(Channel channel) {
		ChannelInfoPanel panel = new ChannelInfoPanel(channel, mapPanel);
		mapPanel.getInfoPanel().clear();
		mapPanel.getInfoPanel().add(panel);
	}

	public void clearOverlays() {
		if (line != null) {
			line.setVisible(false);
			mapPanel.getMap().removeOverlay(line);
			line = null;
		}
		Collection<Polyline> xSectionLines = mapPanel.getChannelManager()
				.getXSectionLines();
		if (xSectionLines != null) {
			for (Polyline line : xSectionLines) {
				mapPanel.getMap().removeOverlay(line);
			}
		}
	}

}
