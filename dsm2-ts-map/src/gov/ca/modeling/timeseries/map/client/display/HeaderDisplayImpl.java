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

import gov.ca.modeling.timeseries.map.client.presenter.HeaderPresenter.HeaderDisplay;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class HeaderDisplayImpl extends Composite implements HeaderDisplay {
	final gov.ca.modeling.dsm2.widgets.client.HeaderPanel headerPanel;

	public HeaderDisplayImpl() {
		headerPanel = new gov.ca.modeling.dsm2.widgets.client.HeaderPanel();
		initWidget(headerPanel);
	}

	public void addLink(String linkText, String linkURL) {
		headerPanel.addToLinkPanel(new Anchor(linkText, linkURL));
	}

	public void addTextToRightSide(String text) {
		headerPanel.addToRightSide(new HTML("<b>" + text + "</b>"));
	}

	public void addLinkToRightSide(String linkText, String linkURL) {
		headerPanel.addToRightSide(new Anchor(linkText, linkURL));
	}

	public void showMessage(boolean show, String message) {
		headerPanel.showWarning(show, message);
	}

	public void showError(boolean show, String message) {
		headerPanel.showError(show, message);
	}

	public void clearMessages() {
		headerPanel.showWarning(false, "");
	}

	@Override
	public Widget asWidget() {
		return this;
	}

}
