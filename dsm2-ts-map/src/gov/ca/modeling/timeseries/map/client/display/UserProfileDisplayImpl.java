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
package gov.ca.modeling.timeseries.map.client.display;

import gov.ca.modeling.timeseries.map.client.presenter.UserProfilePresenter.UserProfileDisplay;

import java.util.List;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class UserProfileDisplayImpl extends Composite implements
		UserProfileDisplay {
	private final FlexTable table;
	private TextBox emailBox;
	private Button addButton;

	public UserProfileDisplayImpl() {
		table = new FlexTable();
		VerticalPanel vpanel = new VerticalPanel();
		vpanel.add(table);
		HorizontalPanel addUserPanel = new HorizontalPanel();
		vpanel.add(addUserPanel);
		addUserPanel.add(new Label("Email: "));
		addUserPanel.add(emailBox = new TextBox());
		addButton = new Button("Add");
		addUserPanel.add(addButton);
		initWidget(vpanel);
		refreshTable();
	}

	public void refreshTable() {
	}

	protected FlexTable getTable() {
		return table;
	}

	public void populateTable(List<String> profiles) {
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

	public void clearTable() {
		FlexTable table = getTable();
		table.clear();
	}

	public Widget asWidget() {
		return this;
	}

	public HasClickHandlers getAddButton() {
		return addButton;
	}

	public HasText getEmail() {
		return emailBox;
	}
}
