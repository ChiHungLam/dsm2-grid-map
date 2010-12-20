package gov.ca.bdo.modeling.dsm2.map.client.presenter;

import gov.ca.bdo.modeling.dsm2.map.client.Presenter;
import gov.ca.bdo.modeling.dsm2.map.client.event.DSM2StudyEvent;
import gov.ca.bdo.modeling.dsm2.map.client.event.MessageEvent;
import gov.ca.bdo.modeling.dsm2.map.client.service.DSM2InputServiceAsync;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

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

	}

	private DSM2InputServiceAsync dsm2InputService;
	private SimpleEventBus eventBus;
	private Display display;
	private ContainerPresenter containerPresenter;

	public DSM2StudyManagerPresenter(DSM2InputServiceAsync dsm2InputService,
			SimpleEventBus eventBus2, Display display, ContainerPresenter containerPresenter) {
		this.containerPresenter = containerPresenter;
		this.dsm2InputService = dsm2InputService;
		this.eventBus = eventBus2;
		this.display = display;
	}

	public void bind() {
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
				eventBus.fireEvent(new MessageEvent("Could not retrieve the study names! "+caught.getMessage(), MessageEvent.ERROR));
			}
		});

		display.getDeleteButton().addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				for (final String study : display.getSelectedStudies()) {
					dsm2InputService.removeStudy(study,
							new AsyncCallback<Void>() {

								public void onFailure(Throwable caught) {
									eventBus.fireEvent(new MessageEvent("Error deleting study! "+caught.getMessage(), MessageEvent.ERROR));
								}

								public void onSuccess(Void result) {
									display.removeStudy(study);
									eventBus.fireEvent(new DSM2StudyEvent(study, DSM2StudyEvent.DELETE));
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
									eventBus.fireEvent(new MessageEvent("Error sharing study: "+study+" "+caught.getMessage(), MessageEvent.ERROR));
								}

								public void onSuccess(String result) {
									String url = GWT.getHostPageBaseURL()
											+ "#map_view/" + result;
									display.addShareUrl(study, url);
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
