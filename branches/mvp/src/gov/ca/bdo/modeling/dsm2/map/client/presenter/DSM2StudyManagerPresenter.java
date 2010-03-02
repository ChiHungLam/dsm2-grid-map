package gov.ca.bdo.modeling.dsm2.map.client.presenter;

import gov.ca.bdo.modeling.dsm2.map.client.Presenter;
import gov.ca.bdo.modeling.dsm2.map.client.service.DSM2InputServiceAsync;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class DSM2StudyManagerPresenter implements Presenter {

	public interface Display {
		public Widget asWidget();

		public void clearTable();

		public void addRowForStudy(String string);

		public void showErrorMessage(String string);
	}

	private DSM2InputServiceAsync dsm2InputService;
	private HandlerManager eventBus;
	private Display display;

	public DSM2StudyManagerPresenter(DSM2InputServiceAsync dsm2InputService,
			HandlerManager eventBus, Display display) {
		this.dsm2InputService = dsm2InputService;
		this.eventBus = eventBus;
		this.display = display;
	}

	public void bind() {
		dsm2InputService.getStudyNames(new AsyncCallback<String[]>() {

			public void onSuccess(final String[] result) {
				display.clearTable();
				for (String element : result) {
					display.addRowForStudy(element);
				}
			}

			public void onFailure(Throwable caught) {
				display
						.showErrorMessage("Oops! An error occurred. Please try again");
			}
		});

	}

	public void go(HasWidgets container) {
		bind();
		container.clear();
		container.add(display.asWidget());
	}

}
