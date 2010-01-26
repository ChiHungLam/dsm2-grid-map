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
package gov.ca.bdo.modeling.dsm2.map.client;

import gov.ca.bdo.modeling.dsm2.map.client.model.GeomUtils;
import gov.ca.bdo.modeling.dsm2.map.client.model.NodeMarkerDataManager;
import gov.ca.dsm2.input.model.Channel;
import gov.ca.dsm2.input.model.Node;
import gov.ca.dsm2.input.model.XSection;
import gov.ca.dsm2.input.model.XSectionLayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.maps.client.event.PolylineClickHandler;
import com.google.gwt.maps.client.event.PolylineLineUpdatedHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.PolyEditingOptions;
import com.google.gwt.maps.client.overlay.PolyStyleOptions;
import com.google.gwt.maps.client.overlay.Polyline;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.ScatterChart;

public class ChannelClickHandler implements PolylineClickHandler {
	private final Channel channel;
	private final MapPanel mapPanel;
	private final String color = "#FF0000";
	private final int weight = 5;
	private final double opacity = 0.75;
	private Polyline line;
	private final HashMap<XSection, Polyline> xsectionLineMap;

	public ChannelClickHandler(Channel lineData, MapPanel mapPanel) {
		channel = lineData;
		this.mapPanel = mapPanel;
		xsectionLineMap = new HashMap<XSection, Polyline>();
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
		ChannelInfoPanel panel = new ChannelInfoPanel(channel);
		mapPanel.getInfoPanel().clear();
		mapPanel.getInfoPanel().add(panel);
		if (line != null) {
			if (line.isVisible()) {
				line.setVisible(false);
				clearOverlays();
				return;
			} else {
				line.setVisible(true);
			}
			mapPanel.getMap().addOverlay(line);
			for (Polyline xline : xsectionLineMap.values()) {
				mapPanel.getMap().addOverlay(xline);
			}
			if (mapPanel.isInEditMode()) {
				line.setEditingEnabled(true);
			}
			return;
		}
		PolyStyleOptions style = PolyStyleOptions.newInstance(color, weight,
				opacity);
		NodeMarkerDataManager nodeManager = mapPanel.getNodeManager();
		Node upNode = nodeManager.getNodeData(channel.getUpNodeId());
		Node downNode = nodeManager.getNodeData(channel.getDownNodeId());
		LatLng[] points = null;
		LatLng upPoint = LatLng.newInstance(upNode.getLatitude(), upNode
				.getLongitude());
		LatLng downPoint = LatLng.newInstance(downNode.getLatitude(), downNode
				.getLongitude());
		List<double[]> latLngPoints = channel.getLatLngPoints();
		if ((latLngPoints != null) && (latLngPoints.size() > 0)) {
			int size = latLngPoints.size();
			points = new LatLng[size + 2];
			for (int i = 1; i < points.length - 1; i++) {
				double[] ds = latLngPoints.get(i - 1);
				points[i] = LatLng.newInstance(ds[0], ds[1]);
			}
		} else {
			points = new LatLng[2];
		}
		points[0] = upPoint;
		points[points.length - 1] = downPoint;
		line = new Polyline(points);
		mapPanel.getMap().addOverlay(line);
		line.setStrokeStyle(style);
		if (mapPanel.isInEditMode()) {

			// allow up to 10 vertices to exist in the line.
			line.setEditingEnabled(PolyEditingOptions.newInstance(15));
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
		} else {
			line.addPolylineClickHandler(new PolylineClickHandler() {

				public void onClick(PolylineClickEvent event) {
					mapPanel.getMap().removeOverlay(line);
					updateDisplay();
				}
			});
			// drawXSectionLines();
		}
	}

