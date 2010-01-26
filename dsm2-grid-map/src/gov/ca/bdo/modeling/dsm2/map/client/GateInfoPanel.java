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

import gov.ca.dsm2.input.model.Gate;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Panel;

public class GateInfoPanel extends Composite {

	public GateInfoPanel(Gate gate) {
		Panel basicInfo = getBasicInfoPanel(gate);
		initWidget(basicInfo);
	}

	private Panel getBasicInfoPanel(Gate gate) {
		return new HTMLPanel("<h3>Gate " + gate.getName() + "</h3>"
				+ "<p>From " + gate.getFromObject() + " "
				+ gate.getFromIdentifier() + " to node " + gate.getToNode()
				+ "</p>");
	}
}
