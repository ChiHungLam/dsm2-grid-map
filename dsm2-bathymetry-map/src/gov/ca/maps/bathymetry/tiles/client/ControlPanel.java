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

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.widgetideas.client.SliderBar;

/**
 * Control for retrieving point, profile along a line
 * 
 * @author nsandhu
 * 
 */
public class ControlPanel extends Composite {

	private final FlowPanel infoPanel;

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
		showData
				.setTitle("Toggle button to display raw data around point clicked on map");
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
		noaaOverlay.setTitle("Adds NOAA Chart maps");
		final ListBox overlayBox = new ListBox();
		overlayBox.addItem("Raw Data", "");
		overlayBox.addItem("Interpolated Data", "i");
		overlayBox.addItem("Interpolated Data + Lidar", "il");
		overlayBox.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				int index = overlayBox.getSelectedIndex();
				String prefix = overlayBox.getValue(index);
				mapPanel.removeOverlays();
				mapPanel.addOverlay(prefix);
			}
		});
		overlayBox.setTitle("Overlay with raw, interpolated and/or lidar");
		// Opacity slider
		final SliderBar opacitySlider = new SliderBar(0, 1.0);
		opacitySlider.setStepSize(0.1);
		opacitySlider.setCurrentValue(0.6);
		opacitySlider.setNumTicks(10);
		opacitySlider.setNumLabels(5);
		opacitySlider.addChangeListener(new ChangeListener() {

			@Override
			public void onChange(Widget sender) {
				double opacity = opacitySlider.getCurrentValue();
				mapPanel.setLayerOpacity(opacity);
			}
		});
		opacitySlider.setTitle("Sets opacity of bathymetry layer");
		//
		VerticalPanel buttonPanel = new VerticalPanel();
		buttonPanel.add(new HTML("<hr/>"));
		buttonPanel.add(showData);
		// buttonPanel.add(drawLineButton);
		buttonPanel.add(new HTML("<hr/>"));
		buttonPanel.add(noaaOverlay);
		buttonPanel.add(new HTML("<hr/>"));
		buttonPanel.add(overlayBox);
		buttonPanel.add(new HTML("<hr/>"));
		buttonPanel.add(opacitySlider);
		infoPanel = new FlowPanel();
		infoPanel.setStyleName("infoPanel");

		FlowPanel mainPanel = new FlowPanel();
		mainPanel.add(buttonPanel);
		mainPanel.add(infoPanel);
		initWidget(mainPanel);
	}

	public void showInInfoPanel(Widget widget) {
		infoPanel.clear();
		infoPanel.add(widget);
	}
}
