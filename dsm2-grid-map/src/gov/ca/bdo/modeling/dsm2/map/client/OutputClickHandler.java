package gov.ca.bdo.modeling.dsm2.map.client;

import gov.ca.bdo.modeling.dsm2.map.client.model.OutputMarkerDataManager;

import com.google.gwt.maps.client.event.MarkerClickHandler;

public class OutputClickHandler implements MarkerClickHandler {
	private String name;
	private OutputMarkerDataManager outputManager;
	private MapPanel mapPanel;

	public OutputClickHandler(String name,
			OutputMarkerDataManager outputManager, MapPanel mapPanel) {
		this.name = name;
		this.outputManager = outputManager;
		this.mapPanel = mapPanel;
	}

	public void onClick(MarkerClickEvent event) {

		mapPanel.getInfoPanel().clear();

		mapPanel.getInfoPanel().add(
				new OutputPanel(name, outputManager.getOutputVariables(name),
						outputManager));

	}

}
