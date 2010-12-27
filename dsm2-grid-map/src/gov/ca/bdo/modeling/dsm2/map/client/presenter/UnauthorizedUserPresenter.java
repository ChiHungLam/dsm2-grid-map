package gov.ca.bdo.modeling.dsm2.map.client.presenter;

import gov.ca.bdo.modeling.dsm2.map.client.Presenter;
import gov.ca.modeling.dsm2.widgets.client.events.MessageEvent;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;

public class UnauthorizedUserPresenter implements Presenter {
	public interface Display {
		public HasText getEmail();

		public HasClickHandlers getSubmitButton();

		public void addSubmitCompleteHandler(
				FormPanel.SubmitCompleteHandler handler);

		public void submitForm();

		public Widget asWidget();

	}

	private Display display;
	private SimpleEventBus eventBus;

	public UnauthorizedUserPresenter(SimpleEventBus eventBus, Display display) {
		this.eventBus = eventBus;
		this.display = display;
	}

	public void bind() {
		display.addSubmitCompleteHandler(new SubmitCompleteHandler() {

			public void onSubmitComplete(SubmitCompleteEvent event) {
				eventBus.fireEvent(new MessageEvent("Thanks! We'll get back to you soon."));
			}
		});

		display.getSubmitButton().addClickHandler(new ClickHandler() {

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
