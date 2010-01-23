package gov.ca.bdo.modeling.dsm2.map.client;

import gov.ca.dsm2.input.model.Channel;
import gov.ca.dsm2.input.model.XSection;
import gov.ca.dsm2.input.model.XSectionLayer;

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

	public ChannelInfoPanel(Channel channel) {
		// Panel basicInfo = getBasicInfoPanel(channel);
		FlowPanel xsectionPanel = new FlowPanel();
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
		String[] colors = new String[channel.getXsections().size()];
		for (XSection xsection : channel.getXsections()) {
			double distance = xsection.getDistance();
			colors[i] = "#"
					+ Integer.toHexString((int) Math.round(255 * distance))
					+ "00"
					+ Integer.toHexString((int) Math
							.round(255 - 255 * distance));
			i++;
			table.addColumn(ColumnType.NUMBER, " @ " + distance);
			for (XSectionLayer layer : xsection.getLayers()) {
				double elevation = layer.getElevation();
				double topWidth = layer.getTopWidth();
				table.insertRows(0, 1);
				table.setValue(0, i, elevation);
				table.setValue(0, 0, -topWidth / 2);
				int nrows = table.getNumberOfRows();
				if (nrows >= table.getNumberOfRows()) {
					table.insertRows(nrows, 1);
				}
				table.setValue(nrows, i, elevation);
				table.setValue(nrows, 0, topWidth / 2);
			}
		}
		options.setColors(colors);
		ScatterChart chart = new ScatterChart(table, options);
		xsectionPanel.add(chart);
		VerticalPanel vpanel = new VerticalPanel();
		DisclosurePanel basicDisclosure = new DisclosurePanel("Basic");
		basicDisclosure.setOpen(true);
		basicDisclosure.add(getBasicInfoPanel(channel));
		vpanel.add(basicDisclosure);
		DisclosurePanel xsectionDisclosure = new DisclosurePanel("XSection");
		xsectionDisclosure.setOpen(true);
		xsectionDisclosure.add(xsectionPanel);
		vpanel.add(xsectionDisclosure);
		initWidget(vpanel);
	}

	private Panel getBasicInfoPanel(Channel channel) {
		return new HTMLPanel("<h3>Channel " + channel.getId() + "</h3>"
				+ "<table>" + "<tr><td>Length</td><td>" + channel.getLength()
				+ "</td></tr>" + "<tr><td>Mannings</td><td>"
				+ channel.getMannings() + "</td></tr>"
				+ "<tr><td>Dispersion</td><td>" + channel.getDispersion()
				+ "</td></tr>" + "</table>");
	}
}
