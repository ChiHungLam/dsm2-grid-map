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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.ControlAnchor;
import com.google.gwt.maps.client.control.ControlPosition;
import com.google.gwt.maps.client.control.Control.CustomControl;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class GridVisibilityControl extends CustomControl {

	private final MapPanel mapPanel;
	private CheckBox channelHideBox;
	private CheckBox gatesHideBox;
	private CheckBox reservoirsHideBox;
	private CheckBox outputMarkerHideBox;
	private CheckBox boundaryMarkerHideBox;
	private CheckBox bathymetryHideBox;
	private CheckBox nodeHideBox;

	public GridVisibilityControl(MapPanel mapPanel) {
		super(new ControlPosition(ControlAnchor.TOP_RIGHT, 135, 5));
		this.mapPanel = mapPanel;
	}

	@Override
	protected Widget initialize(MapWidget map) {
		final Label hideLabel = new Label("Hide...");
		nodeHideBox = new CheckBox();
		nodeHideBox.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				mapPanel.hideNodeMarkers(nodeHideBox.getValue());
			}
		});
		channelHideBox = new CheckBox();
		channelHideBox.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				mapPanel.hideChannelLines(channelHideBox.getValue());
			}
		});
		//
		gatesHideBox = new CheckBox();
		gatesHideBox.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				mapPanel.hideGateMarkers(gatesHideBox.getValue());
			}
		});
		gatesHideBox.setValue(true, true);
		//
		reservoirsHideBox = new CheckBox();
		reservoirsHideBox.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				mapPanel.hideReservoirMarkers(reservoirsHideBox.getValue());
			}
		});
		//
		outputMarkerHideBox = new CheckBox();
		outputMarkerHideBox.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				mapPanel.hideOutputMarkers(outputMarkerHideBox.getValue());
			}
		});
		outputMarkerHideBox.setValue(true, true);
		//
		boundaryMarkerHideBox = new CheckBox();
		boundaryMarkerHideBox.setValue(true);
		boundaryMarkerHideBox.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				mapPanel.hideBoundaryMarkers(boundaryMarkerHideBox.getValue());
			}
		});
		//
		bathymetryHideBox = new CheckBox();
		bathymetryHideBox.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				mapPanel.showBathymetry(bathymetryHideBox.getValue());
			}
		});

		VerticalPanel vpanel = new VerticalPanel();
		vpanel.add(hideLabel);
		vpanel.add(new HTML("<hr/>"));
		vpanel.add(wrapCaptionPanel(nodeHideBox, "Nodes"));
		vpanel.add(wrapCaptionPanel(channelHideBox, "Channels"));
		vpanel.add(wrapCaptionPanel(gatesHideBox, "Gates"));
		vpanel.add(wrapCaptionPanel(reservoirsHideBox, "Reservoirs"));
		vpanel.add(wrapCaptionPanel(outputMarkerHideBox, "Output"));
		vpanel.add(wrapCaptionPanel(boundaryMarkerHideBox, "Boundaries"));
		vpanel.add(new HTML("<hr/>"));
		vpanel.add(wrapCaptionPanel(bathymetryHideBox, "Bathymetry"));
		DisclosurePanel panel = new DisclosurePanel("Visibility   ");
		panel.setStyleName("visibilityPanel");
		panel.setContent(vpanel);
		return panel;
	}

	private Widget wrapCaptionPanel(CheckBox box, String caption) {
		CaptionPanel captionPanel = new CaptionPanel(caption);
		captionPanel.add(box);
		captionPanel.setStyleName("small-captions");
		return captionPanel;
	}

	@Override
	public boolean isSelectable() {
		return false;
	}

	public HasValue<Boolean> getHideChannels() {
		return channelHideBox;
	}

	public HasValue<Boolean> getHideNodes() {
		return nodeHideBox;
	}

	public HasValue<Boolean> getHideGates() {
		return gatesHideBox;
	}

	public HasValue<Boolean> getHideReservoirs() {
		return reservoirsHideBox;
	}

	public HasValue<Boolean> getHideOutputs() {
		return outputMarkerHideBox;
	}

	public HasValue<Boolean> getHideBathymetry() {
		return bathymetryHideBox;
	}

	public HasValue<Boolean> getHideBoundaries() {
		return boundaryMarkerHideBox;
	}
}