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
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class MapControlPanel extends Composite {

	private final ListBox studyBox;
	private Anchor downloadHydroEchoLink;
	private Anchor downloadGisEchoLink;
	private final FlexTable containerPanel;
	private TextBox findNodeBox;
	private TextBox findChannelBox;
	private ToggleButton saveEditModelButton;
	private ToggleButton addTextAnnotationButton;
	private ToggleButton addPolylineButton;
	private ToggleButton addPolygonButton;
	private String[] studies;
	private boolean viewOnly;
	private Label measurementLabel;
	private TextBox kmlUrlBox;
	private Button kmlButton;
	private ToggleButton flowLineButton;
	private Widget elementEditPanel;
	private ToggleButton addElementButton;
	private ToggleButton deleteElementButton;
	private ToggleButton clickForElevationButton;
	private ToggleButton drawXSectionButton;

	public MapControlPanel(boolean viewOnly) {
		this.viewOnly = viewOnly;
		containerPanel = new FlexTable();
		//
		Label studyLabel = new Label("Study");
		studyBox = new ListBox();
		//
		Label findNodeLabel = new Label("Find Node Id:");
		findNodeBox = new TextBox();
		//
		Label findChannelLabel = new Label("Find Channel Id:");
		findChannelBox = new TextBox();

		measurementLabel = new Label("");
		addPolylineButton = new ToggleButton(new Image(IconImages.INSTANCE
				.measureOffIcon()));
		addPolygonButton = new ToggleButton(new Image(IconImages.INSTANCE
				.measurePolygonIcon()), new Image(IconImages.INSTANCE
				.measurePolygonIcon()));
		clickForElevationButton = new ToggleButton(new Image(
				IconImages.INSTANCE.elevationIcon()));
		drawXSectionButton = new ToggleButton(new Image(IconImages.INSTANCE
				.elevationProfileIcon()));
		//
		saveEditModelButton = new ToggleButton("Edit Model", "Save Model");
		addTextAnnotationButton = new ToggleButton(new Image(
				IconImages.INSTANCE.addingTextIcon()));
		flowLineButton = new ToggleButton("Show Flowlines", "Hide Flowlines");
		if (downloadHydroEchoLink == null) {
			downloadHydroEchoLink = new Anchor("Download Hydro Input");
			downloadHydroEchoLink.setTarget("hydro.inp");
		}
		if (downloadGisEchoLink == null) {
			downloadGisEchoLink = new Anchor("Download GIS Input");
			downloadGisEchoLink.setTarget("gis.inp");
		}
		containerPanel.setWidget(0, 0, studyLabel);
		containerPanel.setWidget(0, 1, studyBox);
		if (!viewOnly) {
			containerPanel.setWidget(0, 2, saveEditModelButton);
		}
		containerPanel.setWidget(1, 0, findNodeLabel);
		containerPanel.setWidget(1, 1, findNodeBox);
		containerPanel.setWidget(1, 2, findChannelLabel);
		containerPanel.setWidget(1, 3, findChannelBox);

		addElementButton = new ToggleButton(new Image(IconImages.INSTANCE
				.addIcon()));
		deleteElementButton = new ToggleButton(new Image(IconImages.INSTANCE
				.deleteIcon()));
		HorizontalPanel addDeleteButtonPanel = new HorizontalPanel();
		addDeleteButtonPanel.add(addElementButton);
		addDeleteButtonPanel.add(deleteElementButton);
		CaptionPanel cp = new CaptionPanel("Add/Delete Grid Elements");
		cp.add(addDeleteButtonPanel);
		elementEditPanel = cp;
		elementEditPanel.setVisible(false);

		HorizontalPanel toolbarPanel = new HorizontalPanel();
		toolbarPanel.add(addTextAnnotationButton);
		toolbarPanel.add(addPolylineButton);
		toolbarPanel.add(addPolygonButton);
		toolbarPanel.add(flowLineButton);
		toolbarPanel.add(elementEditPanel);
		toolbarPanel.add(clickForElevationButton);
		toolbarPanel.add(drawXSectionButton);
		containerPanel.setWidget(2, 0, toolbarPanel);
		containerPanel.getFlexCellFormatter().setColSpan(2, 0, 3);
		containerPanel.setWidget(3, 0, measurementLabel);
		containerPanel.getFlexCellFormatter().setColSpan(3, 2, 3);
		// add kml overlay
		kmlUrlBox = new TextBox();
		kmlButton = new Button("Add");
		HorizontalPanel kmlOverlayPanel = new HorizontalPanel();
		kmlOverlayPanel.add(new Label("Add KML URL"));
		kmlOverlayPanel.add(kmlUrlBox);
		kmlOverlayPanel.add(kmlButton);
		containerPanel.setWidget(4, 0, kmlOverlayPanel);
		containerPanel.getFlexCellFormatter().setColSpan(4, 0, 3);
		//
		containerPanel.getFlexCellFormatter().setColSpan(5, 0, 3);
		containerPanel.setWidget(6, 0, downloadHydroEchoLink);
		containerPanel.getFlexCellFormatter().setColSpan(6, 0, 2);
		containerPanel.setWidget(6, 2, downloadGisEchoLink);
		containerPanel.getFlexCellFormatter().setColSpan(6, 2, 2);
		initWidget(containerPanel);
	}

	private String buildDownloadLink(String inputName) {
		if (viewOnly) {
			// FIXME: not a good way. fix later
			String token = History.getToken();
			String studyKey = token.substring(token.indexOf("/") + 1);
			return GWT.getModuleBaseURL() + "dsm2_download?studyKey="
					+ URL.encode(studyKey) + "&inputName="
					+ URL.encode(inputName);
		} else {
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
	}

	public void setStudies(String[] studyNames) {
		studies = studyNames;
		for (String studyName : studyNames) {
			studyBox.addItem(studyName, studyName);
		}
		if (studyBox.getItemCount() > 0) {
			setStudy(studyBox.getItemText(0));
		}
	}

	public HasChangeHandlers getStudyBox() {
		return studyBox;
	}

	public String getStudyChoice() {
		return studyBox.getItemText(studyBox.getSelectedIndex());
	}

	public HasValueChangeHandlers<String> getNodeIdBox() {
		return findNodeBox;
	}

	public HasValueChangeHandlers<String> getChannelIdBox() {
		return findChannelBox;
	}

	public HasClickHandlers getAddPolylineButton() {
		return addPolylineButton;
	}

	public HasClickHandlers getAddPolygonButton() {
		return addPolygonButton;
	}

	public HasClickHandlers getAddTextAnnonationButton() {
		return addTextAnnotationButton;
	}

	public HasClickHandlers getSaveEditModelButton() {
		return saveEditModelButton;
	}

	public HasClickHandlers getAddKmlButton() {
		return kmlButton;
	}

	public HasText getKmlUrlBox() {
		return kmlUrlBox;
	}

	public void setStudy(String studyName) {
		int count = studyBox.getItemCount();
		for (int i = 0; i < count; i++) {
			if (studyName.equals(studyBox.getItemText(i))) {
				studyBox.setSelectedIndex(i);
				updateLinks();
				break;
			}
		}
	}

	public void updateLinks() {
		if (downloadHydroEchoLink == null) {
			downloadHydroEchoLink = new Anchor("Download Hydro Input");
		}
		if (downloadGisEchoLink == null) {
			downloadGisEchoLink = new Anchor("Download GIS Input");
		}
		downloadHydroEchoLink.setHref(buildDownloadLink("hydro_echo_inp"));
		downloadGisEchoLink.setHref(buildDownloadLink("gis_inp"));
	}

	public String[] getStudies() {
		return studies;
	}

	public Label getMeasurementLabel() {
		return measurementLabel;
	}

	public HasClickHandlers getFlowLineButton() {
		return flowLineButton;
	}

	public void setEditMode(boolean editMode) {
		if (editMode) {
			elementEditPanel.setVisible(true);
		} else {
			elementEditPanel.setVisible(false);
		}
	}

	public HasClickHandlers getClickForElevationButton() {
		return clickForElevationButton;
	}

	public HasClickHandlers getDrawXSectionButton() {
		return drawXSectionButton;
	}

	public HasClickHandlers getAddButton() {
		return addElementButton;
	}

	public HasClickHandlers getDeleteButton() {
		return deleteElementButton;
	}

}
