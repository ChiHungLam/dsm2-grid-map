/**
 *   Copyright (C) 2009, 2010 
 *    Nicky Sandhu
 *    State of California,
 *    Department of Water Resources.
 *    This file is part of DSM2 Grid Map
 *    The DSM2 Grid Map is free software: 
 *    you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *    DSM2 Grid Map is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.

 *    You should have received a copy of the GNU General Public License
 *    along with DSM2 Grid Map.  If not, see <http://www.gnu.org/licenses>.
 */
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
	private final gov.ca.modeling.dsm2.widgets.client.HeaderPanel headerPanel;

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
