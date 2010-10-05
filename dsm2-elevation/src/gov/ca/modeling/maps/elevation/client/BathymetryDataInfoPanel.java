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
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ScrollPanel;

public class BathymetryDataInfoPanel extends Composite {

	public BathymetryDataInfoPanel(List<BathymetryDataPoint> points) {
		FlexTable table = new FlexTable();
		table.setStyleName("ae-table");
		table.setHTML(0, 0, "Easting (UTM NZ10)");
		table.setHTML(0, 1, "Northing (UTM NZ10)");
		table.setHTML(0, 2, "Elevation (NGVD 29)");
		table.setHTML(0, 3, "Year");
		table.setHTML(0, 4, "Agency");
		table.getRowFormatter().setStyleName(0, "ae-table-thead");
		int row = 1;
		for (BathymetryDataPoint point : points) {
			table.setHTML(row, 0, displayPrecision(point.x, 6));
			table.setHTML(row, 1, displayPrecision(point.y, 6));
			table.setHTML(row, 2, displayPrecision(point.z, 2));
			table.setHTML(row, 3, point.year + "");
			table.setHTML(row, 4, point.agency + "");
			table.getRowFormatter().setStyleName(row,
					row % 2 == 0 ? "ae-even" : "ae-odd");
			row++;
		}
		ScrollPanel sPanel = new ScrollPanel(table);
		sPanel.setHeight("15em");
		sPanel.setWidth("38em");
		initWidget(sPanel);
	}

	private String displayPrecision(double value, int precision) {
		double multiplier = Math.pow(10, precision);
		return Math.round(value * multiplier) / multiplier + "";
	}
}
