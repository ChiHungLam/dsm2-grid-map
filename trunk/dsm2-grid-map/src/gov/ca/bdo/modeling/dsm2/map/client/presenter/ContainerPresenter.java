package gov.ca.bdo.modeling.dsm2.map.client.presenter;

import gov.ca.bdo.modeling.dsm2.map.client.Presenter;
import gov.ca.bdo.modeling.dsm2.map.client.event.DSM2StudyEvent;
import gov.ca.bdo.modeling.dsm2.map.client.event.DSM2StudyEventHandler;
import gov.ca.bdo.modeling.dsm2.map.client.model.LoginInfo;
import gov.ca.bdo.modeling.dsm2.map.client.service.DSM2InputServiceAsync;
import gov.ca.bdo.modeling.dsm2.map.client.service.LoginServiceAsync;
import gov.ca.dsm2.input.model.DSM2Model;
import gov.ca.modeling.dsm2.widgets.client.events.MessageEvent;
import gov.ca.modeling.dsm2.widgets.client.events.MessageEventHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class ContainerPresenter implements Presenter {
	public static interface Display {
		public boolean isLoggedIn();

		public void setLoginInfo(LoginInfo result);

		public void setStudies(String[] study);

		public String[] getStudies();

		public void setCurrentStudy(String study);

		public String getCurrentStudy();

		public void setModel(DSM2Model result);

		public DSM2Model getModel();

		public HasChangeHandlers onStudyChange();

		public HasWidgets asHasWidgets();

		public void showMessage(String message, int type, int delayInMillis);

		public Widget asWidget();

		public void setViewOnly(boolean b);
	}

	private LoginServiceAsync loginService;
	private Display display;
	private SimpleEventBus eventBus;
	private DSM2InputServiceAsync dsm2InputService;
	private boolean modelLoaded;

	public ContainerPresenter(LoginServiceAsync loginService,
			DSM2InputServiceAsync dsm2InputService, SimpleEventBus eventBus,
			Display display) {
		this.loginService = loginService;
		this.dsm2InputService = dsm2InputService;
		this.display = display;
		this.eventBus = eventBus;
	}

	public void go(HasWidgets container) {
		bind();
		container.clear();
		container.add(display.asWidget());
		display.showMessage("Initializing...", MessageEvent.WARNING, 0);
		modelLoaded = false;
	}

	public void bind() {
		eventBus.addHandler(DSM2StudyEvent.TYPE, new DSM2StudyEventHandler() {

			public void onStudyNameChange(DSM2StudyEvent event) {
			}

			public void onChange(DSM2StudyEvent event) {
				if (event.getOperationType() == DSM2StudyEvent.DELETE) {
					loadStudies();
				}
			}
		});
		eventBus.addHandler(MessageEvent.TYPE, new MessageEventHandler() {

			public void onMessage(MessageEvent event) {
				display.showMessage(event.getMessage(), event.getType(), event
						.getDelayInMillis());
			}
		});

		if (!display.isLoggedIn() && !isViewOnlyPage()) {
			loginService.login(GWT.getHostPageBaseURL(),
					new AsyncCallback<LoginInfo>() {

						public void onSuccess(LoginInfo result) {
							if (!result.isLoggedIn()) {
								Location.replace("/welcome.jsp");
								return;
							}
							display.setLoginInfo(result);
							eventBus.fireEvent(new MessageEvent(
									"You are now logged in as: "
											+ result.getEmailAddress(),
									MessageEvent.WARNING, 2000));
							// bind study change events
							display.onStudyChange().addChangeHandler(
									new ChangeHandler() {

										public void onChange(ChangeEvent event) {
											String currentStudy = display
													.getCurrentStudy();
											loadStudy(currentStudy, false);
										}
									});
							// load studies and set current study.
							loadStudies();
						}

						public void onFailure(Throwable caught) {
							GWT.log(caught.getMessage());
							Location.replace(GWT.getHostPageBaseURL()
									+ "/welcome.jsp");
						}
					});
		} else {
			display.setLoginInfo(null);
			loadViewOnlyStudy();
		}
	}

	public void loadViewOnlyStudy() {
		String token = History.getToken();
		String[] split = token.split("/");
		if (split[0].equals("map_view")) {
			if (split.length > 1) {
				final String studyKey = split[1];
				dsm2InputService.getInputModelForKey(studyKey,
						new AsyncCallback<DSM2Model>() {

							public void onFailure(Throwable caught) {
								GWT.log("The study requested is not available");
								eventBus.fireEvent(new MessageEvent(
										"The study requested is not available",
										MessageEvent.ERROR, 5000));
							}

							public void onSuccess(DSM2Model result) {
								display.setCurrentStudy(studyKey);
								modelLoaded = true;
								display.setModel(result);
								eventBus.fireEvent(new MessageEvent(
										"Loaded study: " + studyKey, 2000));
								eventBus.fireEvent(new DSM2StudyEvent(studyKey,
										result));
							}
						});
			}
		}
	}

	private boolean isViewOnlyPage() {
		return History.getToken().contains("view");
	}

	public void loadStudies() {
		dsm2InputService.getStudyNames(new AsyncCallback<String[]>() {

			public void onSuccess(String[] studies) {
				display.setStudies(studies);
				setCurrentStudyFromHistory();
			}

			public void onFailure(Throwable caught) {
				eventBus.fireEvent(new MessageEvent("Could not load studies: "
						+ caught.getMessage(), MessageEvent.ERROR, 5000));
			}
		});
	}

	public void setCurrentStudyFromHistory() {
		String token = History.getToken();
		String currentStudy = null;
		if (token.startsWith("map")) {
			if ((token.indexOf("/") >= 0)) {
				String studyName = token.substring(token.indexOf("/") + 1);
				currentStudy = studyName;
				String[] studies = display.getStudies();
				if ((studies != null) && (studies.length > 0)) {
					if (studyNotIn(currentStudy, studies)) {
						currentStudy = studies[0];
					}
				}
			} else {
				String[] studies = display.getStudies();
				if ((studies != null) && (studies.length > 0)) {
					currentStudy = studies[0];
				}
			}
			loadStudy(currentStudy, false);
		}

	}

	public String getCurrentStudy() {
		return display.getCurrentStudy();
	}

	public void setCurrentStudy(String study) {
		display.setCurrentStudy(study);
	}

	public boolean isLoggedIn() {
		return display.isLoggedIn();
	}

	public String[] getStudies() {
		return display.getStudies();
	}

	public void setStudies(String[] studies) {
		display.setStudies(studies);
	}

	public DSM2Model getModel() {
		return display.getModel();
	}

	public void setModel(DSM2Model model) {
		display.setModel(model);
	}

	protected void setStudyToHistory(String study) {
		History.newItem(URL.encode("map/" + study), false);
	}

	protected void loadStudy(final String study, final boolean fromHistory) {
		if (study == null) {
			return;
		}
		if (studyNotIn(study, display.getStudies()) && !fromHistory) {
			loadStudies();
			return;
		}
		eventBus.fireEvent(new MessageEvent("Loading study " + study + "...",
				MessageEvent.WARNING, 2000));
		dsm2InputService.getInputModel(study, new AsyncCallback<DSM2Model>() {

			public void onSuccess(DSM2Model result) {
				modelLoaded = true;
				setStudyToHistory(study);
				display.setCurrentStudy(study);
				display.setModel(result);
				eventBus.fireEvent(new MessageEvent("Loaded study: " + study,
						2000));
				if ((result != null) && !fromHistory) {
					eventBus.fireEvent(new DSM2StudyEvent(study, result));
				}
			}

			public void onFailure(Throwable caught) {
				modelLoaded = false;
				eventBus.fireEvent(new MessageEvent("Oops, an error occurred: "
						+ caught.getMessage() + ". Try again",
						MessageEvent.ERROR, 5000));
				GWT.log("Error on loading study: " + study, caught);
			}
		});

	}

	private boolean studyNotIn(String study, String[] studies) {
		for (String s : studies) {
			if (study.equals(s)) {
				return false;
			}
		}
		return true;
	}

	public void fireStudyLoadedEvent() {
		loadStudy(getCurrentStudy(), false);
	}

}