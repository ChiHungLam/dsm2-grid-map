package gov.ca.maps.bathymetry.tiles.client;

import gov.ca.maps.bathymetry.tiles.client.model.BathymetryDataPoint;
import gov.ca.modeling.dsm2.widgets.client.ExpandContractMapControl;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.maps.client.Copyright;
import com.google.gwt.maps.client.CopyrightCollection;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapUIOptions;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.TileLayer;
import com.google.gwt.maps.client.control.OverviewMapControl;
import com.google.gwt.maps.client.event.MapClickHandler;
import com.google.gwt.maps.client.event.MapInfoWindowCloseHandler;
import com.google.gwt.maps.client.event.PolylineCancelLineHandler;
import com.google.gwt.maps.client.event.PolylineClickHandler;
import com.google.gwt.maps.client.event.PolylineEndLineHandler;
import com.google.gwt.maps.client.event.PolylineLineUpdatedHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.LatLngBounds;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.overlay.PolyStyleOptions;
import com.google.gwt.maps.client.overlay.Polygon;
import com.google.gwt.maps.client.overlay.Polyline;
import com.google.gwt.maps.client.overlay.TileLayerOverlay;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ToggleButton;

public class MapPanel extends Composite {
	private final MapWidget map;
	private boolean localhost;
	private final BathymetryDataServiceAsync service;
	private Polygon polygon;
	private final String color = "#FF0000";
	private final int weight = 5;
	private final double opacity = 0.75;
	private Polyline line;
	private ShowPolygonHandler showPolygonHandler;
	private RemovePolygonHandler removePolygonHandler;
	private TileLayerOverlay noaaLayer;

	public MapPanel() {
		service = (BathymetryDataServiceAsync) GWT
				.create(BathymetryDataService.class);
		if (GWT.getHostPageBaseURL().contains("localhost")) {
			localhost = true;
		} else {
			localhost = false;
		}
		map = new MapWidget(LatLng.newInstance(38.15, -121.70), 10);

		ExpandContractMapControl fullScreenControl = new ExpandContractMapControl();
		map.addControl(fullScreenControl);

		setOptions();
		addBathymetryOverlay();
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

	public void addLine(final ControlPanel controlPanel) {
		final ToggleButton drawLineButton = controlPanel.getDrawLineButton();
		PolyStyleOptions style = PolyStyleOptions.newInstance(color, weight,
				opacity);
		if (line != null) {
			map.removeOverlay(line);
		}
		line = new Polyline(new LatLng[0]);
		map.addOverlay(line);
		line.setDrawingEnabled();
		line.setEditingEnabled(true);
		line.setStrokeStyle(style);
		line.addPolylineClickHandler(new PolylineClickHandler() {

			public void onClick(PolylineClickEvent event) {
				// editPolyline();
			}
		});
		line.addPolylineLineUpdatedHandler(new PolylineLineUpdatedHandler() {

			public void onUpdate(PolylineLineUpdatedEvent event) {
				if (line.getVertexCount() == 2) {
					line.setEditingEnabled(false);
					drawLineButton.setDown(false);
					drawXSection(controlPanel);
				}
				Window.setStatus("Length : " + getLengthInFeet() + " ft");
			}
		});

		line.addPolylineCancelLineHandler(new PolylineCancelLineHandler() {

			public void onCancel(PolylineCancelLineEvent event) {
				Window.setStatus("Length : " + getLengthInFeet() + " ft");
			}
		});

		line.addPolylineEndLineHandler(new PolylineEndLineHandler() {

			public void onEnd(PolylineEndLineEvent event) {
				Window.setStatus("Length : " + getLengthInFeet() + " ft");
			}
		});

	}

	public double getLengthInFeet() {
		return Math.round(line.getLength() * 3.2808399 * 100) / 100;
	}

	/**
	 * Generate a sequence of QRST that match the tile requested by the
	 * parameters. The parameters are in the google maps tile numbering style,
	 * that differs from the geogarage tile numbering.
	 * 
	 * @param {Number} x tile coordinate (unit is the tile)
	 * @param {Number} y tile coordinate (unit is the tile)
	 * @param {Number} zoom zoom level
	 * @returns The qrst that identifies that tile
	 * @type String
	 */

	public String genQrst(int x, int y, int zoom) {
		int inc = 1 << zoom - 1;
		String qrstString = "t";
		for (int i = zoom; i > 0; i--) {
			boolean xbitIs1 = (x & inc) == inc;
			boolean ybitIs1 = (y & inc) == inc;
			if (xbitIs1 && ybitIs1) {
				qrstString += "s";
			} else if (xbitIs1 && !ybitIs1) {
				qrstString += "r";
			} else if (!xbitIs1 && !ybitIs1) {
				qrstString += "q";
			} else if (!xbitIs1 && ybitIs1) {
				qrstString += "t";
			}
			inc = inc >> 1;
		}
		return qrstString;
	}

	/**
	 * Generate a path where a file corresponding to the string should located,
	 * in GeoGarage quadtree storage structure.
	 * 
	 * @param {String} qrstString the qrst string corresponding to a file.
	 * @returns The corresponding directory path
	 * @type String
	 */
	public String genPathFromQrst(String qrstString) {
		ArrayList<String> directoryElem = new ArrayList<String>();
		int strLength = qrstString.length();
		for (int i = 0; (i + 1) * 6 <= strLength; i++) {
			directoryElem.add(qrstString.substring(i * 6, i * 6 + 6));
		}
		String result = "";
		for (int i = 0; i < directoryElem.size(); i++) {
			result += directoryElem.get(i) + "/";
		}
		return result + qrstString + ".png";
	}

	public void addNOAAOverlay() {
		if (noaaLayer == null) {
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
					String genQrst = genQrst(tile.getX(), tile.getY(),
							zoomLevel);
					String path = genPathFromQrst(genQrst);
					return "http://tiles5.geogarage.com/noaa/" + path;
				}

				public boolean isPng() {
					return true;
				}
			};
			noaaLayer = new TileLayerOverlay(tileLayer);
		}
		map.addOverlay(noaaLayer);
	}

	public void removeNOAAOverlay() {
		if (noaaLayer != null) {
			map.removeOverlay(noaaLayer);
		}
	}

	private void addBathymetryOverlay() {
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
				String uniqueValue = tile.getX() + "" + tile.getY() + ""
						+ zoomLevel;
				int hashCode = uniqueValue.hashCode();
				if (localhost) {
					return "/tiles/" + hashCode + "_tile" + tile.getX() + "_"
							+ tile.getY() + "_" + zoomLevel + ".png";
				} else {
					int version = (tile.getX() + tile.getY()) % 4 + 1;
					return "http://" + version
							+ ".latest.dsm2bathymetry.appspot.com/tiles/"
							+ hashCode + "_tile" + tile.getX() + "_"
							+ tile.getY() + "_" + zoomLevel + ".png";
				}
			}

			public boolean isPng() {
				return true;
			}
		};
		map.addOverlay(new TileLayerOverlay(tileLayer));
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

}
