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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Control for retrieving point, profile along a line
 * 
 * @author nsandhu
 * 
 */
public class ControlPanel extends Composite {

	private final FlowPanel infoPanel;
	private final ToggleButton drawLineButton;

	public ControlPanel(final MapPanel mapPanel) {
		final ToggleButton showData = new ToggleButton("Click to show data");
		showData.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				if (showData.isDown()) {
					mapPanel.activateShowDataHandler(true);
				} else {
					mapPanel.activateShowDataHandler(false);
				}
			}
		});
		drawLineButton = new ToggleButton("Draw line");
		drawLineButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				if (drawLineButton.isDown()) {
					mapPanel.addLine(ControlPanel.this);
				}
			}
		});
		final CheckBox noaaOverlay = new CheckBox(
				"NOAA Overlay: Courtesy http://demo.geogarage.com/noaa");
		noaaOverlay.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				if (noaaOverlay.getValue()) {
					mapPanel.addNOAAOverlay();
				} else {
					mapPanel.removeNOAAOverlay();
				}
			}
		});

		final CheckBox interpolatedOverlay = new CheckBox(
				"Interpolated Overlay");
		interpolatedOverlay.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (interpolatedOverlay.getValue()) {
					mapPanel.removeBathymetryOverlay();
					mapPanel.addInterpolatedBathymetryOverlay();
				} else {
					mapPanel.removeInterpolatedBathymetryOverlay();
					mapPanel.addBathymetryOverlay();
				}
			}
		});
		//
		VerticalPanel buttonPanel = new VerticalPanel();
		buttonPanel.add(showData);
		buttonPanel.add(drawLineButton);
		buttonPanel.add(noaaOverlay);
		buttonPanel.add(interpolatedOverlay);
		infoPanel = new FlowPanel();
		infoPanel.setStyleName("infoPanel");

		FlowPanel mainPanel = new FlowPanel();
		mainPanel.add(buttonPanel);
		mainPanel.add(infoPanel);
		initWidget(mainPanel);
	}

	public ToggleButton getDrawLineButton() {
		return drawLineButton;
	}

	public void showInInfoPanel(Widget widget) {
		infoPanel.clear();
		infoPanel.add(widget);
	}
}
