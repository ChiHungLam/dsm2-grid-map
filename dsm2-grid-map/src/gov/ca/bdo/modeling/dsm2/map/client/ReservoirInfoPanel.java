package gov.ca.bdo.modeling.dsm2.map.client;

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
