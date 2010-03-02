package gov.ca.bdo.modeling.dsm2.map.client;

import gov.ca.bdo.modeling.dsm2.map.client.manager.StudyManagerDisplay;
import gov.ca.bdo.modeling.dsm2.map.client.manager.UploadStudyDisplay;
import gov.ca.bdo.modeling.dsm2.map.client.map.MapDisplay;
import gov.ca.bdo.modeling.dsm2.map.client.presenter.DSM2GridMapPresenter;
import gov.ca.bdo.modeling.dsm2.map.client.presenter.DSM2StudyManagerPresenter;
import gov.ca.bdo.modeling.dsm2.map.client.presenter.DSM2StudyUploadPresenter;
import gov.ca.bdo.modeling.dsm2.map.client.service.DSM2InputServiceAsync;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HasWidgets;

public class AppController implements Presenter, ValueChangeHandler<String> {
	private final HandlerManager eventBus;
	private final DSM2InputServiceAsync dsm2InputService;
	private HasWidgets container;

	public AppController(DSM2InputServiceAsync dsm2InputService,
			HandlerManager eventBus) {
		this.dsm2InputService = dsm2InputService;
		this.eventBus = eventBus;
		bind();
	}

	private void bind() {
		History.addValueChangeHandler(this);
		/*
		 * eventBus.addHandler(AddContactEvent.TYPE, new
		 * AddContactEventHandler() { public void onAddContact(AddContactEvent
		 * event) { doAddNewContact(); } });
		 */
	}

	/*
	 * private void doAddNewContact() { History.newItem("add"); }
	 */

	public void go(HasWidgets container) {
		this.container = container;

		if ("".equals(History.getToken())) {
			History.newItem("map");
		} else {
			History.fireCurrentHistoryState();
		}
	}

	public void onValueChange(ValueChangeEvent<String> event) {
		String token = event.getValue();

		if (token != null) {
			Presenter presenter = null;

			if (token.startsWith("map")) {
				presenter = createDSM2GridMapPresenter();
			} else if (token.equals("studies")) {
				presenter = new DSM2StudyManagerPresenter(dsm2InputService,
						eventBus, new StudyManagerDisplay());
			} else if (token.equals("upload_study")) {
				presenter = new DSM2StudyUploadPresenter(eventBus,
						new UploadStudyDisplay());
			} else if (token.equals("profile")) {
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
				new MapDisplay());
	}

}
