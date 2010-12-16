package gov.ca.bdo.modeling.dsm2.map.client.display;

import gov.ca.bdo.modeling.dsm2.map.client.map.MapPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.maps.client.event.MapClickHandler;
import com.google.gwt.maps.client.overlay.Overlay;

public class DeleteMapElementClickHandler implements MapClickHandler {

	private MapPanel mapPanel;

	public DeleteMapElementClickHandler(MapPanel mapPanel) {
		this.mapPanel = mapPanel;
	}

	public void onClick(MapClickEvent event) {
		Overlay overlay = event.getOverlay();
		if (overlay == null){
			return;
		}
		GWT.log("Deleting overlay: "+overlay);
	}

}
