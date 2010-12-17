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

import gov.ca.dsm2.input.model.Channel;
import gov.ca.dsm2.input.model.Node;
import gov.ca.dsm2.input.model.XSection;
import gov.ca.dsm2.input.model.XSectionLayer;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.LegendPosition;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.visualizations.ScatterChart;
import com.google.gwt.visualization.client.visualizations.ScatterChart.Options;

//TODO: more efficient to make a setChannel() method that updates existing 
// panels rather than creating new ones
public class ChannelInfoPanel extends Composite {

	private FlowPanel xsectionPanel;
	private DisclosurePanel xsectionDisclosure;
	private FlowPanel xsectionContainerPanel;

	public ChannelInfoPanel(Channel channel, final MapPanel mapPanel) {
		xsectionContainerPanel = new FlowPanel();
		xsectionPanel = new FlowPanel();
		xsectionContainerPanel.add(xsectionPanel);
		drawXSection(channel, -1);
		VerticalPanel vpanel = new VerticalPanel();
		DisclosurePanel basicDisclosure = new DisclosurePanel("Basic");
		basicDisclosure.setOpen(true);
		basicDisclosure.add(getBasicInfoPanel(channel));
		Node upNode = mapPanel.getNodeManager().getNodes().getNode(
				channel.getUpNodeId());
		Node downNode = mapPanel.getNodeManager().getNodes().getNode(
				channel.getDownNodeId());
		// basicDisclosure.add(getXSectionGenerationPanel(channel, upNode,
		// downNode));
		vpanel.add(basicDisclosure);
		xsectionDisclosure = new DisclosurePanel("XSection");
		xsectionDisclosure.setOpen(true);
		xsectionDisclosure.add(xsectionContainerPanel);
		vpanel.add(xsectionDisclosure);
		initWidget(vpanel);
	}

	private Panel getXSectionGenerationPanel(final Channel channel,
			final Node upNode, final Node downNode) {
		Panel panel = new FlowPanel();
		Button generateXSectionsButton = new Button("Generate XSections");
		Button clearXSectionsButton = new Button("Clear XSections");
		panel.add(generateXSectionsButton);
		panel.add(clearXSectionsButton);
		generateXSectionsButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				ModelUtils.generateXSections(channel, upNode, downNode);
			}
		});
		return panel;
	}

	/**
	 * Creates a profile of the xsection perpendicular to the flow line for the
	 * channel
	 * 
	 * @param channel
	 * @param index
	 *            , the index of the xsection to be drawn or -1 for all
	 * @return
	 */
	public void drawXSection(Channel channel, int index) {
		String title = "Channel: " + channel.getId() + " Length: "
				+ channel.getLength() + " X-Section View";
		Options options = Options.create();
		options.setHeight(350);
		options.setTitle(title);
		options.setTitleX("Centered Width");
		options.setTitleY("Elevation (ft)");
		options.setWidth(500);
		options.setLineSize(1);
		options.setLegend(LegendPosition.BOTTOM);
		options.setShowCategories(false);
		DataTable table = DataTable.create();
		table.addColumn(ColumnType.NUMBER, "Width");
		int i = 0;
		int numberOfXSections = channel.getXsections().size();
		String[] colors = new String[numberOfXSections];
		int actualCount = 0;
		for (XSection xsection : channel.getXsections()) {
			if (((index >= 0) && (index < numberOfXSections)) && (i != index)) {
				i++;
				continue; // skip if index specified is valid
			}
			double distance = xsection.getDistance();
			colors[actualCount] = "#"
					+ getHexString((int) Math.round(255 * distance)) + "33"
					+ getHexString((int) Math.round(255 - 255 * distance));
			i++;
			actualCount++;
			table.addColumn(ColumnType.NUMBER, " @ " + distance);
			for (XSectionLayer layer : xsection.getLayers()) {
				double elevation = layer.getElevation();
				double topWidth = layer.getTopWidth();
				table.insertRows(0, 1);
				table.setValue(0, actualCount, elevation);
				table.setValue(0, 0, -topWidth / 2);
				int nrows = table.getNumberOfRows();
				if (nrows >= table.getNumberOfRows()) {
					table.insertRows(nrows, 1);
				}
				table.setValue(nrows, actualCount, elevation);
				table.setValue(nrows, 0, topWidth / 2);
			}
		}
		String[] cArray = new String[actualCount];
		System.arraycopy(colors, 0, cArray, 0, actualCount);
		options.setColors(cArray);
		ScatterChart chart = new ScatterChart(table, options);
		xsectionPanel.setVisible(false);
		xsectionPanel.clear();
		xsectionPanel.add(chart);
		xsectionPanel.setVisible(true);
	}

	private String getHexString(int value) {
		String hexString = Integer.toHexString(value);
		if (hexString.length() == 1) {
			hexString = "0" + hexString;
		}
		return hexString;
	}

	private Panel getBasicInfoPanel(Channel channel) {
		return new HTMLPanel("<h3>Channel " + channel.getId() + "[ "
				+ channel.getUpNodeId() + "->" + channel.getDownNodeId() + " ]"
				+ "</h3>" + "<table>" + "<tr><td>Length</td><td>"
				+ channel.getLength() + "</td></tr>"
				+ "<tr><td>Mannings</td><td>" + channel.getMannings()
				+ "</td></tr>" + "<tr><td>Dispersion</td><td>"
				+ channel.getDispersion() + "</td></tr>" + "</table>");
	}

}