	private void drawXSectionLines() {
		LatLng[] channelOutlinePoints = getChannelOutlinePoints();
		ArrayList<XSection> xsections = channel.getXsections();
		for (XSection xSection : xsections) {
			double distance = xSection.getDistance();
			distance = channel.getLength() * distance;
			int segmentIndex = GeomUtils.findSegmentAtDistance(
					channelOutlinePoints, distance);
			LatLng point1 = channelOutlinePoints[segmentIndex];
			LatLng point2 = channelOutlinePoints[segmentIndex + 1];
			double segmentDistance = GeomUtils.findDistanceUptoSegment(
					segmentIndex, channelOutlinePoints);
			LatLng point0 = GeomUtils.findPointAtDistance(point1, point2,
					distance - segmentDistance);
			double slope = GeomUtils.getSlopeBetweenPoints(point1, point2);
			double width = getTopWidthAtElevation(xSection, 0);
			LatLng[] latLngs = GeomUtils
					.getLineWithSlopeOfLengthAndCenteredOnPoint(-1 / slope,
							width, point0);
			Polyline line = new Polyline(latLngs, "green", 4);
			xsectionLineMap.put(xSection, line);
			mapPanel.getMap().addOverlay(line);
		}
	}

	private double getTopWidthAtElevation(XSection xsection, double elevation) {
		ArrayList<XSectionLayer> layers = xsection.getLayers();
		double previousElevation = 0;
		double previousTopWidth = 0;
		for (XSectionLayer xSectionLayer : layers) {
			if (elevation < xSectionLayer.getElevation()) {
				return interpolateLinearly(elevation, xSectionLayer
						.getTopWidth(), xSectionLayer.getElevation(),
						previousElevation, previousTopWidth);
			}
			previousElevation = xSectionLayer.getElevation();
			previousTopWidth = xSectionLayer.getTopWidth();
		}
		return 0;
	}

	private double interpolateLinearly(double elevation, double thisTopWidth,
			double thisElevation, double previousElevation,
			double previousTopWidth) {
		return (elevation - previousElevation)
				* (thisTopWidth - previousTopWidth)
				/ (thisElevation - previousElevation) + previousTopWidth;
	}

	public LatLng[] getChannelOutlinePoints() {
		NodeMarkerDataManager nodeManager = mapPanel.getNodeManager();
		Node upNode = nodeManager.getNodeData(channel.getUpNodeId());
		Node downNode = nodeManager.getNodeData(channel.getDownNodeId());
		LatLng[] points = null;
		LatLng upPoint = LatLng.newInstance(upNode.getLatitude(), upNode
				.getLongitude());
		LatLng downPoint = LatLng.newInstance(downNode.getLatitude(), downNode
				.getLongitude());
		List<double[]> latLngPoints = channel.getLatLngPoints();
		if ((latLngPoints != null) && (latLngPoints.size() > 0)) {
			int size = latLngPoints.size();
			points = new LatLng[size + 2];
			for (int i = 1; i < points.length - 1; i++) {
				double[] ds = latLngPoints.get(i - 1);
				points[i] = LatLng.newInstance(ds[0], ds[1]);
			}
		} else {
			points = new LatLng[2];
		}
		points[0] = upPoint;
		points[points.length - 1] = downPoint;
		return points;
	}

	public void updateChannelLengthLatLng() {
		channel.setLength(getLengthInFeet());
		int vcount = line.getVertexCount();
		ArrayList<double[]> points = new ArrayList<double[]>();
		for (int i = 1; i < vcount - 1; i++) {
			LatLng vertex = line.getVertex(i);
			double[] point = new double[] { vertex.getLatitude(),
					vertex.getLongitude() };
			points.add(point);
		}
		channel.setLatLngPoints(points);
	}

	public void updateDisplay() {
		ChannelInfoPanel panel = new ChannelInfoPanel(channel);
		mapPanel.getInfoPanel().clear();
		mapPanel.getInfoPanel().add(panel);
	}

	public double getLengthInFeet() {
		return Math.round(line.getLength() * 3.2808399 * 100) / 100;
	}

	public void clearOverlays() {
		mapPanel.getMap().removeOverlay(line);
		for (Polyline xline : xsectionLineMap.values()) {
			mapPanel.getMap().removeOverlay(xline);
		}
	}

}
