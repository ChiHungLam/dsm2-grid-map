package gov.ca.bdo.modeling.dsm2.map.client;

import gov.ca.bdo.modeling.dsm2.map.client.model.LoginInfo;
import gov.ca.bdo.modeling.dsm2.map.client.service.LoginService;
import gov.ca.bdo.modeling.dsm2.map.client.service.LoginServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

public class HeaderPanel extends Composite {
	private final LoginServiceAsync loginService;
	private gov.ca.modeling.dsm2.widgets.client.HeaderPanel headerPanel;

	public HeaderPanel() {
		loginService = GWT.create(LoginService.class);
		headerPanel = new gov.ca.modeling.dsm2.widgets.client.HeaderPanel();
		headerPanel.addToLinkPanel(new HTML("<b>DSM2 Grid Map</b>"));
		loginService.login(GWT.getHostPageBaseURL(),
				new AsyncCallback<LoginInfo>() {

					public void onSuccess(LoginInfo result) {
						if (result.isLoggedIn()) {
							HTML nameDisplay = new HTML("<b>"
									+ result.getEmailAddress() + "</b>");
							nameDisplay.setStyleName("name");
							headerPanel.addToRightSide(nameDisplay);
							headerPanel.addToRightSide(new Anchor("Logout",
									result.getLogoutUrl()));
						} else {
							headerPanel.addToRightSide(new Anchor("Login",
									result.getLoginUrl()));
						}
					}

					public void onFailure(Throwable caught) {
						headerPanel.showError(true,
								"Oops... an error occurred: Please try again");
					}
				});
		initWidget(headerPanel);
	}

	public void showMessage(boolean show, String message) {
		headerPanel.showWarning(show, message);
	}

	public void showError(boolean show, String message) {
		headerPanel.showError(show, message);
	}
}
