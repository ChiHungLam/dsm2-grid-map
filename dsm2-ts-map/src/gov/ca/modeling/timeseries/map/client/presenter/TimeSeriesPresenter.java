package gov.ca.modeling.timeseries.map.client.presenter;

import gov.ca.modeling.timeseries.map.client.Presenter;
import gov.ca.modeling.timeseries.map.client.display.Display;
import gov.ca.modeling.timeseries.map.client.event.DataChangedEvent;
import gov.ca.modeling.timeseries.map.client.event.DataChangedEventHandler;
import gov.ca.modeling.timeseries.map.shared.data.TimeSeriesReferenceData;
import gov.ca.modeling.timeseries.map.shared.service.DataService;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.visualization.client.visualizations.Table;

public class TimeSeriesPresenter implements Presenter {
	public static interface TimeSeriesDisplay extends Display {
		public Table getDataTable();

		public HasText getInfoLabel();
	}

	private DataService dataService;
	private HandlerManager eventBus;
	private TimeSeriesDisplay display;

	public TimeSeriesPresenter(DataService dataService,
			HandlerManager eventBus, TimeSeriesDisplay display) {
		this.dataService = dataService;
		this.eventBus = eventBus;
		this.display = display;
	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		container.add(display.asWidget());
	}

	public void bind() {
		eventBus.addHandler(DataChangedEvent.TYPE,
				new DataChangedEventHandler() {

					@Override
					public void onDataChange(DataChangedEvent event) {
						TimeSeriesReferenceData reference = event
								.getReference();
					}
				});
	}
}
