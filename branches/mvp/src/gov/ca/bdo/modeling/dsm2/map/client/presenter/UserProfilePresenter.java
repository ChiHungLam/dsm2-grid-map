package gov.ca.bdo.modeling.dsm2.map.client.presenter;

import gov.ca.bdo.modeling.dsm2.map.client.Presenter;
import gov.ca.bdo.modeling.dsm2.map.client.service.UserProfileServiceAsync;

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
	public interface Display {
		public HasText getEmail();

		public HasClickHandlers getAddButton();

		public void showMessage(String string);

		public void populateTable(List<String> result);

		public void clearTable();

		public Widget asWidget();

	}

	private Display display;
	private UserProfileServiceAsync userProfileService;

	public UserProfilePresenter(UserProfileServiceAsync userProfileService,
			HandlerManager eventBus, Display display) {
		this.userProfileService = userProfileService;
		this.display = display;
	}

	public void bind() {
		display.getAddButton().addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				String email = display.getEmail().getText();
				userProfileService.createUserProfile(email,
						new AsyncCallback<Void>() {

							public void onSuccess(Void result) {
							}

							public void onFailure(Throwable caught) {
							}
						});
			}
		});
		userProfileService.getUserProfiles(new AsyncCallback<List<String>>() {

			public void onSuccess(List<String> result) {
				display.populateTable(result);
			}

			public void onFailure(Throwable caught) {
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
