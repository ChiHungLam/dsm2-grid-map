package gov.ca.bdo.modeling.dsm2.map.client;

import gov.ca.dsm2.input.model.Gate;

import com.google.gwt.maps.client.InfoWindow;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.event.MarkerClickHandler;
import com.google.gwt.user.client.ui.Panel;

public class GateClickHandler implements MarkerClickHandler {

	private Gate gate;
	private Panel infoPanel;
	private MapWidget map;

	public GateClickHandler(Gate gate, Panel panel, MapWidget map) {
		this.gate = gate;
		this.infoPanel = panel;
		this.map = map;
	}

	public void onClick(MarkerClickEvent event) {
		GateInfoPanel panel = new GateInfoPanel(this.gate);
		infoPanel.clear();
		infoPanel.add(panel);
		InfoWindowContent content = new InfoWindowContent(panel
				.getBasicInfoPanel(this.gate));
		InfoWindow window = map.getInfoWindow();
		window.open(event.getSender().getLatLng(), content);
	}

}
