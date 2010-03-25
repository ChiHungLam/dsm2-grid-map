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
package gov.ca.maps.bathymetry.tiles.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;

public class MainEntryPoint implements EntryPoint {

	private DockLayoutPanel mainPanel;
	private MapPanel mapPanel;

	public void onModuleLoad() {
		if (!Maps.isLoaded()) {
			Window
					.alert("The Maps API is not installed."
							+ "  The <script> tag that loads the Maps API may be missing or your Maps key may be wrong.");
			return;
		}

		if (!Maps.isBrowserCompatible()) {
			Window.alert("The Maps API is not compatible with this browser.");
			return;
		}
		Runnable mapLoadCallback = new Runnable() {

			public void run() {
				createUI();
			}
		};
		mapLoadCallback.run();

	}

	protected void createUI() {
		mainPanel = new DockLayoutPanel(Unit.EM);
		mapPanel = new MapPanel();
		mainPanel
				.addNorth(
						new HTML(
								"<h3>Bathymetry Data for the Sacramento-San Joaquin Delta</h3>"),
						5);
		StackLayoutPanel sidePanel = new StackLayoutPanel(Unit.EM);
		sidePanel.add(ExportOverlays.getLegendPanel(), new HTML("Legend"), 2);
		sidePanel.add(new ControlPanel(mapPanel), new HTML("Controls"), 2);
		mainPanel.addEast(sidePanel, 25);
		mainPanel.add(mapPanel);
		RootLayoutPanel.get().add(mainPanel);
		RootLayoutPanel.get().animate(0, new AnimationCallback() {
			public void onLayout(Layer layer, double progress) {
			}

			public void onAnimationComplete() {
				mapPanel.onResize();
			}
		});
	}

}
