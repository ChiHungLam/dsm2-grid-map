package gov.ca.bdo.modeling.dsm2.map.client.presenter;

import gov.ca.bdo.modeling.dsm2.map.client.Presenter;
import gov.ca.bdo.modeling.dsm2.map.client.service.DSM2InputServiceAsync;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class DSM2StudyManagerPresenter implements Presenter {

	public interface Display {
		public Widget asWidget();

		public void clearTable();

		public void addRowForStudy(String studyName);

		public void showErrorMessage(String message);

		public void showMessage(String message);

		public HasClickHandlers getDeleteButton();

		public ArrayList<String> getSelectedStudies();

		public void removeStudy(String study);

		public void clearMessages();

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
		display.showMessage("Loading...");
		dsm2InputService.getStudyNames(new AsyncCallback<String[]>() {

			public void onSuccess(final String[] result) {
				display.clearTable();
				display.clearMessages();
				for (String element : result) {
					display.addRowForStudy(element);
				}
			}

			public void onFailure(Throwable caught) {
				display
						.showErrorMessage("Oops! An error occurred. Please try again");
			}
		});

		display.getDeleteButton().addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				for (final String study : display.getSelectedStudies()) {
					dsm2InputService.removeStudy(study,
							new AsyncCallback<Void>() {

								public void onFailure(Throwable caught) {
									display.showErrorMessage("Error "
											+ caught.getMessage()
											+ " occurred deleting study "
											+ study);
								}

								public void onSuccess(Void result) {
									display.removeStudy(study);
								}
							});
				}
			}
		});

	}

	public void go(HasWidgets container) {
		bind();
		container.clear();
		container.add(display.asWidget());
	}

}
