package gov.ca.modeling.calsim.report.client.display;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class CompareReportDisplay extends Composite {
	private VerticalPanel panel;

	public CompareReportDisplay() {
		panel = new VerticalPanel();
		initWidget(panel);
	}

	public void add(Widget w) {
		panel.add(w);
	}

	public void clear() {
		panel.clear();
	}

}
