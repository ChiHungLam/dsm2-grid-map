package gov.ca.bdo.modeling.dsm2.map.client;

import gov.ca.bdo.modeling.dsm2.map.client.model.LoginInfo;
import gov.ca.bdo.modeling.dsm2.map.client.service.LoginService;
import gov.ca.bdo.modeling.dsm2.map.client.service.LoginServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;

public class HeaderPanel extends Composite {
	private final LoginServiceAsync loginService;

	public HeaderPanel() {
		loginService = GWT.create(LoginService.class);
		final DockLayoutPanel panel = new DockLayoutPanel(Unit.EM);
		panel.setStyleName("headerPanel");
		panel.addWest(new HTML("<h3>DSM2 Grid Map</h3>"), 40);
		loginService.login(GWT.getHostPageBaseURL(),
				new AsyncCallback<LoginInfo>() {

					public void onSuccess(LoginInfo result) {
						if (result.isLoggedIn()) {
							panel.add(new Anchor("Signed in as "
									+ result.getEmailAddress()));
						} else {
							panel
									.add(new Anchor("Login", result
											.getLoginUrl()));
						}
					}

					public void onFailure(Throwable caught) {
					}
				});
		initWidget(panel);
	}
}
