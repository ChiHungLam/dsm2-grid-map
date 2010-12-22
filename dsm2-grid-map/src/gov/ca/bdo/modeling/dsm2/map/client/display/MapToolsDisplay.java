package gov.ca.bdo.modeling.dsm2.map.client.display;

import gov.ca.bdo.modeling.dsm2.map.client.map.AddMapElementClickHandler;
import gov.ca.bdo.modeling.dsm2.map.client.map.MeasuringAreaInPolygon;
import gov.ca.bdo.modeling.dsm2.map.client.map.MeasuringDistanceAlongLine;
import gov.ca.dsm2.input.model.Channel;
import gov.ca.modeling.maps.elevation.client.BathymetryDisplayer;
import gov.ca.modeling.maps.elevation.client.ElevationDisplayer;
import gov.ca.modeling.maps.elevation.client.ElevationProfileDisplayer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.event.MapClickHandler;
import com.google.gwt.maps.client.event.PolylineLineUpdatedHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.GeoXmlLoadCallback;
import com.google.gwt.maps.client.overlay.GeoXmlOverlay;
import com.google.gwt.maps.client.overlay.PolyStyleOptions;
import com.google.gwt.maps.client.overlay.Polyline;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MapToolsDisplay extends MapDisplay {

	private MeasuringDistanceAlongLine lengthMeasurer;
	private MeasuringAreaInPolygon areaMeasurer;
	private ElevationDisplayer elevationDisplayer;
	private FlowPanel infoPanel;
	
	public MapToolsDisplay(ContainerDisplay display, boolean viewOnly) {
		super(display, viewOnly, new VerticalPanel());
		// TODO Auto-generated constructor stub
	}

	public void startMeasuringDistanceAlongLine() {
		if (lengthMeasurer == null) {
			lengthMeasurer = new MeasuringDistanceAlongLine(mapPanel
					.getMapWidget());
			lengthMeasurer.addPolyline();
		}
	}

	public void stopMeasuringDistanceAlongLine() {
		if (lengthMeasurer != null) {
			lengthMeasurer.clearOverlay();
			lengthMeasurer = null;
		}
	}

	public void startMeasuringAreaInPolygon() {
		if (areaMeasurer == null) {
			areaMeasurer = new MeasuringAreaInPolygon(mapPanel.getMap());
			areaMeasurer.addPolyline();
		}
	}

	public void stopMeasuringAreaInPolygon() {
		if (areaMeasurer != null) {
			areaMeasurer.clearOverlay();
			areaMeasurer = null;
		}
	}

	public void turnOnTextAnnotation() {
		mapPanel.turnOnTextAnnotation();
	}

	public void turnOffTextAnnotation() {
		mapPanel.turnOffTextAnnotation();
	}

	public void addKmlOverlay(final String url) {
		GeoXmlOverlay.load(url, new GeoXmlLoadCallback() {

			@Override
			public void onFailure(String url, Throwable e) {
				StringBuffer message = new StringBuffer("KML File " + url
						+ " failed to load");
				if (e != null) {
					message.append(e.toString());
				}
				Window.alert(message.toString());
			}

			@Override
			public void onSuccess(String url, GeoXmlOverlay overlay) {
				if (overlay == null) {
					return;
				}
				GWT.log("KML File " + url + " loaded successfully", null);
				mapPanel.getMap().addOverlay(overlay);
			}
		});
	}

	public void hideFlowLines() {
		mapPanel.hideFlowLines();
	}

	public void showFlowLines() {
		mapPanel.showFlowLines();
	}

	private Polyline line;
	private ElevationProfileDisplayer elevationProfileDisplayer;
	private BathymetryDisplayer bathymetryDisplayer;
	private AddMapElementClickHandler addMapElementHandler;
	private MapClickHandler deleteMapElementHandler;

	public void addLine(Channel channel) {
		MapWidget map = mapPanel.getMap();
		PolyStyleOptions style = PolyStyleOptions.newInstance("#0000ff", 3, 1);
		if (line != null) {
			map.removeOverlay(line);
		}
		line = new Polyline(new LatLng[0]);
		map.addOverlay(line);
		line.setDrawingEnabled();
		line.setEditingEnabled(true);
		line.setStrokeStyle(style);
		line.addPolylineLineUpdatedHandler(new PolylineLineUpdatedHandler() {

			public void onUpdate(PolylineLineUpdatedEvent event) {
				if (line.getVertexCount() == 2) {
					line.setEditingEnabled(false);

					// drawLineButton.setDown(false);
					// drawXSection(line.getVertex(0),line.getVertex(1));
				}
			}
		});

	}

	public double getLengthInFeet() {
		return Math.round(line.getLength() * 3.2808399 * 100) / 100;
	}

	public void startClickingForElevation() {
		if (elevationDisplayer == null) {
			elevationDisplayer = new ElevationDisplayer(mapPanel.getMap());
		}
		elevationDisplayer.start();
	}

	public void stopClickingForElevation() {
		if (elevationDisplayer != null) {
			elevationDisplayer.stop();
		}
	}

	public void startDrawingElevationProfileLine() {
		if (elevationProfileDisplayer == null) {
			elevationProfileDisplayer = new ElevationProfileDisplayer(mapPanel
					.getMap(), infoPanel);
		}
		/*
		 * elevationProfileDisplayer .startDrawingLine((ToggleButton)
		 * getDisplayElevationProfileButton());
		 */
	}

	public void stopDrawingElevationProfileLine() {
		if (elevationProfileDisplayer != null) {
			elevationProfileDisplayer.stopDrawingLine();
		}
	}

	public void startShowingBathymetryPoints() {
		if (bathymetryDisplayer == null) {
			bathymetryDisplayer = new BathymetryDisplayer(mapPanel.getMap());
		}
		bathymetryDisplayer.activateShowDataHandler(true);
	}

	public void stopShowingBathymetryPoints() {
		if (bathymetryDisplayer != null) {
			bathymetryDisplayer.activateShowDataHandler(false);
		}
	}

}
