package gov.ca.modeling.maps.elevation.client;

import gov.ca.modeling.maps.elevation.client.model.BathymetryDataPoint;
import gov.ca.modeling.maps.elevation.client.service.BathymetryDataService;
import gov.ca.modeling.maps.elevation.client.service.BathymetryDataServiceAsync;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.event.MapClickHandler;
import com.google.gwt.maps.client.event.MapInfoWindowCloseHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Polygon;
import com.google.gwt.maps.client.overlay.Polyline;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;

public class BathymetryDisplayer {
	private RemovePolygonHandler removePolygonHandler;
	private ShowPolygonHandler showPolygonHandler;
	private Polyline line;
	private Polygon polygon;
	private BathymetryDataServiceAsync service;
	private MapWidget map;

	public BathymetryDisplayer(MapWidget map) {
		this.map = map;
		service = (BathymetryDataServiceAsync) GWT
				.create(BathymetryDataService.class);

	}

	public void drawXSection(final FlowPanel infoPanel) {
		if ((line == null) || (line.getVertexCount() != 2)) {
			return;
		}
		LatLng startVertex = line.getVertex(0);
		LatLng endVertex = line.getVertex(1);
		service.getBathymetryDataPoints(startVertex.getLatitude(), startVertex
				.getLongitude(), endVertex.getLatitude(), endVertex
				.getLongitude(),
				new AsyncCallback<List<BathymetryDataPoint>>() {

					public void onSuccess(List<BathymetryDataPoint> result) {
						infoPanel.clear();
						infoPanel.add(new ElevationChart(result));
					}

					public void onFailure(Throwable caught) {
					}
				});
	}

	/**
	 * activates the handler that retrieves the data points around point
	 * clicked.
	 * 
	 * @param activate
	 */
	public void activateShowDataHandler(boolean activate) {
		if (activate) {
			if (removePolygonHandler == null) {
				removePolygonHandler = new RemovePolygonHandler();
			}
			if (showPolygonHandler == null) {
				showPolygonHandler = new ShowPolygonHandler();
			}
			map.addInfoWindowCloseHandler(removePolygonHandler);
			map.addMapClickHandler(showPolygonHandler);
		} else {
			if (removePolygonHandler != null) {
				map.removeInfoWindowCloseHandler(removePolygonHandler);
			}
			if (showPolygonHandler != null) {
				map.removeMapClickHandler(showPolygonHandler);
			}
		}
	}

	private final class ShowPolygonHandler implements MapClickHandler {
		public void onClick(MapClickEvent event) {
			final LatLng latLng = event.getLatLng();
			service.getBathymetryDataPoints(latLng.getLatitude(), latLng
					.getLongitude(),
					new AsyncCallback<List<BathymetryDataPoint>>() {

						public void onSuccess(List<BathymetryDataPoint> result) {
							if ((result != null) && (result.size() > 0)) {
								if (polygon != null) {
									map.removeOverlay(polygon);
								}
								double lat1000 = Math.floor(latLng
										.getLatitude() * 1000) / 1000;
								double lng1000 = Math.floor(latLng
										.getLongitude() * 1000) / 1000;
								LatLng[] points = new LatLng[] {
										LatLng.newInstance(lat1000, lng1000),
										LatLng.newInstance(lat1000 + 0.001,
												lng1000),
										LatLng.newInstance(lat1000 + 0.001,
												lng1000 + 0.001),
										LatLng.newInstance(lat1000,
												lng1000 + 0.001),
										LatLng.newInstance(lat1000, lng1000) };
								polygon = new Polygon(points, "blue", 2, 0.25,
										"blue", 0.25);
								map.addOverlay(polygon);
								map.getInfoWindow().open(
										latLng,
										new InfoWindowContent(
												new BathymetryDataInfoPanel(
														result)));
							}

						}

						public void onFailure(Throwable caught) {
							Window.setStatus("Error: " + caught.getMessage());
						}
					});
		}
	};

	private final class RemovePolygonHandler implements
			MapInfoWindowCloseHandler {
		public void onInfoWindowClose(MapInfoWindowCloseEvent event) {
			if (polygon != null) {
				map.removeOverlay(polygon);
				polygon = null;
			}
		}
	};

}
