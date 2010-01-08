package gov.ca.bdo.modeling.dsm2.map.client;

import gov.ca.dsm2.input.model.Gate;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Panel;

public class GateInfoPanel extends Composite {

	public GateInfoPanel(Gate gate) {
		Panel basicInfo = getBasicInfoPanel(gate);
		initWidget(basicInfo);
	}

	private Panel getBasicInfoPanel(Gate gate) {
		return new HTMLPanel("<h3>Gate " + gate.getName() + "</h3>"
				+ "<p>From " + gate.getFromObject() + " "
				+ gate.getFromIdentifier() + " to node " + gate.getToNode()
				+ "</p>");
	}
}
