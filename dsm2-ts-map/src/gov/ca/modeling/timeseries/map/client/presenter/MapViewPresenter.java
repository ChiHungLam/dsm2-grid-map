package gov.ca.modeling.timeseries.map.client.presenter;

import gov.ca.modeling.timeseries.map.client.Presenter;
import gov.ca.modeling.timeseries.map.client.display.Display;
import gov.ca.modeling.timeseries.map.client.event.HeaderMessageEvent;
import gov.ca.modeling.timeseries.map.shared.service.DataServiceAsync;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;

public class MapViewPresenter implements Presenter {

	public static interface MapViewDisplay extends Display {
	}

	private DataServiceAsync dataService;
	private HandlerManager eventBus;
	private MapViewDisplay display;

	public MapViewPresenter(DataServiceAsync dataService,
			HandlerManager eventBus, MapViewDisplay display) {
		this.dataService = dataService;
		this.eventBus = eventBus;
		this.display = display;
	}

	public void go(HasWidgets container) {
		container.clear();
		bind();
		container.add(display.asWidget());
		eventBus.fireEvent(new HeaderMessageEvent(""));
	}

	private void bind() {
		// TODO Auto-generated method stub

	}

}
