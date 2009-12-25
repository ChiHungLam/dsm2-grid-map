package gov.ca.bdo.modeling.dsm2.map.client;

import gov.ca.dsm2.input.model.Reservoir;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.maps.client.event.MarkerClickHandler;
import com.google.gwt.maps.client.event.PolygonClickHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Polygon;
import com.google.gwt.user.client.ui.Panel;

public class ReservoirClickHandler implements MarkerClickHandler {

	private Reservoir reservoir;
	private Panel infoPanel;
	private MapPanel mapPanel;
	private Polygon polygon;

	public ReservoirClickHandler(Reservoir reservoir, Panel infoPanel,
			MapPanel mapPanel) {
		this.reservoir = reservoir;
		this.infoPanel = infoPanel;
		this.mapPanel = mapPanel;
	}

	public void onClick(MarkerClickEvent event) {
		ReservoirInfoPanel panel = new ReservoirInfoPanel(this.reservoir);
		infoPanel.clear();
		infoPanel.add(panel);
		List<double[]> latLngPoints = reservoir.getLatLngPoints();
		if (polygon == null) {
			LatLng[] points;
			if (latLngPoints != null && latLngPoints.size() > 0) {
				points = new LatLng[latLngPoints.size()];
				for (int i = 0; i < points.length; i++) {
					double[] latLngPoint = latLngPoints.get(i);
					points[i] = LatLng.newInstance(latLngPoint[0],
							latLngPoint[1]);
				}
			} else {
				points = new LatLng[5];
				double latitude = reservoir.getLatitude();
				double longitude = reservoir.getLongitude();
				double jitter = 0.02;
				points[0] = LatLng.newInstance(latitude - jitter, longitude
						- jitter);
				points[1] = LatLng.newInstance(latitude - jitter, longitude
						+ jitter);
				points[2] = LatLng.newInstance(latitude + jitter, longitude
						+ jitter);
				points[3] = LatLng.newInstance(latitude + jitter, longitude
						- jitter);
				points[4] = LatLng.newInstance(latitude - jitter, longitude
						- jitter);
			}

			polygon = new Polygon(points);

		} else {
			if (polygon.isVisible()) {
				updateReservoirAreaLatLng();
				polygon.setVisible(false);
				mapPanel.getMap().removeOverlay(polygon);
				return;
			} else {
				polygon.setVisible(true);
			}
		}
		mapPanel.getMap().addOverlay(polygon);
		if (mapPanel.isInEditMode()) {
			polygon.addPolygonClickHandler(new PolygonClickHandler() {

				public void onClick(PolygonClickEvent event) {
					updateReservoirAreaLatLng();
					polygon.setEditingEnabled(false);
					mapPanel.getMap().removeOverlay(polygon);
					updateDisplay();
				}
			});
			polygon.setEditingEnabled(true);
		} else {
			//
		}
	}

	public void updateReservoirAreaLatLng() {
		reservoir.setArea(getAreaInSquareFeet() / 1e6);
		int vcount = polygon.getVertexCount();
		ArrayList<double[]> latLngPoints = new ArrayList<double[]>();
		for (int i = 0; i < vcount; i++) {
			LatLng vertex = polygon.getVertex(i);
			latLngPoints.add(new double[] { vertex.getLatitude(),
					vertex.getLongitude() });
		}
		reservoir.setLatLngPoints(latLngPoints);
	}

	public void updateDisplay() {
		ReservoirInfoPanel panel = new ReservoirInfoPanel(reservoir);
		infoPanel.clear();
		infoPanel.add(panel);
	}

	public double getAreaInSquareFeet() {
		return Math.round(polygon.getArea() * 3.2808399 * 3.2808399 * 100) / 100;
	}

}