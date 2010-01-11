package gov.ca.bdo.modeling.dsm2.map.client;

import gov.ca.bdo.modeling.dsm2.map.client.model.NodeMarkerDataManager;
import gov.ca.dsm2.input.model.Channel;
import gov.ca.dsm2.input.model.Node;
import gov.ca.dsm2.input.model.XSection;
import gov.ca.dsm2.input.model.XSectionLayer;

import java.util.ArrayList;
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
		ChannelInfoPanel panel = new ChannelInfoPanel(channel);
		mapPanel.getInfoPanel().clear();
		mapPanel.getInfoPanel().add(panel);
		if (line != null) {
			if (line.isVisible()) {
				line.setVisible(false);
				mapPanel.getMap().removeOverlay(line);
				return;
			} else {
				line.setVisible(true);
			}
			mapPanel.getMap().addOverlay(line);
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
					mapPanel.getMap().removeOverlay(line);
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
		}
	}

	private void drawXSectionLines() {
		LatLng[] channelOutlinePoints = getChannelOutlinePoints();
		ArrayList<XSection> xsections = channel.getXsections();
		for (XSection xSection : xsections) {
			double distance = xSection.getDistance();
			distance = channel.getLength() * distance;
			LatLng latLng = findLatLngOfXSectionIntersectionAlongOutline(
					distance, channelOutlinePoints);
			double width = getTopWidthAtElevation(xSection, 0);
			drawLineCenteredAtLatLngOfWidth(latLng, width);
		}
	}

	private void drawLineCenteredAtLatLngOfWidth(LatLng latLng, double width) {
		// TODO Auto-generated method stub

	}

	private LatLng findLatLngOfXSectionIntersectionAlongOutline(
			double distance, LatLng[] channelOutlinePoints) {

		if (distance < 0) {
			return channelOutlinePoints[0];
		}
		LatLng intersection = channelOutlinePoints[0];
		return intersection;
	}

	private double getTopWidthAtElevation(XSection xsection, double elevation) {
		ArrayList<XSectionLayer> layers = xsection.getLayers();
		double previousElevation = 0;
		double previousTopWidth = 0;
		for (XSectionLayer xSectionLayer : layers) {
			if (elevation < xSectionLayer.getElevation()) {
				return interpolateLinearly(xSectionLayer.getTopWidth(),
						xSectionLayer.getElevation(), previousElevation,
						previousTopWidth);
			}
			previousElevation = xSectionLayer.getElevation();
			previousTopWidth = xSectionLayer.getTopWidth();
		}
		return 0;
	}

	private double interpolateLinearly(double topWidth, double elevation,
			double previousElevation, double previousTopWidth) {
		// TODO Auto-generated method stub
		return 0;
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

}