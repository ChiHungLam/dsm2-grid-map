/**
 *    Copyright (C) 2009, 2010 
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
 *    GNU General Public License for more details. [http://www.gnu.org/licenses]
 *    
 *    @author Nicky Sandhu
 *    
 */
package gov.ca.modeling.maps.elevation.client;

import gov.ca.modeling.maps.elevation.client.model.BathymetryDataPoint;

import java.util.List;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.LegendPosition;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.visualizations.ScatterChart;
import com.google.gwt.visualization.client.visualizations.ScatterChart.Options;

/**
 * Charts a list of bathymetry points on an X-Y graph where X is the length of
 * projection along the vertical line and Y is the depth/elevation
 * 
 * Todo: The distance from the vertical plane can be represented by transparency
 * The color can represent the age of the data ?
 * 
 * @author nsandhu
 * 
 */
public class ElevationChart extends Composite {

	public ElevationChart(List<BathymetryDataPoint> points) {
		FlowPanel chartPanel = new FlowPanel();
		initWidget(chartPanel);
		ensureCodeAndCreateChart(points);
	}

	protected void ensureCodeAndCreateChart(
			final List<BathymetryDataPoint> points) {
		VisualizationUtils.loadVisualizationApi(new Runnable() {

			public void run() {
				Panel panel = (Panel) getWidget();
				panel.add(createChart(points));
			}
		}, ScatterChart.PACKAGE);

	}

	protected ScatterChart createChart(List<BathymetryDataPoint> points) {
		String title = "Elevation Chart";
		Options options = Options.create();
		options.setTitle(title);
		options.setTitleX("Distance Along Line");
		options.setTitleY("Elevation (ft)");
		options.setHeight(300);
		options.setWidth(500);
		options.setLegend(LegendPosition.BOTTOM);
		options.setShowCategories(false);
		DataTable table = DataTable.create();
		table.addColumn(ColumnType.NUMBER, "Width");
		table.addColumn(ColumnType.NUMBER, "Distance");
		int i = 0;
		table.insertRows(0, points.size());
		for (BathymetryDataPoint point : points) {
			table.setValue(i, 1, point.z);
			table.setValue(i, 0, point.x);
			i++;
		}
		ScatterChart chart = new ScatterChart(table, options);
		return chart;
	}
}
