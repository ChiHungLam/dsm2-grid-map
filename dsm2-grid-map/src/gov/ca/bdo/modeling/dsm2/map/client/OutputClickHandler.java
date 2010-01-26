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

import gov.ca.bdo.modeling.dsm2.map.client.model.OutputMarkerDataManager;

import com.google.gwt.maps.client.event.MarkerClickHandler;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.AnnotatedTimeLine;

public class OutputClickHandler implements MarkerClickHandler {
	private final String name;
	private final OutputMarkerDataManager outputManager;
	private final MapPanel mapPanel;

	public OutputClickHandler(String name,
			OutputMarkerDataManager outputManager, MapPanel mapPanel) {
		this.name = name;
		this.outputManager = outputManager;
		this.mapPanel = mapPanel;
	}

	public void onClick(final MarkerClickEvent event) {
		Runnable visualizationLoadCallback = new Runnable() {
			public void run() {
				doOnClick(event);
			}
		};
		// Load the visualization api, passing the onLoadCallback to be called
		// when loading is done.
		VisualizationUtils.loadVisualizationApi(visualizationLoadCallback,
				AnnotatedTimeLine.PACKAGE);
	}

	public void doOnClick(MarkerClickEvent event) {

		mapPanel.getInfoPanel().clear();

		mapPanel.getInfoPanel().add(
				new OutputPanel(name, outputManager.getOutputVariables(name),
						outputManager));

	}

}
