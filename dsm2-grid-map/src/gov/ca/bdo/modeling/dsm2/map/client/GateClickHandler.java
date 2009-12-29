package gov.ca.bdo.modeling.dsm2.map.client;

import gov.ca.dsm2.input.model.Gate;

import com.google.gwt.maps.client.InfoWindow;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.event.MarkerClickHandler;

public class GateClickHandler implements MarkerClickHandler {

	private final Gate gate;
	private final MapPanel mapPanel;

	public GateClickHandler(Gate gate, MapPanel mapPanel) {
		this.gate = gate;
		this.mapPanel = mapPanel;
	}

	public void onClick(MarkerClickEvent event) {
		GateInfoPanel panel = new GateInfoPanel(gate);
		mapPanel.getInfoPanel().clear();
		mapPanel.getInfoPanel().add(panel);
		InfoWindowContent content = new InfoWindowContent(panel
				.getBasicInfoPanel(gate));
		InfoWindow window = mapPanel.getMap().getInfoWindow();
		window.open(event.getSender().getLatLng(), content);
	}

}
