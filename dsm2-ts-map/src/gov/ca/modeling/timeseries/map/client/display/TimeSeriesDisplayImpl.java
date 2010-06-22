package gov.ca.modeling.timeseries.map.client.display;

import gov.ca.modeling.timeseries.map.client.presenter.TimeSeriesPresenter.TimeSeriesDisplay;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.visualizations.Table;

public class TimeSeriesDisplayImpl extends Composite implements
		TimeSeriesDisplay {

	public Table table;
	public Label infoLabel;

	public TimeSeriesDisplayImpl() {
		FlowPanel panel = new FlowPanel();
		panel.add(infoLabel = new Label());
		panel.add(table = new Table());
		initWidget(panel);
	}

	public Table getDataTable() {
		return table;
	}

	public HasText getInfoLabel() {
		return infoLabel;
	}

	@Override
	public Widget asWidget() {
		return this;
	}

}
