package gov.ca.modeling.dsm2.widgets.client;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A div panel containing a <nobr> element that contains the widgets
 * 
 * @author nsandhu
 * 
 */
public class NoBrPanel extends ComplexPanel {

	public NoBrPanel() {
		setElement(DOM.createElement("nobr"));
	}

	public void add(Widget widget) {
		add(widget, getElement());
	}
}
