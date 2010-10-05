package gov.ca.maps.bathymetry.tiles.client;

import gov.ca.modeling.maps.elevation.client.BathymetryDisplayer;
import gov.ca.modeling.maps.elevation.client.ElevationDisplayer;
import gov.ca.modeling.maps.elevation.client.ElevationProfileDisplayer;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class DataPanel extends Composite {
	private final FlowPanel infoPanel;
	private ToggleButton elevationButton;
	private ToggleButton elevationProfileButton;
	private ElevationDisplayer elevationDisplayer;
	private ElevationProfileDisplayer elevationProfileDisplayer;
	private BathymetryDisplayer bathymetryDisplayer;

	public DataPanel(final MapPanel mapPanel) {
		final ToggleButton showData = new ToggleButton("Click to show data");
		elevationButton = new ToggleButton("Show Elevation");
		elevationProfileButton = new ToggleButton("Show Elevation Profile");
		showData.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				if (bathymetryDisplayer == null) {
					bathymetryDisplayer = new BathymetryDisplayer(mapPanel
							.getMap());
				}
				if (showData.isDown()) {
					bathymetryDisplayer.activateShowDataHandler(true);
				} else {
					bathymetryDisplayer.activateShowDataHandler(false);
				}
			}
		});
		elevationButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (elevationDisplayer == null) {
					elevationDisplayer = new ElevationDisplayer(mapPanel
							.getMap());
				}
				if (elevationButton.isDown()) {
					elevationDisplayer.start();
				} else {
					elevationDisplayer.stop();
				}
			}
		});
		elevationProfileButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (elevationProfileDisplayer == null) {
					elevationProfileDisplayer = new ElevationProfileDisplayer(
							mapPanel.getMap(), infoPanel);
				}
				if (elevationProfileButton.isDown()) {
					elevationProfileDisplayer
							.startDrawingLine(elevationProfileButton);
				} else {
					elevationProfileDisplayer.stopDrawingLine();
				}

			}
		});

		showData
				.setTitle("Toggle button to display raw data around point clicked on map");

		infoPanel = new FlowPanel();
		infoPanel.setStyleName("infoPanel");

		VerticalPanel dataPanel = new VerticalPanel();
		dataPanel.add(showData);
		dataPanel.add(new HTML("<hr/>"));
		dataPanel.add(elevationButton);
		dataPanel.add(new HTML("<hr/>"));
		dataPanel.add(elevationProfileButton);
		dataPanel.add(new HTML("<hr/>"));
		dataPanel.add(infoPanel);

		FlowPanel mainPanel = new FlowPanel();
		mainPanel.add(dataPanel);
		mainPanel.setWidth("500px");

		initWidget(mainPanel);

	}

	public void showInInfoPanel(Widget widget) {
		infoPanel.clear();
		infoPanel.add(widget);
	}
}
