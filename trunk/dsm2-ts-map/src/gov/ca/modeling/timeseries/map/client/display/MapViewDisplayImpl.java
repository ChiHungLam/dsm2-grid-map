package gov.ca.modeling.timeseries.map.client.display;

import gov.ca.modeling.timeseries.map.client.presenter.MapViewPresenter.MapViewDisplay;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class MapViewDisplayImpl extends Composite implements MapViewDisplay {

	public MapViewDisplayImpl() {
		FlowPanel panel = new FlowPanel();
		panel.add(new HTML("<b> Map goes here </b>"));
		initWidget(panel);
	}

	@Override
	public Widget asWidget() {
		return this;
	}

}
