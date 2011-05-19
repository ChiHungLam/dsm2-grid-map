package gov.ca.bdo.modeling.dsm2.map.client.presenter;

import gov.ca.bdo.modeling.dsm2.map.client.Presenter;
import gov.ca.bdo.modeling.dsm2.map.client.event.DSM2StudyEvent;
import gov.ca.bdo.modeling.dsm2.map.client.service.DSM2InputServiceAsync;
import gov.ca.modeling.dsm2.widgets.client.events.MessageEvent;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;

public class DSM2StudyManagerPresenter implements Presenter {

	public interface Display {
		public Widget asWidget();

		public void clearTable();

		public ArrayList<String> getSelectedStudies();

		public void addRowForStudy(String studyName);

		public HasClickHandlers getDeleteButton();

		public void removeStudy(String study);

		public HasClickHandlers getShareButton();

		public void addShareUrl(String study, String url);

		// upload study
		public HasChangeHandlers getEchoFile();

		public HasChangeHandlers getGisFile();

		public HasText getStudyName();

		public HasClickHandlers getUploadButton();

		public void submitForm();

		public void addSubmitCompleteHandler(
				FormPanel.SubmitCompleteHandler handler);

		// upload study data
		public HasChangeHandlers getDataFile();

		public HasText getStudyDataName();

		public HasClickHandlers getUploadDataButton();

		public void submitDataForm();

		public void addSubmitDataCompleteHandler(
				FormPanel.SubmitCompleteHandler handler);

		public void showStudySharingOptions();

		public String getSharingType();

	}

	private DSM2InputServiceAsync dsm2InputService;
	private SimpleEventBus eventBus;
	private Display display;
	private ContainerPresenter containerPresenter;
	private boolean viewOnly;

	public DSM2StudyManagerPresenter(DSM2InputServiceAsync dsm2InputService,
			SimpleEventBus eventBus2, Display display,
			ContainerPresenter containerPresenter, boolean viewOnly) {
		this.containerPresenter = containerPresenter;
		this.dsm2InputService = dsm2InputService;
		eventBus = eventBus2;
		this.display = display;
		this.viewOnly = viewOnly;
	}

	public void bind() {
		if (viewOnly) {
			display.clearTable();
			String[] split = History.getToken().split("/");
			String studyKey = split[1];
			display.addRowForStudy(studyKey);
			return;
		}
		eventBus.fireEvent(new MessageEvent("Loading studies.."));
		dsm2InputService.getStudyNames(new AsyncCallback<String[]>() {

			public void onSuccess(final String[] result) {
				display.clearTable();
				eventBus.fireEvent(new MessageEvent(""));
				for (String element : result) {
					display.addRowForStudy(element);
				}
			}

			public void onFailure(Throwable caught) {
				eventBus.fireEvent(new MessageEvent(
						"Could not retrieve the study names! "
								+ caught.getMessage(), MessageEvent.ERROR));
			}
		});

		display.getDeleteButton().addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				for (final String study : display.getSelectedStudies()) {
					dsm2InputService.removeStudy(study,
							new AsyncCallback<Void>() {

								public void onFailure(Throwable caught) {
									eventBus.fireEvent(new MessageEvent(
											"Error deleting study! "
													+ caught.getMessage(),
											MessageEvent.ERROR));
								}

								public void onSuccess(Void result) {
									display.removeStudy(study);
									eventBus.fireEvent(new DSM2StudyEvent(
											study, DSM2StudyEvent.DELETE));
								}
							});
				}
			}
		});

		display.getShareButton().addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				for (final String study : display.getSelectedStudies()) {
					dsm2InputService.generateSharingKey(study,
							new AsyncCallback<String>() {

								public void onFailure(Throwable caught) {
									eventBus
											.fireEvent(new MessageEvent(
													"Error sharing study: "
															+ study
															+ " "
															+ caught
																	.getMessage(),
													MessageEvent.ERROR));
								}

								public void onSuccess(String result) {
									display.showStudySharingOptions();
									if (display.getSharingType().equals(
											"unlisted")) {
										String url = GWT.getHostPageBaseURL()
												+ "dsm2_grid_map_view.html#map_view/"
												+ result;
										display.addShareUrl(study, url);
									}
								}
							});
				}
			}
		});
		display.addSubmitCompleteHandler(new SubmitCompleteHandler() {

			public void onSubmitComplete(SubmitCompleteEvent event) {
				History.newItem("map/" + display.getStudyName().getText(),
						false);
				containerPresenter.loadStudies();
			}
		});

		display.getUploadButton().addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				display.submitForm();
			}
		});

	}

	public void go(HasWidgets container) {
		bind();
		container.clear();
		container.add(display.asWidget());
	}

}
