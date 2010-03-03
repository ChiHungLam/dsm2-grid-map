package gov.ca.bdo.modeling.dsm2.map.client.presenter;

import gov.ca.bdo.modeling.dsm2.map.client.Presenter;
import gov.ca.bdo.modeling.dsm2.map.client.service.DSM2InputServiceAsync;
import gov.ca.dsm2.input.model.DSM2Model;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.logical.shared.InitializeEvent;
import com.google.gwt.event.logical.shared.InitializeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class DSM2GridMapPresenter implements Presenter {
	public interface Display {
		public Widget asWidget();

		public void setStudy(String studyName);

		public void setStudies(String[] result);

		public void showError(String string);

		public void setModel(DSM2Model result);

		public void showMessage(String string);

		public void populateGrid();

		public void clearMessages();

		public DSM2Model getModel();

		public HasChangeHandlers getStudyBox();

		public String getStudyChoice();

		public String[] getStudies();

		public HandlerRegistration addInitializeHandler(
				InitializeHandler initializeHandler);

	}

	private DSM2InputServiceAsync dsm2InputService;
	private HandlerManager eventBus;
	private Display display;
	private String currentStudy;

	public DSM2GridMapPresenter(DSM2InputServiceAsync dsm2InputService,
			HandlerManager eventBus, Display display) {
		this.dsm2InputService = dsm2InputService;
		this.eventBus = eventBus;
		this.display = display;
	}

	public void go(HasWidgets container) {
		loadStudies();
		bind();
		container.clear();
		container.add(display.asWidget());
		display.asWidget().setVisible(true);
	}

	public void bind() {
		display.getStudyBox().addChangeHandler(new ChangeHandler() {

			public void onChange(ChangeEvent event) {
				display.setStudy(display.getStudyChoice());
			}
		});
		display.addInitializeHandler(new InitializeHandler() {

			public void onInitialize(InitializeEvent event) {
				setStudyFromHistory();
				loadStudy();
			}
		});
	}

	public void setStudyFromHistory() {
		String token = History.getToken();
		if (token.startsWith("map")) {
			if (token.indexOf("/") >= 0) {
				String studyName = token.substring(token.indexOf("/") + 1);
				currentStudy = studyName;
			} else {
				String[] studies = display.getStudies();
				if ((studies != null) && (studies.length > 0)) {
					currentStudy = studies[0];
				}
			}
			display.setStudy(currentStudy);
		}
	}

	public void loadStudy() {
		dsm2InputService.getInputModel(currentStudy,
				new AsyncCallback<DSM2Model>() {

					public void onSuccess(DSM2Model result) {
						display.setModel(result);
						if (result != null) {
							display.showMessage("Drawing...");
							display.populateGrid();
						}
						display.clearMessages();
					}

					public void onFailure(Throwable caught) {
						display.showError("Oops, an error occurred: "
								+ caught.getMessage() + ". Try again");
						System.err.println(caught);
					}
				});
	}

	public void loadStudies() {
		dsm2InputService.getStudyNames(new AsyncCallback<String[]>() {

			public void onSuccess(String[] result) {
				display.setStudies(result);
				setStudyFromHistory();
				loadStudy();
			}

			public void onFailure(Throwable caught) {
				display.showError("Oops and error occurred: "
						+ caught.getMessage());
				display.setStudies(new String[0]);
			}
		});
	}

	public void saveCurrentStudy() {
		dsm2InputService.saveModel(currentStudy, display.getModel(),
				new AsyncCallback<Void>() {

					public void onSuccess(Void result) {
						display.showMessage("Saved study " + currentStudy);
					}

					public void onFailure(Throwable caught) {
						display.showError("Could not save study "
								+ currentStudy);
					}
				});

	}

}
