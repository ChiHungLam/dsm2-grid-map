package gov.ca.modeling.timeseries.map.client.presenter;

import gov.ca.modeling.timeseries.map.client.Presenter;
import gov.ca.modeling.timeseries.map.client.event.HeaderMessageEvent;
import gov.ca.modeling.timeseries.map.shared.service.UserProfileServiceAsync;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class UserProfilePresenter implements Presenter {
	public interface UserProfileDisplay {
		public HasText getEmail();

		public HasClickHandlers getAddButton();

		public void populateTable(List<String> result);

		public void clearTable();

		public Widget asWidget();

	}

	private UserProfileDisplay display;
	private UserProfileServiceAsync userProfileService;
	private HandlerManager eventBus;

	public UserProfilePresenter(UserProfileServiceAsync userProfileService,
			HandlerManager eventBus, UserProfileDisplay display) {
		this.userProfileService = userProfileService;
		this.eventBus = eventBus;
		this.display = display;
	}

	public void bind() {
		display.getAddButton().addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				String email = display.getEmail().getText();
				userProfileService.createUserProfile(email,
						new AsyncCallback<Void>() {

							public void onSuccess(Void result) {
								eventBus.fireEvent(new HeaderMessageEvent(""));
							}

							public void onFailure(Throwable caught) {
								eventBus.fireEvent(new HeaderMessageEvent(
										caught.getMessage()));
							}
						});
			}
		});
		userProfileService.getUserProfiles(new AsyncCallback<List<String>>() {

			public void onSuccess(List<String> result) {
				display.populateTable(result);
				eventBus.fireEvent(new HeaderMessageEvent(""));
			}

			public void onFailure(Throwable caught) {
				eventBus.fireEvent(new HeaderMessageEvent(
						"Error loading user profiles"));
				display.clearTable();
			}
		});
	}

	public void go(HasWidgets container) {
		bind();
		container.clear();
		container.add(display.asWidget());
	}

}
