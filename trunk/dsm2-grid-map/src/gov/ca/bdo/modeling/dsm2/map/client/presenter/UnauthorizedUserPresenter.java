package gov.ca.bdo.modeling.dsm2.map.client.presenter;

import gov.ca.bdo.modeling.dsm2.map.client.Presenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
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

		public Widget asWidget();

		public void showMessage(String string);

		public void submitForm();
	}

	private Display display;

	public UnauthorizedUserPresenter(Display display) {
		this.display = display;
	}

	public void bind() {
		display.addSubmitCompleteHandler(new SubmitCompleteHandler() {

			public void onSubmitComplete(SubmitCompleteEvent event) {
				display.showMessage("Thanks! We'll get back to you soon.");
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