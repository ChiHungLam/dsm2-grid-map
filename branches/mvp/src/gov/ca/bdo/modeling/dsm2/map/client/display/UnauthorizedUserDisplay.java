package gov.ca.bdo.modeling.dsm2.map.client.display;

import gov.ca.bdo.modeling.dsm2.map.client.HeaderPanel;
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
	private HeaderPanel headerPanel;
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
		elementsPanel
				.add(new HTML(
						"<p>Enter your email address that has Google authentication:<br>"));
		elementsPanel.add(emailField);
		elementsPanel
				.add(new HTML(
						"<p>If you enable your account for Google authentication go <a href='https://www.google.com/accounts/NewAccount'>here</a></p>"));
		FlowPanel buttonPanel = new FlowPanel();
		buttonPanel.add(submitButton);
		elementsPanel.add(buttonPanel);

		FlowPanel containerPanel = new FlowPanel();
		containerPanel.add(title);
		containerPanel.add(formPanel);

		mainPanel = new DockLayoutPanel(Unit.EM);
		headerPanel = new HeaderPanel();
		headerPanel.clearMessages();
		mainPanel.addWest(new FlowPanel(), 5);
		mainPanel.addEast(new FlowPanel(), 5);
		mainPanel.addNorth(headerPanel, 2);
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

	public void showMessage(String message) {
		headerPanel.showMessage(true, message);
	}

}
