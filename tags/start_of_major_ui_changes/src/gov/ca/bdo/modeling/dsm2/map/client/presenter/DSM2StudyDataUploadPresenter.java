package gov.ca.bdo.modeling.dsm2.map.client.presenter;

import gov.ca.bdo.modeling.dsm2.map.client.Presenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;

public class DSM2StudyDataUploadPresenter implements Presenter {
	public interface Display {
		public HasChangeHandlers getDataFile();

		public HasText getStudyName();

		public HasClickHandlers getUploadButton();

		public void submitForm();

		public void addSubmitCompleteHandler(
				FormPanel.SubmitCompleteHandler handler);

		public Widget asWidget();
	}

	private SimpleEventBus eventBus;
	private Display display;

	public DSM2StudyDataUploadPresenter(SimpleEventBus eventBus2, Display display) {
		this.eventBus = eventBus2;
		this.display = display;
	}

	public void bind() {
		display.addSubmitCompleteHandler(new SubmitCompleteHandler() {

			public void onSubmitComplete(SubmitCompleteEvent event) {
				History.newItem("map/" + display.getStudyName().getText());
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
