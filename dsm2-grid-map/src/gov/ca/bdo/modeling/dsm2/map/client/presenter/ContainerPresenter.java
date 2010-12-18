package gov.ca.bdo.modeling.dsm2.map.client.presenter;

import gov.ca.bdo.modeling.dsm2.map.client.Presenter;
import gov.ca.bdo.modeling.dsm2.map.client.event.DSM2StudyEvent;
import gov.ca.bdo.modeling.dsm2.map.client.event.MessageEvent;
import gov.ca.bdo.modeling.dsm2.map.client.event.MessageEventHandler;
import gov.ca.bdo.modeling.dsm2.map.client.model.LoginInfo;
import gov.ca.bdo.modeling.dsm2.map.client.service.DSM2InputServiceAsync;
import gov.ca.bdo.modeling.dsm2.map.client.service.LoginServiceAsync;
import gov.ca.dsm2.input.model.DSM2Model;

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
	}

	private LoginServiceAsync loginService;
	private Display display;
	private SimpleEventBus eventBus;
	private DSM2InputServiceAsync dsm2InputService;

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
	}

	public void bind() {
		eventBus.addHandler(MessageEvent.TYPE, new MessageEventHandler() {

			public void onMessage(MessageEvent event) {
				display.showMessage(event.getMessage(), event.getType(), event
						.getDelayInMillis());
			}
		});

		if (!display.isLoggedIn()) {
			loginService.login(GWT.getHostPageBaseURL(),
					new AsyncCallback<LoginInfo>() {

						public void onSuccess(LoginInfo result) {
							if (!result.isLoggedIn()){
								Location.replace("/welcome.jsp");
								return;
							}
							display.setLoginInfo(result);
							eventBus.fireEvent(new MessageEvent(
									"You are now logged in as: "
											+ result.getEmailAddress(),
									MessageEvent.WARNING, 2000));
							// bind study change events
							display.onStudyChange().addChangeHandler(new ChangeHandler() {
								
								public void onChange(ChangeEvent event) {
									String currentStudy = display.getCurrentStudy();
									loadStudy(currentStudy);
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
		}
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
			} else {
				String[] studies = display.getStudies();
				if ((studies != null) && (studies.length > 0)) {
					currentStudy = studies[0];
				}
			}
			loadStudy(currentStudy);
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

	protected void loadStudy(final String study) {
		if (study == null) {
			return;
		}
		eventBus.fireEvent(new MessageEvent("Loading study " + study + "...",
				MessageEvent.WARNING, 2000));
		dsm2InputService.getInputModel(study, new AsyncCallback<DSM2Model>() {

			public void onSuccess(DSM2Model result) {
				setStudyToHistory(study);
				display.setCurrentStudy(study);
				display.setModel(result);
				eventBus.fireEvent(new MessageEvent("Loaded study: "+study,2000));
				if (result != null) {
					eventBus.fireEvent(new DSM2StudyEvent(study, result));
				}
			}

			public void onFailure(Throwable caught) {
				eventBus.fireEvent(new MessageEvent("Oops, an error occurred: "
						+ caught.getMessage() + ". Try again",
						MessageEvent.ERROR, 5000));
				GWT.log("Error on loading study: " + study, caught);
			}
		});

	}

}