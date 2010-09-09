package gov.ca.bdo.modeling.dsm2.map.client.presenter;

import gov.ca.bdo.modeling.dsm2.map.client.Presenter;
import gov.ca.bdo.modeling.dsm2.map.client.service.DSM2InputServiceAsync;
import gov.ca.dsm2.input.model.DSM2Model;

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
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.ToggleButton;
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

		public boolean isInEditMode();

		public void setEditMode(boolean editMode);

		public HasClickHandlers getSaveEditButton();

		public void updateLinks();

		public HasClickHandlers getTextAnnotationButton();

		public void turnOnTextAnnotation();

		public HasClickHandlers getAddPolylineButton();

		public void stopMeasuringDistanceAlongLine();

		public void startMeasuringDistanceAlongLine();

		public void stopMeasuringAreaInPolygon();

		public void startMeasuringAreaInPolygon();

		public HasClickHandlers getAddPolygonButton();

		public void turnOffTextAnnotation();

		public HasClickHandlers getAddKmlButton();

		public HasText getKmlUrlBox();

		public void addKmlOverlay(String url);

	}

	private DSM2InputServiceAsync dsm2InputService;
	private HandlerManager eventBus;
	private Display display;
	private String currentStudy;
	private boolean viewOnly;

	public DSM2GridMapPresenter(DSM2InputServiceAsync dsm2InputService,
			HandlerManager eventBus, Display display, boolean viewOnly) {
		this.dsm2InputService = dsm2InputService;
		this.eventBus = eventBus;
		this.display = display;
		this.viewOnly = viewOnly;
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
				currentStudy = display.getStudyChoice();
				if (!viewOnly) {
					loadStudy();
				}
			}
		});
		display.addInitializeHandler(new InitializeHandler() {

			public void onInitialize(InitializeEvent event) {
				if (viewOnly) {
					loadViewOnlyStudy();
					display.updateLinks();
				} else {
					setStudyFromHistory();
					loadStudy();
				}
			}
		});
		display.getSaveEditButton().addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				Object source = event.getSource();
				if (source instanceof ToggleButton) {
					ToggleButton button = (ToggleButton) source;
					display.setEditMode(button.isDown());
					if (!button.isDown()) {
						saveCurrentStudy();
					}
				}
			}
		});

		display.getTextAnnotationButton().addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				Object source = event.getSource();
				if (source instanceof ToggleButton) {
					ToggleButton button = (ToggleButton) source;
					if (button.isDown()) {
						display.turnOnTextAnnotation();
					} else {
						display.turnOffTextAnnotation();
					}
				}
			}
		});

		display.getAddPolylineButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				Object source = event.getSource();
				if (source instanceof ToggleButton) {
					ToggleButton button = (ToggleButton) source;
					if (button.isDown()) {
						display.startMeasuringDistanceAlongLine();
					} else {
						display.stopMeasuringDistanceAlongLine();
					}
				}
			}
		});
		display.getAddPolygonButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				Object source = event.getSource();
				if (source instanceof ToggleButton) {
					ToggleButton button = (ToggleButton) source;
					if (button.isDown()) {
						display.startMeasuringAreaInPolygon();
					} else {
						display.stopMeasuringAreaInPolygon();
					}
				}
			}
		});
		display.getAddKmlButton().addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				String url = display.getKmlUrlBox().getText();
				display.addKmlOverlay(url);
			}
		});
	}

	public void setStudyFromHistory() {
		if (viewOnly) {
			//
		} else {
			String token = History.getToken();
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
				display.setStudy(currentStudy);
			}
		}
	}

	public void loadStudy() {
		if (currentStudy == null) {
			return;
		}
		display.showMessage("Loading study " + currentStudy + "...");
		dsm2InputService.getInputModel(currentStudy,
				new AsyncCallback<DSM2Model>() {

					public void onSuccess(DSM2Model result) {
						History.newItem(URL.encode("map/" + currentStudy),
								false);
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
		if (viewOnly) {
		} else {
			dsm2InputService.getStudyNames(new AsyncCallback<String[]>() {

				public void onSuccess(String[] result) {
					display.setStudies(result);
					setStudyFromHistory();
					loadStudy();
				}

				public void onFailure(Throwable caught) {
					display.showError("Oops and error occurred: "
							+ caught.getMessage());
					display.setStudies(new String[] { "" });
					// History.newItem("request_access");
				}
			});
		}
	}

	public void saveCurrentStudy() {
		dsm2InputService.saveModel(currentStudy, display.getModel(),
				new AsyncCallback<Void>() {

					public void onSuccess(Void result) {
						display.showMessage("Saved study " + currentStudy);
						Timer timer = new Timer() {

							@Override
							public void run() {
								display.clearMessages();
							}
						};
						timer.schedule(5000);
					}

					public void onFailure(Throwable caught) {
						display.showError("Could not save study "
								+ currentStudy);
					}
				});

	}

	public void loadViewOnlyStudy() {
		String token = History.getToken();
		if (token.startsWith("map_view")) {
			if (token.indexOf("/") >= 0) {
				final String studyKey = token.substring(token.indexOf("/") + 1);
				dsm2InputService.getStudyNameForSharingKey(studyKey,
						new AsyncCallback<String>() {

							public void onFailure(Throwable caught) {
							}

							public void onSuccess(String result) {
								display.setStudies(new String[] { result });
							}
						});
				dsm2InputService.getInputModelForKey(studyKey,
						new AsyncCallback<DSM2Model>() {

							public void onFailure(Throwable caught) {
								display.showError("Error "
										+ caught.getMessage() + " for key "
										+ studyKey);
							}

							public void onSuccess(DSM2Model result) {
								display.setModel(result);
								if (result != null) {
									display.showMessage("Drawing...");
									display.populateGrid();
								}
								display.clearMessages();

							}
						});
			}
		}

	}

}
