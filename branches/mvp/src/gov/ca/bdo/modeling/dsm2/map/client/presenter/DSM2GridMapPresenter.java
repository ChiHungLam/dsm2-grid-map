package gov.ca.bdo.modeling.dsm2.map.client.presenter;

import gov.ca.bdo.modeling.dsm2.map.client.Presenter;
import gov.ca.bdo.modeling.dsm2.map.client.service.DSM2InputServiceAsync;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class DSM2GridMapPresenter implements Presenter {
	public interface Display {
		public Widget asWidget();

		public void setStudy(String studyName);
	}

	private DSM2InputServiceAsync dsm2InputService;
	private HandlerManager eventBus;
	private Display display;

	public DSM2GridMapPresenter(DSM2InputServiceAsync dsm2InputService,
			HandlerManager eventBus, Display display) {
		this.dsm2InputService = dsm2InputService;
		this.eventBus = eventBus;
		this.display = display;

	}

	public void go(HasWidgets container) {
		container.clear();
		container.add(display.asWidget());
		display.asWidget().setVisible(true);
		String token = History.getToken();
		if (token.startsWith("map")) {
			if (token.indexOf("/") >= 0) {
				String studyName = token.substring(token.indexOf("/"));
				display.setStudy(studyName);
			}
		}
	}

}
