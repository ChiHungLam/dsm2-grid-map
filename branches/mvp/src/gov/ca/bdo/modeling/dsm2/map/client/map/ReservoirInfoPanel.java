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
package gov.ca.bdo.modeling.dsm2.map.client.map;

import gov.ca.dsm2.input.model.Reservoir;
import gov.ca.dsm2.input.model.ReservoirConnection;

import java.util.List;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Panel;

public class ReservoirInfoPanel extends Composite {

	public ReservoirInfoPanel(Reservoir reservoir) {
		Panel basicInfo = getBasicInfoPanel(reservoir);
		initWidget(basicInfo);
	}

	private Panel getBasicInfoPanel(Reservoir reservoir) {
		FlowPanel panel = new FlowPanel();
		panel.add(new HTMLPanel("<h3>Reservoir " + reservoir.getName()
				+ "</h3>"));
		FlexTable table = new FlexTable();
		table.setHTML(1, 0, "Area (Million Sq. Feet): " + reservoir.getArea());
		table.setHTML(2, 0, "Bottom Elevation (Feet): "
				+ reservoir.getBottomElevation());
		panel.add(table);
		FlexTable connectionTable = new FlexTable();
		connectionTable.setHTML(0, 0, "<b>NODE</b>");
		connectionTable.setHTML(0, 1, "<b>COEFF IN</b>");
		connectionTable.setHTML(0, 2, "<b>COEFF OUT</b>");
		List<ReservoirConnection> reservoirConnections = reservoir
				.getReservoirConnections();
		int index = 1;
		for (ReservoirConnection reservoirConnection : reservoirConnections) {
			connectionTable.setHTML(index, 0, reservoirConnection.nodeId);
			connectionTable.setHTML(index, 1, ""
					+ reservoirConnection.coefficientIn);
			connectionTable.setHTML(index, 2, ""
					+ reservoirConnection.coefficientOut);
			index++;
		}
		panel.add(connectionTable);
		return panel;
	}
}
