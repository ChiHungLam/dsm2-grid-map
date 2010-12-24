package gov.ca.bdo.modeling.dsm2.map.client.display;

import gov.ca.bdo.modeling.dsm2.map.client.map.MeasuringAreaInPolygon;
import gov.ca.bdo.modeling.dsm2.map.client.map.MeasuringDistanceAlongLine;
import gov.ca.bdo.modeling.dsm2.map.client.presenter.MapToolsPresenter.Display;
import gov.ca.dsm2.input.model.Channel;
import gov.ca.modeling.maps.elevation.client.BathymetryDisplayer;
import gov.ca.modeling.maps.elevation.client.ElevationDisplayer;
import gov.ca.modeling.maps.elevation.client.ElevationProfileDisplayer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.event.PolylineLineUpdatedHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.GeoXmlLoadCallback;
import com.google.gwt.maps.client.overlay.GeoXmlOverlay;
import com.google.gwt.maps.client.overlay.PolyStyleOptions;
import com.google.gwt.maps.client.overlay.Polyline;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MapToolsDisplay extends MapDisplay implements Display {

	private MeasuringDistanceAlongLine lengthMeasurer;
	private MeasuringAreaInPolygon areaMeasurer;
	private ElevationDisplayer elevationDisplayer;
	private FlowPanel infoPanel;
	private Polyline line;
	private ElevationProfileDisplayer elevationProfileDisplayer;
	private BathymetryDisplayer bathymetryDisplayer;
	private FlowPanel controlPanel;
	public TextBox findTextBox;
	public Button findButton;
	public Button showFlowlinesButton;
	public Button showChannelAreaButton;
	public Button measureLengthButton;
	public Button measureAreaButton;
	public Button measureVolumeButton;
	public Button measureAverageElevationButton;
	public Button showProfileButton;
	public Button showElevationButton;
	public Button showBathymetryButton;

	public MapToolsDisplay(ContainerDisplay display, boolean viewOnly) {
		super(display, viewOnly, new VerticalPanel());
		controlPanel = new FlowPanel();
		controlPanel.setStyleName("control-panel");
		CaptionPanel captionPanel = new CaptionPanel("Find");
		controlPanel.add(captionPanel);
		HorizontalPanel findPanel = new HorizontalPanel();
		captionPanel.add(findPanel);
		findPanel.add(findTextBox = new TextBox());
		findPanel.add(findButton = new Button("Find"));
		//
		captionPanel = new CaptionPanel("Grid Outline");
		controlPanel.add(captionPanel);
		HorizontalPanel gridOutlinePanel = new HorizontalPanel();
		captionPanel.add(gridOutlinePanel);
		gridOutlinePanel.add(showFlowlinesButton=new Button("Show Flow Lines"));
		gridOutlinePanel.add(showChannelAreaButton=new Button("Show Channel Area"));
		//
		captionPanel = new CaptionPanel("Measure");
		controlPanel.add(captionPanel);
		HorizontalPanel measurePanel = new HorizontalPanel();
		captionPanel.add(measurePanel);
		measurePanel.add(measureLengthButton=new Button("Measure Length"));
		measurePanel.add(measureAreaButton=new Button("Measure Area"));
		measurePanel.add(measureVolumeButton=new Button("Measure Volume"));
		measurePanel.add(measureAverageElevationButton=new Button("Measure Average Elevation"));
		//
		captionPanel = new CaptionPanel("Elevation");
		controlPanel.add(captionPanel);
		HorizontalPanel elevationPanel = new HorizontalPanel();
		captionPanel.add(elevationPanel);
		elevationPanel.add(showElevationButton=new Button("Show Elevation"));
		elevationPanel.add(showProfileButton=new Button("Show Profile"));
		elevationPanel.add(showBathymetryButton=new Button("Show Bathymetry"));
	}

	@Override
	protected void initializeUI() {
		super.initializeUI();
		// layout top level things here
		VerticalPanel sidePanel = (VerticalPanel) super.getSidePanel();
		sidePanel.add(new ScrollPanel(controlPanel));
		mapPanel.setInfoPanel(infoPanel);
	}

	/* (non-Javadoc)
	 * @see gov.ca.bdo.modeling.dsm2.map.client.display.IMapDisplay#startMeasuringDistanceAlongLine()
	 */
	public void startMeasuringDistanceAlongLine() {
		if (lengthMeasurer == null) {
			lengthMeasurer = new MeasuringDistanceAlongLine(mapPanel
					.getMapWidget());
			lengthMeasurer.addPolyline();
		}
	}

	/* (non-Javadoc)
	 * @see gov.ca.bdo.modeling.dsm2.map.client.display.IMapDisplay#stopMeasuringDistanceAlongLine()
	 */
	public void stopMeasuringDistanceAlongLine() {
		if (lengthMeasurer != null) {
			lengthMeasurer.clearOverlay();
			lengthMeasurer = null;
		}
	}

	public TextBox getFindTextBox() {
		return findTextBox;
	}

	public Button getFindButton() {
		return findButton;
	}

	public Button getShowFlowlinesButton() {
		return showFlowlinesButton;
	}

	public Button getShowChannelAreaButton() {
		return showChannelAreaButton;
	}

	public Button getMeasureLengthButton() {
		return measureLengthButton;
	}

	public Button getMeasureAreaButton() {
		return measureAreaButton;
	}

	public Button getMeasureVolumeButton() {
		return measureVolumeButton;
	}

	public Button getMeasureAverageElevationButton() {
		return measureAverageElevationButton;
	}

	public Button getShowProfileButton() {
		return showProfileButton;
	}

	public Button getShowElevationButton() {
		return showElevationButton;
	}

	public Button getShowBathymetryButton() {
		return showBathymetryButton;
	}

	/* (non-Javadoc)
	 * @see gov.ca.bdo.modeling.dsm2.map.client.display.IMapDisplay#startMeasuringAreaInPolygon()
	 */
	public void startMeasuringAreaInPolygon() {
		if (areaMeasurer == null) {
			areaMeasurer = new MeasuringAreaInPolygon(mapPanel.getMap());
			areaMeasurer.addPolyline();
		}
	}

	/* (non-Javadoc)
	 * @see gov.ca.bdo.modeling.dsm2.map.client.display.IMapDisplay#stopMeasuringAreaInPolygon()
	 */
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

	/* (non-Javadoc)
	 * @see gov.ca.bdo.modeling.dsm2.map.client.display.IMapDisplay#addKmlOverlay(java.lang.String)
	 */
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

	/* (non-Javadoc)
	 * @see gov.ca.bdo.modeling.dsm2.map.client.display.IMapDisplay#hideFlowLines()
	 */
	public void hideFlowLines() {
		mapPanel.hideFlowLines();
	}

	/* (non-Javadoc)
	 * @see gov.ca.bdo.modeling.dsm2.map.client.display.IMapDisplay#showFlowLines()
	 */
	public void showFlowLines() {
		mapPanel.showFlowLines();
	}

	/* (non-Javadoc)
	 * @see gov.ca.bdo.modeling.dsm2.map.client.display.IMapDisplay#addLine(gov.ca.dsm2.input.model.Channel)
	 */
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

	/* (non-Javadoc)
	 * @see gov.ca.bdo.modeling.dsm2.map.client.display.IMapDisplay#startClickingForElevation()
	 */
	public void startClickingForElevation() {
		if (elevationDisplayer == null) {
			elevationDisplayer = new ElevationDisplayer(mapPanel.getMap());
		}
		elevationDisplayer.start();
	}

	/* (non-Javadoc)
	 * @see gov.ca.bdo.modeling.dsm2.map.client.display.IMapDisplay#stopClickingForElevation()
	 */
	public void stopClickingForElevation() {
		if (elevationDisplayer != null) {
			elevationDisplayer.stop();
		}
	}

	/* (non-Javadoc)
	 * @see gov.ca.bdo.modeling.dsm2.map.client.display.IMapDisplay#startDrawingElevationProfileLine()
	 */
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

	/* (non-Javadoc)
	 * @see gov.ca.bdo.modeling.dsm2.map.client.display.IMapDisplay#stopDrawingElevationProfileLine()
	 */
	public void stopDrawingElevationProfileLine() {
		if (elevationProfileDisplayer != null) {
			elevationProfileDisplayer.stopDrawingLine();
		}
	}

	/* (non-Javadoc)
	 * @see gov.ca.bdo.modeling.dsm2.map.client.display.IMapDisplay#startShowingBathymetryPoints()
	 */
	public void startShowingBathymetryPoints() {
		if (bathymetryDisplayer == null) {
			bathymetryDisplayer = new BathymetryDisplayer(mapPanel.getMap());
		}
		bathymetryDisplayer.activateShowDataHandler(true);
	}

	/* (non-Javadoc)
	 * @see gov.ca.bdo.modeling.dsm2.map.client.display.IMapDisplay#stopShowingBathymetryPoints()
	 */
	public void stopShowingBathymetryPoints() {
		if (bathymetryDisplayer != null) {
			bathymetryDisplayer.activateShowDataHandler(false);
		}
	}

}
