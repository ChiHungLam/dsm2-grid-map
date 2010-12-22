package gov.ca.bdo.modeling.dsm2.map.client.display;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ColorSchemeDisplay extends MapDisplay {

	private FlowPanel controlPanel;
	private FlowPanel infoPanel;

	public ColorSchemeDisplay(ContainerDisplay display) {
		super(display, true, new FlowPanel());
		controlPanel = new FlowPanel();
		infoPanel = new FlowPanel();
	}

	@Override
	protected void initializeUI() {
		super.initializeUI();
		// layout top level things here
		VerticalPanel sidePanel = (VerticalPanel) super.getSidePanel();
		sidePanel.add(new ScrollPanel(controlPanel));
		mapPanel.setInfoPanel(infoPanel);
	}

}
