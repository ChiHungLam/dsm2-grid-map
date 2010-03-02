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

import gov.ca.bdo.modeling.dsm2.map.client.model.RegularTimeSeries;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.visualizations.AnnotatedTimeLine;
import com.google.gwt.visualization.client.visualizations.AnnotatedTimeLine.AnnotatedLegendPosition;
import com.google.gwt.visualization.client.visualizations.AnnotatedTimeLine.Options;
import com.google.gwt.visualization.client.visualizations.AnnotatedTimeLine.ScaleType;

public class OutputPanel extends Composite {

	private final FlowPanel container;
	private final String name;

	public OutputPanel(String name, String outputVariables,
			OutputMarkerDataManager manager) {
		this.name = name;
		String[] variables = null;
		if (outputVariables != null) {
			variables = outputVariables.split(",");
		}
		manager.getRegularTimeSeries(name, variables, this);
		container = new FlowPanel();
		initWidget(container);
	}

	@SuppressWarnings("deprecation")
	public void displayData(List<RegularTimeSeries> list) {
		Options options = Options.create();
		options.setDisplayAnnotations(false);
		options.setScaleColumns(0, 1);
		options.setScaleType(ScaleType.ALLMAXIMIZE);
		DataTable table = DataTable.create();
		table.addColumn(ColumnType.DATE, "Date");
		int maxrows = 0;
		for (RegularTimeSeries regularTimeSeries : list) {
			table.addColumn(ColumnType.NUMBER, regularTimeSeries.getType());
			maxrows = Math.max(maxrows, regularTimeSeries.getData().length);
		}
		table.addRows(maxrows);
		RegularTimeSeries timeSeries = list.get(0);
		Date date = timeSeries.getStartTime();
		for (int i = 0; i < maxrows; i++) {
			table.setValue(i, 0, date);
			date = new Date(date.getTime());
			date.setDate(date.getDate() + 1);
		}
		for (int i = 0; i < list.size(); i++) {
			RegularTimeSeries regularTimeSeries = list.get(i);
			double[] data = regularTimeSeries.getData();
			for (int j = 0; j < data.length; j++) {
				table.setValue(j, i + 1, data[j]);
			}
		}
		AnnotatedTimeLine chart = new AnnotatedTimeLine(table, options,
				"600px", "400px");
		options.setLegendPosition(AnnotatedLegendPosition.SAME_ROW);
		VerticalPanel vpanel = new VerticalPanel();
		vpanel.add(new HTML("<h3>Location: " + name + "</h3>"));
		container.add(chart);
	}

	public void displayError(Throwable caught) {
		container.clear();
		String errorMessage = "Could not retrieve data. Error message says "
				+ caught.getMessage();
		HTMLPanel htmlPanel = new HTMLPanel(errorMessage);
		container.add(htmlPanel);
		String possibleCause = "Please check your connection.";
		container.add(new HTML(possibleCause));
	}

}
