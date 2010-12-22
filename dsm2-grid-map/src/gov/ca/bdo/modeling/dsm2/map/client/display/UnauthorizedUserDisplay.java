package gov.ca.bdo.modeling.dsm2.map.client.display;

import gov.ca.bdo.modeling.dsm2.map.client.presenter.UnauthorizedUserPresenter.Display;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;

public class UnauthorizedUserDisplay extends Composite implements Display {

	private TextBox emailField;
	private Button submitButton;
	private DockLayoutPanel mainPanel;
	private FormPanel formPanel;

	public UnauthorizedUserDisplay() {

		emailField = new TextBox();
		emailField.setName("email");
		emailField.setMaxLength(30);
		submitButton = new Button("Submit");

		Label title = new Label("Access Request");
		title.setStylePrimaryName("bordered-title");

		formPanel = new FormPanel();
		formPanel.setStyleName("bordered-content");
		formPanel.setAction("/dsm2_grid_map/request_access");
		formPanel.setMethod("post");
		FlowPanel elementsPanel = new FlowPanel();
		formPanel.add(elementsPanel);
		String email = "";//FIXME:?
		elementsPanel.add(new HTML("<p>Your user id " + email
				+ " is not authorized to access this application!</p>"));
		elementsPanel
				.add(new HTML(
						"<p>Enter an email address that has Google authentication or use the one below:<br>"));
		elementsPanel.add(emailField);
		emailField.setText(email);
		elementsPanel
				.add(new HTML(
						"<p>If you want to know how to enable your account for Google authentication go <a href='https://www.google.com/accounts/NewAccount'>here</a></p>"));
		FlowPanel buttonPanel = new FlowPanel();
		buttonPanel.add(submitButton);
		elementsPanel.add(buttonPanel);

		FlowPanel containerPanel = new FlowPanel();
		containerPanel.add(title);
		containerPanel.add(formPanel);

		mainPanel = new DockLayoutPanel(Unit.EM);
		mainPanel.addWest(new FlowPanel(), 5);
		mainPanel.addEast(new FlowPanel(), 5);
		mainPanel.addSouth(new HTML(""), 1);
		mainPanel.add(containerPanel);
		initWidget(mainPanel);
	}

	public HasText getEmail() {
		return emailField;
	}

	public HasClickHandlers getSubmitButton() {
		return submitButton;
	}

	public void submitForm() {
		formPanel.submit();
	}

	public Widget asWidget() {
		return this;
	}

	public void addSubmitCompleteHandler(SubmitCompleteHandler handler) {
		formPanel.addSubmitCompleteHandler(handler);
	}
}
