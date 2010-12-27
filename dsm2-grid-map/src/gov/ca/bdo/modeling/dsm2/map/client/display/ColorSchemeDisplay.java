package gov.ca.bdo.modeling.dsm2.map.client.display;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class ColorSchemeDisplay extends MapDisplay {

	private FlowPanel controlPanel;
	private FlowPanel infoPanel;

	public ColorSchemeDisplay(ContainerDisplay display, EventBus eventBus) {
		super(display, true, eventBus);
		infoPanel = new FlowPanel();
	}

	@Override
	protected void initializeUI() {
		super.initializeUI();
		// layout top level things here
		mapPanel.setInfoPanel(infoPanel);
	}

	@Override
	public Widget getSidePanel() {
		if (controlPanel == null) {
			controlPanel = new FlowPanel();
		}
		return controlPanel;
	}

}
