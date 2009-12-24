package gov.ca.bdo.modeling.dsm2.map.client;

import gov.ca.bdo.modeling.dsm2.map.client.model.OutputMarkerDataManager;

import com.google.gwt.maps.client.event.MarkerClickHandler;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.AnnotatedTimeLine;

public class OutputClickHandler implements MarkerClickHandler {
	private final String name;
	private final OutputMarkerDataManager outputManager;
	private final MapPanel mapPanel;

	public OutputClickHandler(String name,
			OutputMarkerDataManager outputManager, MapPanel mapPanel) {
		this.name = name;
		this.outputManager = outputManager;
		this.mapPanel = mapPanel;
	}

	public void onClick(final MarkerClickEvent event) {
		Runnable visualizationLoadCallback = new Runnable() {
			public void run() {
				doOnClick(event);
			}
		};
		// Load the visualization api, passing the onLoadCallback to be called
		// when loading is done.
		VisualizationUtils.loadVisualizationApi(visualizationLoadCallback,
				AnnotatedTimeLine.PACKAGE);
	}

	public void doOnClick(MarkerClickEvent event) {

		mapPanel.getInfoPanel().clear();

		mapPanel.getInfoPanel().add(
				new OutputPanel(name, outputManager.getOutputVariables(name),
						outputManager));

	}

}
