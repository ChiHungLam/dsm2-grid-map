package gov.ca.bdo.modeling.dsm2.map.client.presenter;

import gov.ca.bdo.modeling.dsm2.map.client.Presenter;
import gov.ca.bdo.modeling.dsm2.map.client.service.DSM2InputServiceAsync;
import gov.ca.dsm2.input.model.DSM2Model;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.InitializeEvent;
import com.google.gwt.event.logical.shared.InitializeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * Presents the model as a set of tables or a map
 * 
 * @author nsandhu
 * 
 */
public class DSM2ModelBasePresenter implements Presenter {
	public interface Display {
		public void setCurrentStudy(String studyName);

		public String getCurrentStudy();

		public void setStudies(String[] studies);

		public String[] getStudies();

		public void showError(String string);

		public void setModel(DSM2Model model);

		public DSM2Model getModel();

		public void refresh();

		public HasChangeHandlers onStudyChange();

		public HasClickHandlers getSaveEditButton();

		public void showMessage(String string);

		public void showMessageFor(String string, int delayInMillisecs);

		public void clearMessages();

		public Widget asWidget();

		public HandlerRegistration addInitializeHandler(
				InitializeHandler initializeHandler);

	}

	protected Display display;
	protected SimpleEventBus eventBus;
	protected DSM2InputServiceAsync dsm2InputService;

	public DSM2ModelBasePresenter(DSM2InputServiceAsync dsm2InputService,
			SimpleEventBus eventBus2, Display display) {
		this.display = display;
		this.eventBus = eventBus2;
		this.dsm2InputService = dsm2InputService;
	}

	public void go(HasWidgets container) {

		bind();

		container.clear();
		container.add(display.asWidget());
		display.asWidget().setVisible(true);
	}

	protected void bind() {
		display.addInitializeHandler(new InitializeHandler() {

			public void onInitialize(InitializeEvent event) {
				loadStudies();
			}
		});

		display.onStudyChange().addChangeHandler(new ChangeHandler() {

			public void onChange(ChangeEvent event) {
				final String study = display.getCurrentStudy();
				if (study == null) {
					return;
				}
				display.setCurrentStudy(study);
				display.showMessage("Loading study " + study + "...");
				dsm2InputService.getInputModel(study,
						new AsyncCallback<DSM2Model>() {

							public void onFailure(Throwable caught) {
								display.showError("Could not load study: "
										+ study + "! Error message: "
										+ caught.getMessage());
							}

							public void onSuccess(DSM2Model model) {
								setStudyToHistory(study);
								display.setModel(model);
								display.refresh();
								display.clearMessages();
							}
						});
			}
		});

		display.getSaveEditButton().addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				Object source = event.getSource();
				if (source instanceof ToggleButton) {
					ToggleButton toggleButton = (ToggleButton) source;
					if (toggleButton.isDown()) {
						return;
					}
				}
				final String study = display.getCurrentStudy();
				final DSM2Model model = display.getModel();
				dsm2InputService.saveModel(study, model,
						new AsyncCallback<Void>() {

							public void onFailure(Throwable caught) {
								display.showError("Could not save study: "
										+ study + "! Error message: "
										+ caught.getMessage());
							}

							public void onSuccess(Void result) {
								display.showMessageFor("Saved study: " + study,
										5000);
							}
						});
			}
		});

	}

	protected void loadStudies() {
		dsm2InputService.getStudyNames(new AsyncCallback<String[]>() {

			public void onSuccess(String[] studies) {
				display.setStudies(studies);
				setStudyFromHistory();
			}

			public void onFailure(Throwable caught) {
			}
		});
	}

	protected void loadStudy(final String study) {
		if (study == null) {
			return;
		}
		display.showMessage("Loading study " + study + "...");
		dsm2InputService.getInputModel(study, new AsyncCallback<DSM2Model>() {

			public void onSuccess(DSM2Model result) {
				setStudyToHistory(study);
				display.setCurrentStudy(study);
				display.setModel(result);
				if (result != null) {
					display.showMessage("Drawing...");
					display.refresh();
				}
				display.clearMessages();
			}

			public void onFailure(Throwable caught) {
				display.showError("Oops, an error occurred: "
						+ caught.getMessage() + ". Try again");
				GWT.log("Error on loading study: " + study, caught);
			}
		});

	}

	protected void setStudyToHistory(String study) {
		History.newItem(URL.encode("map/" + study), false);
	}

	protected void setStudyFromHistory() {
		String token = History.getToken();
		String currentStudy = null;
		if (token.startsWith("map")) {
			if ((token.indexOf("/") >= 0)) {
				String studyName = token.substring(token.indexOf("/") + 1);
				currentStudy = studyName;
			} else {
				String[] studies = display.getStudies();
				if ((studies != null) && (studies.length > 0)) {
					currentStudy = studies[0];
				}
			}
			loadStudy(currentStudy);
		}

	}

}
