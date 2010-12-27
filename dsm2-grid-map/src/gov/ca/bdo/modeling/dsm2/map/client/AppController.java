package gov.ca.bdo.modeling.dsm2.map.client;

import gov.ca.bdo.modeling.dsm2.map.client.display.ContainerDisplay;
import gov.ca.bdo.modeling.dsm2.map.client.display.DSM2GridMapDisplay;
import gov.ca.bdo.modeling.dsm2.map.client.display.StudyManagerDisplay;
import gov.ca.bdo.modeling.dsm2.map.client.display.UnauthorizedUserDisplay;
import gov.ca.bdo.modeling.dsm2.map.client.display.UploadStudyDataDisplay;
import gov.ca.bdo.modeling.dsm2.map.client.display.UploadStudyDisplay;
import gov.ca.bdo.modeling.dsm2.map.client.display.UserProfileDisplay;
import gov.ca.bdo.modeling.dsm2.map.client.display.XSectionEditorDisplay;
import gov.ca.bdo.modeling.dsm2.map.client.presenter.ContainerPresenter;
import gov.ca.bdo.modeling.dsm2.map.client.presenter.DSM2GridMapPresenter;
import gov.ca.bdo.modeling.dsm2.map.client.presenter.DSM2StudyDataUploadPresenter;
import gov.ca.bdo.modeling.dsm2.map.client.presenter.DSM2StudyManagerPresenter;
import gov.ca.bdo.modeling.dsm2.map.client.presenter.DSM2StudyUploadPresenter;
import gov.ca.bdo.modeling.dsm2.map.client.presenter.UnauthorizedUserPresenter;
import gov.ca.bdo.modeling.dsm2.map.client.presenter.UserProfilePresenter;
import gov.ca.bdo.modeling.dsm2.map.client.presenter.XSectionEditorPresenter;
import gov.ca.bdo.modeling.dsm2.map.client.service.DSM2InputServiceAsync;
import gov.ca.bdo.modeling.dsm2.map.client.service.LoginServiceAsync;
import gov.ca.bdo.modeling.dsm2.map.client.service.UserProfileServiceAsync;
import gov.ca.modeling.maps.elevation.client.service.BathymetryDataServiceAsync;
import gov.ca.modeling.maps.elevation.client.service.DEMDataServiceAsync;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HasWidgets;

public class AppController implements Presenter, ValueChangeHandler<String> {
	private final SimpleEventBus eventBus;
	private final DSM2InputServiceAsync dsm2InputService;
	private HasWidgets container;
	private UserProfileServiceAsync userProfileService;
	private BathymetryDataServiceAsync bathymetryService;
	private DEMDataServiceAsync demService;
	private DSM2GridMapPresenter mapPresenter;
	private ContainerPresenter containerPresenter;
	private ContainerDisplay containerDisplay;
	private LoginServiceAsync loginService;

	public AppController(LoginServiceAsync loginService,
			DSM2InputServiceAsync dsm2InputService,
			UserProfileServiceAsync userProfileService,
			BathymetryDataServiceAsync bathymetryService,
			DEMDataServiceAsync demService, SimpleEventBus eventBus) {
		this.loginService = loginService;
		this.dsm2InputService = dsm2InputService;
		this.userProfileService = userProfileService;
		this.bathymetryService = bathymetryService;
		this.demService = demService;
		this.eventBus = eventBus;
		bind();
	}

	private void bind() {
		History.addValueChangeHandler(this);
	}

	public void go(HasWidgets rootPanelAsContainer) {
		containerPresenter = new ContainerPresenter(loginService,
				dsm2InputService, eventBus,
				containerDisplay = new ContainerDisplay());
		container = containerDisplay.asHasWidgets();

		containerPresenter.go(rootPanelAsContainer);
		containerDisplay.setLinkBarActive(History.getToken());

		// fire history event
		if ("".equals(History.getToken())) {
			History.newItem("map");
		} else {
			History.fireCurrentHistoryState();
		}

	}

	public void onValueChange(ValueChangeEvent<String> event) {
		String token = event.getValue();
		containerDisplay.setLinkBarActive(token);
		if (token != null) {
			Presenter presenter = null;
			if (token.startsWith("map_view")) {
				presenter = new DSM2GridMapPresenter(dsm2InputService,
						eventBus,
						new DSM2GridMapDisplay(containerDisplay, true, eventBus),
						containerPresenter);
			} else if (token.startsWith("map")) {
				if (mapPresenter == null) {
					mapPresenter = createDSM2GridMapPresenter();
				}
				presenter = mapPresenter;
			} else if (token.equals("studies")) {
				presenter = new DSM2StudyManagerPresenter(dsm2InputService,
						eventBus, new StudyManagerDisplay(containerDisplay),
						containerPresenter);
			} else if (token.equals("xsection")) {
				presenter = new XSectionEditorPresenter(dsm2InputService,
						bathymetryService, demService, eventBus,
						new XSectionEditorDisplay());
			} else if (token.equals("upload_study")) {
				presenter = new DSM2StudyUploadPresenter(eventBus,
						new UploadStudyDisplay());
			} else if (token.equals("upload_data")) {
				presenter = new DSM2StudyDataUploadPresenter(eventBus,
						new UploadStudyDataDisplay());
			} else if (token.equals("admin/profile")) {
				presenter = new UserProfilePresenter(userProfileService,
						eventBus, new UserProfileDisplay());
			} else if (token.equals("request_access")) {
				presenter = new UnauthorizedUserPresenter(eventBus,
						new UnauthorizedUserDisplay());
			} else {
				presenter = createDSM2GridMapPresenter();
			}

			if (presenter != null) {
				presenter.go(container);
			}
		}
	}

	private DSM2GridMapPresenter createDSM2GridMapPresenter() {
		return new DSM2GridMapPresenter(dsm2InputService, eventBus,
				new DSM2GridMapDisplay(containerDisplay, false, eventBus),
				containerPresenter);
	}

}
