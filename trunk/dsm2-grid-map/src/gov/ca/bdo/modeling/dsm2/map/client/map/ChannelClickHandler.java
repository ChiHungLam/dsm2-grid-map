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
import gov.ca.dsm2.input.model.XSection;
import gov.ca.dsm2.input.model.XSectionProfile;
import gov.ca.modeling.maps.elevation.client.model.GeomUtils;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.maps.client.event.MapClickHandler;
import com.google.gwt.maps.client.event.PolylineClickHandler;
import com.google.gwt.maps.client.event.PolylineLineUpdatedHandler;
import com.google.gwt.maps.client.event.PolylineMouseOverHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.PolyEditingOptions;
import com.google.gwt.maps.client.overlay.PolyStyleOptions;
import com.google.gwt.maps.client.overlay.Polyline;
import com.google.gwt.user.client.Window;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.ScatterChart;

public class ChannelClickHandler implements PolylineClickHandler {
	private static final PolyStyleOptions redLineStyle = PolyStyleOptions
			.newInstance("red");
	private static final PolyStyleOptions blueLineStyle = PolyStyleOptions
			.newInstance("blue");
	private static final PolyStyleOptions greenLineStyle = PolyStyleOptions
			.newInstance("green");

	private final class XSectionLineClickHandler implements
			PolylineClickHandler {
		private final XSection xSection;
		private int xSectionIndex;
		private boolean edit;

		public XSectionLineClickHandler(XSection xSection, int index,
				boolean edit) {
			this.xSection = xSection;
			xSectionIndex = index;
			this.edit = edit;
		}

		public void onClick(PolylineClickEvent event) {
			for (XSection xs : mapPanel.getChannelManager().getXSections()) {
				Polyline line = mapPanel.getChannelManager()
						.getXsectionLineFor(xs);
				if (xs == xSection) {
					line.setStrokeStyle(blueLineStyle);
					infoPanel.drawXSection(channel, xSectionIndex);
				} else {
					line.setStrokeStyle(greenLineStyle);
				}
			}
			final Polyline line = mapPanel.getChannelManager()
					.getXsectionLineFor(xSection);
			line.setStrokeStyle(PolyStyleOptions.newInstance("red"));
			if (!edit) {
				line.setEditingEnabled(false);
				infoPanel.drawXSection(channel, xSectionIndex);
			} else {
				if (xsEditorPanel == null) {
					xsEditorPanel = new CrossSectionEditorPanel();
				}
				line.setEditingEnabled(PolyEditingOptions.newInstance(2));
				line
						.addPolylineLineUpdatedHandler(new PolylineLineUpdatedHandler() {

							public void onUpdate(PolylineLineUpdatedEvent event) {
								XSectionProfile profile = xSection.getProfile();
								if (profile == null) {
									return;
								}
								ArrayList<double[]> endPoints = new ArrayList<double[]>();
								for (int i = 0; i < 2; i++) {
									double[] points = new double[2];
									LatLng vertex = line.getVertex(i);
									points[0] = vertex.getLatitude();
									points[1] = vertex.getLongitude();
									endPoints.add(points);
								}
								profile.setEndPoints(endPoints);
								Node upNode = mapPanel.getNodeManager()
										.getNodes().getNode(
												channel.getUpNodeId());
								Node downNode = mapPanel.getNodeManager()
										.getNodes().getNode(
												channel.getDownNodeId());
								double distance = ModelUtils
										.getIntersectionDistanceFromUpstream(
												profile, channel, upNode,
												downNode);
								if ((distance >= 0)
										&& (distance <= channel.getLength())) {
									double dratio = distance
											/ channel.getLength();
									dratio = Math.round(dratio * 1000) / 1000.0;
									profile.setDistance(dratio);
									xSection.setDistance(dratio);
								}
							}
						});
				mapPanel.getInfoPanel().clear();
				mapPanel.getInfoPanel().add(xsEditorPanel);
				mapPanel.getInfoPanel().add(xsEditorPanel);
				xsEditorPanel.draw(channel, xSectionIndex, mapPanel);
			}
		}

	}

	private final Channel channel;
	private final MapPanel mapPanel;
	private final String color = "#FF0000";
	private final int weight = 5;
	private final double opacity = 0.75;
	private Polyline line;
	private ChannelInfoPanel infoPanel;
	private CrossSectionEditorPanel xsEditorPanel;

