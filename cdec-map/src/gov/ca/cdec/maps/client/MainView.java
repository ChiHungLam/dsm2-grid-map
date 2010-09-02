package gov.ca.cdec.maps.client;

import gov.ca.cdec.maps.client.model.Station;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class MainView extends Composite {
	interface DisplayBinder extends UiBinder<Widget, MainView> {
	}

	static DisplayBinder uiBinder = GWT.create(DisplayBinder.class);
	@UiField
	FlowPanel mapPanelContainer;
	@UiField
	FlowPanel dataDisplayPanelContainer;
	@UiField
	VerticalPanel controlPanelContainer;

	public MainView() {
		initWidget(uiBinder.createAndBindUi(this));
		MapPanel mapPanel = new MapPanel();
		MapControlPanel controlPanel = new MapControlPanel(mapPanel);
		mapPanelContainer.add(mapPanel);
		mapPanel.setControlPanel(controlPanel);
		controlPanelContainer.add(controlPanel);
		mapPanel.setDataDisplayPanel(dataDisplayPanelContainer);
		mapPanel.showMarkers();
	}
	
	public Widget asWidget() {
		return this;
	}

}
