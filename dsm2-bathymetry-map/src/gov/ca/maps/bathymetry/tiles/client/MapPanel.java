/**
 *    Copyright (C) 2009, 2010 
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
 *    GNU General Public License for more details. [http://www.gnu.org/licenses]
 *    
 *    @author Nicky Sandhu
 *    
 */
package gov.ca.maps.bathymetry.tiles.client;

import gov.ca.maps.bathymetry.tiles.client.ExportOverlays.BathymetryTileLayer;
import gov.ca.maps.bathymetry.tiles.client.model.BathymetryDataPoint;
import gov.ca.modeling.dsm2.widgets.client.ExpandContractMapControl;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapUIOptions;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.TileLayer;
import com.google.gwt.maps.client.control.OverviewMapControl;
import com.google.gwt.maps.client.event.MapClickHandler;
import com.google.gwt.maps.client.event.MapInfoWindowCloseHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Overlay;
import com.google.gwt.maps.client.overlay.Polygon;
import com.google.gwt.maps.client.overlay.Polyline;
import com.google.gwt.maps.client.overlay.TileLayerOverlay;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;

public class MapPanel extends Composite {
	private final MapWidget map;
	private final BathymetryDataServiceAsync service;
	private Polygon polygon;
	private Polyline line;
	private ShowPolygonHandler showPolygonHandler;
	private RemovePolygonHandler removePolygonHandler;
	private TileLayerOverlay noaaLayer;
	private Overlay currentOverlay;
	private double opacity = 0.6;
	private BathymetryTileLayer currentTileLayer;

	public MapPanel(double latCenter, double lngCenter, int zoom) {
		service = (BathymetryDataServiceAsync) GWT
				.create(BathymetryDataService.class);
		map = new MapWidget(LatLng.newInstance(latCenter, lngCenter), zoom);

		ExpandContractMapControl fullScreenControl = new ExpandContractMapControl();
		map.addControl(fullScreenControl);

		setOptions();
		addOverlay("");
		initWidget(map);
	}

	public void drawXSection(final ControlPanel controlPanel) {
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
						controlPanel
								.showInInfoPanel(new ElevationChart(result));
					}

					public void onFailure(Throwable caught) {
					}
				});
	}

	public void addNOAAOverlay() {
		if (noaaLayer == null) {
			TileLayer tileLayer = ExportOverlays.getNOAATileLayer();
			noaaLayer = new TileLayerOverlay(tileLayer);
		}
		map.addOverlay(noaaLayer);
	}

	public void removeNOAAOverlay() {
		if (noaaLayer != null) {
			map.removeOverlay(noaaLayer);
		}
	}

	public void removeOverlays() {
		if (currentOverlay != null) {
			map.removeOverlay(currentOverlay);
		}
	}

	public void addOverlay(String prefix) {
		BathymetryTileLayer tileLayer = ExportOverlays
				.getBathymetryTileLayer(prefix);
		currentTileLayer = tileLayer;
		currentTileLayer.setOpacity(getLayerOpacity());
		currentOverlay = new TileLayerOverlay(tileLayer);
		map.addOverlay(currentOverlay);
	}

	private void setOptions() {
		MapUIOptions options = MapUIOptions.newInstance(map.getSize());
		options.setDoubleClick(true);
		options.setKeyboard(true);
		options.setMapTypeControl(true);
		options.setMenuMapTypeControl(false);
		options.setNormalMapType(true);
		options.setPhysicalMapType(true);
		options.setSatelliteMapType(true);
		options.setScaleControl(true);
		// options.setScrollwheel(true);
		options.setLargeMapControl3d(true);
		map.setUI(options);
		OverviewMapControl control = new OverviewMapControl();
		map.addControl(control);
	}

	public void onResize() {
		map.checkResizeAndCenter();
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
	}

	private final class RemovePolygonHandler implements
			MapInfoWindowCloseHandler {
		public void onInfoWindowClose(MapInfoWindowCloseEvent event) {
			if (polygon != null) {
				map.removeOverlay(polygon);
				polygon = null;
			}
		}
	}

	public void setLayerOpacity(double opacity) {
		this.opacity = opacity;
		if (currentTileLayer != null) {
			currentTileLayer.setOpacity(opacity);
			if (currentOverlay != null) {
				map.removeOverlay(currentOverlay);
				map.addOverlay(currentOverlay);
			}
		}
	}

	public double getLayerOpacity() {
		return opacity;
	}

	public String generateLinkURL() {
		return GWT.getHostPageBaseURL() + "?" + "lat="
				+ map.getCenter().getLatitude() + "&lng="
				+ map.getCenter().getLongitude() + "&z=" + map.getZoomLevel();
	}

}