	public ChannelClickHandler(Channel lineData, MapPanel mapPanel) {
		channel = lineData;
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
		infoPanel = new ChannelInfoPanel(channel, mapPanel);
		mapPanel.getInfoPanel().clear();
		mapPanel.getInfoPanel().add(infoPanel);
		NodeMarkerDataManager nodeManager = mapPanel.getNodeManager();
		Node upNode = nodeManager.getNodeData(channel.getUpNodeId());
		Node downNode = nodeManager.getNodeData(channel.getDownNodeId());
		if (line != null) {
			if (line.isVisible()) {
				line.setVisible(false);
				clearOverlays();
				return;
			} else {
				line.setVisible(true);
			}
			mapPanel.getMap().addOverlay(line);
			if (mapPanel.getChannelManager().getXSectionLines() != null) {
				for (Polyline xline : mapPanel.getChannelManager()
						.getXSectionLines()) {
					mapPanel.getMap().addOverlay(xline);
				}
			}
			// indicate up and down node by letters U and D at the nodes
			//
			if (mapPanel.isInEditMode()) {
				mapPanel.getMap().addMapClickHandler(new MapClickHandler() {

					public void onClick(MapClickEvent event) {
						Window.setStatus(event.getLatLng() + "");
					}
				});
				line.setEditingEnabled(true);
			}
			return;
		}

		PolyStyleOptions style = PolyStyleOptions.newInstance(color, weight,
				opacity);
		LatLng[] points = ModelUtils.getPointsForChannel(channel, upNode,
				downNode);
		line = new Polyline(points);
		mapPanel.getMap().addOverlay(line);
		line.setStrokeStyle(style);
		if (mapPanel.isInEditMode()) {

			// allow up to 25 vertices to exist in the line.
			line.setEditingEnabled(PolyEditingOptions.newInstance(25));
			line.addPolylineClickHandler(new PolylineClickHandler() {
				public void onClick(PolylineClickEvent event) {
					updateChannelLengthLatLng();
					line.setEditingEnabled(false);
					clearOverlays();
					updateDisplay();
				}

			});
			line
					.addPolylineLineUpdatedHandler(new PolylineLineUpdatedHandler() {

						public void onUpdate(PolylineLineUpdatedEvent event) {
							updateChannelLengthLatLng();
							updateDisplay();
						}
					});
			drawXSectionLines();
		} else {
			line.addPolylineClickHandler(new PolylineClickHandler() {

				public void onClick(PolylineClickEvent event) {
					clearOverlays();
					updateDisplay();
				}
			});
			line.addPolylineMouseOverHandler(new PolylineMouseOverHandler() {

				public void onMouseOver(PolylineMouseOverEvent event) {
					WindowUtils.changeCursor("pointer");
				}

			});
			drawXSectionLines();
		}
	}

	private void drawXSectionLines() {
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
				int segmentIndex = GeomUtils.findSegmentAtDistance(
						channelOutlinePoints, distance);
				LatLng point1 = channelOutlinePoints[segmentIndex];
				LatLng point2 = channelOutlinePoints[segmentIndex + 1];
				double segmentDistance = GeomUtils.findDistanceUptoSegment(
						segmentIndex, channelOutlinePoints);

				LatLng point0 = GeomUtils.findPointAtDistance(point1, point2,
						distance - segmentDistance);
				double slope = GeomUtils.getSlopeBetweenPoints(point1, point2);
				double width = ModelUtils.getMaxTopWidth(xSection);
				latLngs = GeomUtils.getLineWithSlopeOfLengthAndCenteredOnPoint(
						-1 / slope, width, point0);
			} else {
				List<double[]> endPoints = xSection.getProfile().getEndPoints();
				latLngs = new LatLng[] {
						LatLng.newInstance(endPoints.get(0)[0], endPoints
								.get(0)[1]),
						LatLng.newInstance(endPoints.get(1)[0], endPoints
								.get(1)[1]) };
			}
			final Polyline line = new Polyline(latLngs, "green", 4);

			line.addPolylineClickHandler(new XSectionLineClickHandler(xSection,
					xSectionIndex, mapPanel.isInEditMode()));
			line.addPolylineMouseOverHandler(new PolylineMouseOverHandler() {

				public void onMouseOver(PolylineMouseOverEvent event) {
					WindowUtils.changeCursor("pointer");
				}

			});
			mapPanel.getChannelManager().addXSectionLine(xSection, line);
			mapPanel.getMap().addOverlay(line);
			if (mapPanel.isInEditMode()) {
				// TODO: with some other trigger
				// line.setEditingEnabled(true);
				// line.setEditingEnabled(PolyEditingOptions.newInstance(2));
			}

			xSectionIndex++;
		}
	}

	public void updateChannelLengthLatLng() {
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
		Node upNode = mapPanel.getNodeManager().getNodes().getNode(
				channel.getUpNodeId());
		Node downNode = mapPanel.getNodeManager().getNodes().getNode(
				channel.getDownNodeId());
		LatLng[] pointsForChannel = ModelUtils.getPointsForChannel(channel,
				upNode, downNode);
		double findDistanceUptoSegment = GeomUtils.findDistanceUptoSegment(
				pointsForChannel.length - 1, pointsForChannel);
	}

	public void updateDisplay() {
		ChannelInfoPanel panel = new ChannelInfoPanel(channel, mapPanel);
		mapPanel.getInfoPanel().clear();
		mapPanel.getInfoPanel().add(panel);
	}

	public void clearOverlays() {
		mapPanel.getMap().removeOverlay(line);
		if (mapPanel.getChannelManager().getXSectionLines() == null) {
			return;
		}
		for (Polyline xline : mapPanel.getChannelManager().getXSectionLines()) {
			mapPanel.getMap().removeOverlay(xline);
		}
	}

}
