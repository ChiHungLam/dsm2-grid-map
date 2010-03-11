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

import gov.ca.dsm2.input.model.Gate;
import gov.ca.dsm2.input.model.GateDevice;
import gov.ca.dsm2.input.model.GatePipeDevice;
import gov.ca.dsm2.input.model.GateWeirDevice;
import gov.ca.dsm2.input.model.OperatingRule;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class GateInfoPanel extends Composite {

	public GateInfoPanel(Gate gate) {
		Panel basicInfo = getBasicInfoPanel(gate);
		initWidget(basicInfo);
	}

	private Panel getBasicInfoPanel(Gate gate) {
		HTMLPanel basicPanel = new HTMLPanel("<h3>GATE " + gate.getName()
				+ " [ " + gate.getFromObject() + " " + gate.getFromIdentifier()
				+ " ---> node " + gate.getToNode() + " ]</h3>");

		FlexTable deviceTable = new FlexTable();
		deviceTable.setStyleName("bordered-title");
		deviceTable.setWidth("100%");
		ArrayList<GateDevice> gateDevices = gate.getGateDevices();
		deviceTable.setTitle("Gate Devices");
		int col = 0;
		deviceTable.getRowFormatter().setStyleName(0, "table-header");
		deviceTable.getRowFormatter().setStyleName(1, "table-header");
		deviceTable.getFlexCellFormatter().setRowSpan(0, col, 2);
		deviceTable.setHTML(0, col++, "<b>TYPE</b>");
		deviceTable.getFlexCellFormatter().setRowSpan(0, col, 2);
		deviceTable.setHTML(0, col++, "<b>DEVICE</b>");
		deviceTable.getFlexCellFormatter().setRowSpan(0, col, 2);
		deviceTable.setHTML(0, col++, "<b># DUP</b>");
		deviceTable.getFlexCellFormatter().setRowSpan(0, col, 2);
		deviceTable.setHTML(0, col++, "<b>COEFF FROM</b>");
		deviceTable.getFlexCellFormatter().setRowSpan(0, col, 2);
		deviceTable.setHTML(0, col++, "<b>COEFF TO</b>");
		deviceTable.getFlexCellFormatter().setRowSpan(0, col, 2);
		deviceTable.setHTML(0, col++, "<b>DEF OPS</b>");
		deviceTable.getFlexCellFormatter().setRowSpan(0, col, 2);
		deviceTable.setHTML(0, col++, "<b>ELEV</b>");
		deviceTable.setHTML(0, col, "<b>RADIUS</b>");
		deviceTable.getFlexCellFormatter().setColSpan(0, col, 2);
		deviceTable.setHTML(1, 0, "<b>HEIGHT</b>");
		deviceTable.setHTML(1, 1, "<b>WIDTH</b>");
		int row = 2;
		for (GateDevice device : gateDevices) {
			col = 0;
			if (device instanceof GatePipeDevice) {
				deviceTable.setText(row, col++, "PIPE");
			} else if (device instanceof GateWeirDevice) {
				deviceTable.setText(row, col++, "WEIR");
			} else {
				deviceTable.setText(row, col++, "PLAIN");
			}
			deviceTable.setText(row, col++, device.device);
			deviceTable.setText(row, col++, device.numberOfDuplicates + "");
			deviceTable.setText(row, col++, device.coefficientFromNode + "");
			deviceTable.setText(row, col++, device.coefficientToNode + "");
			deviceTable.setText(row, col++, device.defaultOperation + "");
			deviceTable.setText(row, col++, device.elevation + "");
			if (device instanceof GatePipeDevice) {
				GatePipeDevice pipeDevice = (GatePipeDevice) device;
				deviceTable.setText(row, col++, pipeDevice.radius + "");
				deviceTable.getFlexCellFormatter().setColSpan(row, col, 2);
			} else if (device instanceof GateWeirDevice) {
				GateWeirDevice weirDevice = (GateWeirDevice) device;
				deviceTable.setText(row, col++, weirDevice.height + "");
				deviceTable.setText(row, col++, weirDevice.width + "");
			} else {
			}
			row++;
		}
		FlexTable ruleTable = new FlexTable();
		ruleTable.setStyleName("bordered-title");
		ruleTable.setWidth("100%");
		row = 0;
		col = 0;
		ruleTable.getColumnFormatter().setWidth(col, "10%");
		ruleTable.setHTML(row, col++, "<b>NAME</b>");
		ruleTable.getColumnFormatter().setWidth(col, "70%");
		ruleTable.setHTML(row, col++, "<b>TRIGGER</b>");
		ruleTable.getColumnFormatter().setWidth(col, "20%");
		ruleTable.setHTML(row, col++, "<b>ACTION</b>");
		ruleTable.getRowFormatter().setStyleName(0, "table-header");
		ArrayList<OperatingRule> gateOperations = gate.getGateOperations();
		row = 1;
		for (OperatingRule rule : gateOperations) {
			col = 0;
			ruleTable.setText(row, col++, rule.name);
			ruleTable.setText(row, col++, rule.trigger);
			ruleTable.setText(row, col++, rule.action);
		}
		VerticalPanel vPanel = new VerticalPanel();
		vPanel.add(basicPanel);
		vPanel.add(new HTML("<h3>DEVICES</h3>"));
		vPanel.add(deviceTable);
		vPanel.add(new HTML("<h3>RULES</h3>"));
		vPanel.add(ruleTable);
		return vPanel;
	}
}
