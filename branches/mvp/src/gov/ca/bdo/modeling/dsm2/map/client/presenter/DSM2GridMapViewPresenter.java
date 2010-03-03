package gov.ca.bdo.modeling.dsm2.map.client.presenter;

import gov.ca.bdo.modeling.dsm2.map.client.Presenter;
import gov.ca.bdo.modeling.dsm2.map.client.service.DSM2InputServiceAsync;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class DSM2GridMapViewPresenter implements Presenter {
	public interface Display {
		public Widget asWidget();

		public void setStudy(String studyName);

		public void showErrorMessage(String string);
	}

	private DSM2InputServiceAsync dsm2InputService;
	private HandlerManager eventBus;
	private Display display;

	public DSM2GridMapViewPresenter(DSM2InputServiceAsync dsm2InputService,
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
		if (token.startsWith("map_view")) {
			if (token.indexOf("/") >= 0) {
				final String studyKey = token.substring(token.indexOf("/"));
				dsm2InputService.getStudyNameForSharingKey(studyKey,
						new AsyncCallback<String>() {

							public void onSuccess(String studyName) {
								display.setStudy(studyName);
							}

							public void onFailure(Throwable caught) {
								display.showErrorMessage("Error "
										+ caught.getMessage() + " for key "
										+ studyKey);
							}
						});
			}
		}
	}

}
