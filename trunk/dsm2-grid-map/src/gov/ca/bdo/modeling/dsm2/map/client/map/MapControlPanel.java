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

import gov.ca.bdo.modeling.dsm2.map.client.images.IconImages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;

public class MapControlPanel extends Composite {

	private final MapPanel mapPanel;
	private final ListBox studyBox;
	private Anchor downloadHydroEchoLink;
	private Anchor downloadGisEchoLink;
	private final FlexTable containerPanel;
	private DisclosurePanel colorSchemePanel;

	public MapControlPanel(MapPanel panel) {
		mapPanel = panel;
		containerPanel = new FlexTable();
		//
		Label studyLabel = new Label("Study");
		studyBox = new ListBox();

		setStudies(mapPanel.getStudyNames());
		studyBox.addChangeHandler(new ChangeHandler() {

			public void onChange(ChangeEvent event) {

				mapPanel.setStudy(studyBox
						.getValue(studyBox.getSelectedIndex()));
			}
		});
		//
		Label findNodeLabel = new Label("Find Node Id:");
		TextBox findNodeBox = new TextBox();
		findNodeBox.addValueChangeHandler(new ValueChangeHandler<String>() {

			public void onValueChange(ValueChangeEvent<String> event) {
				String nodeId = event.getValue();
				mapPanel.centerAndZoomOnNode(nodeId);
			}
		});
		//
		Label findChannelLabel = new Label("Find Channel Id:");
		TextBox findChannelBox = new TextBox();
		findChannelBox.addValueChangeHandler(new ValueChangeHandler<String>() {

			public void onValueChange(ValueChangeEvent<String> event) {
				String channelId = event.getValue();
				mapPanel.centerAndZoomOnChannel(channelId);
			}
		});
		//
		final ToggleButton addPolylineButton = new ToggleButton(new Image(
				IconImages.INSTANCE.measureOffIcon()));
		final Label measurementLabel = new Label("");
		addPolylineButton.addClickHandler(new ClickHandler() {
			private MeasuringDistanceAlongLine measurer;

			public void onClick(ClickEvent event) {
				if (addPolylineButton.isDown()) {
					if (measurer == null) {
						measurer = new MeasuringDistanceAlongLine(mapPanel
								.getMapWidget(), measurementLabel);
						measurer.addPolyline();
					} else {
						measurer.addPolyline();
					}
				} else {
					if (measurer != null) {
						measurer.clearOverlay();
						measurementLabel.setText("");
					}

				}

			}
		});
		//
		final ToggleButton addPolygonButton = new ToggleButton(new Image(
				IconImages.INSTANCE.measurePolygonIcon()), new Image(
				IconImages.INSTANCE.measurePolygonIcon()));
		addPolygonButton.addClickHandler(new ClickHandler() {
			private MeasuringAreaInPolygon measurer;

			public void onClick(ClickEvent event) {
				if (addPolygonButton.isDown()) {
					if (measurer == null) {
						measurer = new MeasuringAreaInPolygon(mapPanel
								.getMapWidget(), measurementLabel);
						measurer.addPolyline();
					} else {
						measurer.clearOverlay();
						measurementLabel.setText("");
						measurer.addPolyline();
					}
				} else {
					if (measurer != null) {
						measurer.clearOverlay();
						measurementLabel.setText("");
					}

				}

			}
		});
		//
		Label colorArraySchemeLabel = new Label("Color variation scheme: ");
		final ListBox colorArraySchemeOptions = new ListBox();
		colorArraySchemeOptions.addItem("sequential");
		colorArraySchemeOptions.addItem("qualitative");
		colorArraySchemeOptions.addItem("diverging");
		colorArraySchemeOptions.setSelectedIndex(0);
		Label channelColorLabel = new Label("Color Channels By: ");
		final ListBox channelColorOptions = new ListBox();
		channelColorOptions.addItem(MapPanel.CHANNEL_COLOR_PLAIN);
		channelColorOptions.addItem(MapPanel.CHANNEL_COLOR_MANNINGS);
		channelColorOptions.addItem(MapPanel.CHANNEL_COLOR_DISPERSION);
		ChangeHandler colorSchemeHandler = new ChangeHandler() {

			public void onChange(ChangeEvent event) {
				mapPanel.setChannelColorScheme(channelColorOptions
						.getItemText(channelColorOptions.getSelectedIndex()),
						colorArraySchemeOptions
								.getItemText(colorArraySchemeOptions
										.getSelectedIndex()));
			}
		};
		channelColorOptions.addChangeHandler(colorSchemeHandler);
		colorArraySchemeOptions.addChangeHandler(colorSchemeHandler);
		//
		final ToggleButton saveEditModelButton = new ToggleButton("Edit Model",
				"Save Model");
		if (mapPanel.isInEditMode()) {
			saveEditModelButton.setDown(true);
		} else {
			saveEditModelButton.setDown(false);
		}
		saveEditModelButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				if (saveEditModelButton.isDown()) {
					mapPanel.setEditMode(true);
				} else {
					mapPanel.saveCurrentStudy();
					mapPanel.setEditMode(false);
				}
			}
		});
		//
		final ToggleButton addTextAnnotationButton = new ToggleButton(
				new Image(IconImages.INSTANCE.addingTextIcon()));
		addTextAnnotationButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				if (addTextAnnotationButton.isDown()) {
					mapPanel.turnOnTextAnnotation();
				} else {
					mapPanel.turnOffTextAnnotation();
				}
			}
		});
		if (downloadHydroEchoLink == null) {
			downloadHydroEchoLink = new Anchor("Download Hydro Input");
			downloadHydroEchoLink.setTarget("_download_input");
		}
		if (downloadGisEchoLink == null) {
			downloadGisEchoLink = new Anchor("Download GIS Input");
			downloadGisEchoLink.setTarget("_download_input");
		}
		Anchor uploadStudyLink = new Anchor("Upload study here",
				"/upload_study.html");
		Anchor uploadStudyDataLink = new Anchor("Upload study data here",
				"/upload_data.html");
		containerPanel.setWidget(0, 0, studyLabel);
		containerPanel.setWidget(0, 1, studyBox);
		containerPanel.setWidget(0, 2, saveEditModelButton);
		containerPanel.setWidget(1, 0, findNodeLabel);
		containerPanel.setWidget(1, 1, findNodeBox);
		containerPanel.setWidget(1, 2, findChannelLabel);
		containerPanel.setWidget(1, 3, findChannelBox);

		HorizontalPanel toolbarPanel = new HorizontalPanel();
		toolbarPanel.add(addTextAnnotationButton);
		toolbarPanel.add(addPolylineButton);
		toolbarPanel.add(addPolygonButton);
		containerPanel.setWidget(2, 0, toolbarPanel);
		containerPanel.getFlexCellFormatter().setColSpan(2, 0, 3);
		containerPanel.setWidget(3, 0, measurementLabel);
		containerPanel.getFlexCellFormatter().setColSpan(3, 2, 3);

		containerPanel.setWidget(4, 0, channelColorLabel);
		containerPanel.setWidget(4, 1, channelColorOptions);
		containerPanel.setWidget(4, 2, colorArraySchemeOptions);
		containerPanel.setWidget(4, 3, colorSchemePanel = new DisclosurePanel(
				"colorLegend"));
		containerPanel.getFlexCellFormatter().setColSpan(5, 0, 3);
		containerPanel.setWidget(6, 0, downloadHydroEchoLink);
		containerPanel.getFlexCellFormatter().setColSpan(6, 0, 2);
		containerPanel.setWidget(6, 2, downloadGisEchoLink);
		containerPanel.getFlexCellFormatter().setColSpan(6, 2, 2);
		containerPanel.setWidget(7, 0, uploadStudyLink);
		containerPanel.getFlexCellFormatter().setColSpan(7, 0, 2);
		containerPanel.setWidget(7, 2, uploadStudyDataLink);
		containerPanel.getFlexCellFormatter().setColSpan(7, 2, 2);
		initWidget(containerPanel);
	}

	private String buildDownloadLink(String inputName) {
		if (studyBox.getItemCount() == 0) {
			return "";
		} else {
			return GWT.getModuleBaseURL()
					+ "dsm2_download?studyName="
					+ URL.encode(studyBox.getItemText(studyBox
							.getSelectedIndex())) + "&inputName="
					+ URL.encode(inputName);
		}
	}

	public void setStudies(String[] studyNames) {
		for (String studyName : studyNames) {
			studyBox.addItem(studyName, studyName);
		}
		studyBox.setSelectedIndex(0);
		if (downloadHydroEchoLink == null) {
			downloadHydroEchoLink = new Anchor("Download Hydro Input");
			downloadHydroEchoLink.setTarget("_download_input");
		}
		if (downloadGisEchoLink == null) {
			downloadGisEchoLink = new Anchor("Download GIS Input");
			downloadGisEchoLink.setTarget("_download_input");
		}
		downloadHydroEchoLink.setHref(buildDownloadLink("hydro_echo_inp"));
		downloadGisEchoLink.setHref(buildDownloadLink("gis_inp"));

	}

	public void setColorPanel(Panel colorArraySchemePanel) {
		colorSchemePanel.clear();
		colorSchemePanel.add(colorArraySchemePanel);
	}

}