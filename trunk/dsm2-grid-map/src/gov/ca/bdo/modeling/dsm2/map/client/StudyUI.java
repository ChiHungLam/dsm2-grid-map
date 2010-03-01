package gov.ca.bdo.modeling.dsm2.map.client;

import gov.ca.bdo.modeling.dsm2.map.client.service.DSM2InputService;
import gov.ca.bdo.modeling.dsm2.map.client.service.DSM2InputServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootLayoutPanel;

public class StudyUI {

	private DSM2InputServiceAsync dsm2Service;
	private DockLayoutPanel mainPanel;
	private HeaderPanel headerPanel;

	public StudyUI() {
		dsm2Service = GWT.create(DSM2InputService.class);
	}

	public void bind() {
		mainPanel = new DockLayoutPanel(Unit.EM);
		headerPanel = new HeaderPanel();
		headerPanel.showMessage(true, "Loading...");
		mainPanel.addNorth(headerPanel, 2);
		mainPanel.addSouth(new HTML(""), 1);
		mainPanel.add(new StudyManagerPanel(dsm2Service));
		RootLayoutPanel.get().clear();
		RootLayoutPanel.get().add(mainPanel);
	}
}
