package gov.ca.bdo.modeling.dsm2.map.client;

import gov.ca.bdo.modeling.dsm2.map.client.service.UserProfileService;
import gov.ca.bdo.modeling.dsm2.map.client.service.UserProfileServiceAsync;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class UserProfilePanel extends Composite {
	private final UserProfileServiceAsync userProfileService;
	private final FlexTable table;
	private TextBox emailBox;

	public UserProfilePanel() {
		userProfileService = GWT.create(UserProfileService.class);
		table = new FlexTable();
		VerticalPanel vpanel = new VerticalPanel();
		vpanel.add(table);
		HorizontalPanel addUserPanel = new HorizontalPanel();
		vpanel.add(addUserPanel);
		addUserPanel.add(new Label("Email: "));
		addUserPanel.add(emailBox = new TextBox());
		Button addButton = new Button("Add");
		addButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				String email = emailBox.getText();
				userProfileService.createUserProfile(email,
						new AsyncCallback<Void>() {

							public void onSuccess(Void result) {
								// TODO Auto-generated method stub

							}

							public void onFailure(Throwable caught) {
								// TODO Auto-generated method stub

							}
						});
			}
		});
		addUserPanel.add(addButton);
		initWidget(vpanel);
		refreshTable();
	}

	public void refreshTable() {
		userProfileService.getUserProfiles(new AsyncCallback<List<String>>() {

			public void onSuccess(List<String> result) {
				populateTable(result);
			}

			public void onFailure(Throwable caught) {
				clearTable();
			}
		});
	}

	protected FlexTable getTable() {
		return table;
	}

	private void populateTable(List<String> profiles) {
		FlexTable table = getTable();
		table.clear();
		int row = 0;
		table.setHTML(row, 0, "Email");
		row++;
		for (String userEmail : profiles) {
			table.setHTML(row, 0, userEmail);
			row++;
		}
	}

	private void clearTable() {
		FlexTable table = getTable();
		table.clear();
	}
}
