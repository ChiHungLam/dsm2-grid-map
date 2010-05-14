package gov.ca.modeling.timeseries.map.client.presenter;

import gov.ca.modeling.timeseries.map.client.Presenter;
import gov.ca.modeling.timeseries.map.client.display.Display;
import gov.ca.modeling.timeseries.map.client.event.HeaderMessageEvent;
import gov.ca.modeling.timeseries.map.shared.data.LoginInfo;
import gov.ca.modeling.timeseries.map.shared.service.LoginServiceAsync;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

public class HeaderPresenter implements Presenter {
	public static interface HeaderDisplay extends Display {

		public void showMessage(boolean show, String message);

		public void showError(boolean show, String message);

		public void clearMessages();

		public void addLink(String linkText, String linkURL);

		public void addTextToRightSide(String text);

		public void addLinkToRightSide(String linkText, String linkURL);

	}

	private LoginServiceAsync loginService;
	private HandlerManager eventBus;
	private HeaderDisplay display;

	public HeaderPresenter(LoginServiceAsync loginService,
			HandlerManager eventBus, HeaderDisplay display) {
		this.loginService = loginService;
		this.eventBus = eventBus;
		this.display = display;
	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		bind();
		display.addLink("View", "#view");
		display.addLink("User Settings", "#user");
		container.add(display.asWidget());
	}

	public void bind() {
		loginService.login(Location.getHref(), new AsyncCallback<LoginInfo>() {

			public void onSuccess(LoginInfo result) {
				if (result.isLoggedIn()) {
					display.addTextToRightSide(result.getEmailAddress());
					display.addLinkToRightSide("Logout", result.getLogoutUrl());
				} else {
					display.addLinkToRightSide("Login", result.getLoginUrl());
					HeaderMessageEvent event = new HeaderMessageEvent(
							"You are now logged in");
					event.setDuration(5000);
					eventBus.fireEvent(event);
				}
			}

			public void onFailure(Throwable caught) {
				Location.reload();
			}
		});
	}

	public void showMessage(HeaderMessageEvent event) {
		if (event.isError()) {
			display.showError(true, event.getMessage());
		} else {
			display.showMessage(true, event.getMessage());
		}
		int millisecs = event.getDuration();
		if (millisecs > 0) {
			Timer clearTimer = new Timer() {

				@Override
				public void run() {
					display.clearMessages();
				}
			};
			clearTimer.schedule(millisecs);
		}
	}

}
