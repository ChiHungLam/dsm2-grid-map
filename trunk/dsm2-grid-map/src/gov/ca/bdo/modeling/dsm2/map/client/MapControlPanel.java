package gov.ca.bdo.modeling.dsm2.map.client;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;

public class MapControlPanel extends Composite {

	private final MapPanel mapPanel;
	private final ListBox studyBox;
	private final CheckBox channelHideBox;

	public MapControlPanel(MapPanel panel) {
		mapPanel = panel;
		FlexTable containerPanel = new FlexTable();
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
		final CheckBox nodeHideBox = new CheckBox("Hide Nodes", false);
		nodeHideBox.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				mapPanel.hideMarkers(nodeHideBox.getValue());
			}
		});
		channelHideBox = new CheckBox("Hide Channels");
		channelHideBox.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				mapPanel.hideChannelLines(channelHideBox.getValue());
			}
		});
		//
		final CheckBox outputMarkerHideBox = new CheckBox("Hide Output Markers");
		outputMarkerHideBox.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				mapPanel.hideOutputMarkers(outputMarkerHideBox.getValue());
			}
		});
		//
		final CheckBox bathymetryHideBox = new CheckBox("Show Bathymetry");
		bathymetryHideBox.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				mapPanel.showBathymetry(bathymetryHideBox.getValue());
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
		final ToggleButton addPolylineButton = new ToggleButton(
				"Start measuring length", "Stop measuring length");
		final Label measuringPolylineLengthLabel = new Label("");
		addPolylineButton.addClickHandler(new ClickHandler() {
			private MeasuringDistanceAlongLine measurer;

			public void onClick(ClickEvent event) {
				if (addPolylineButton.isDown()) {
					if (measurer == null) {
						measurer = new MeasuringDistanceAlongLine(mapPanel
								.getMapWidget(), measuringPolylineLengthLabel);
						measurer.addPolyline();
					} else {
						measurer.addPolyline();
					}
				} else {
					if (measurer != null) {
						measurer.clearOverlay();
						measuringPolylineLengthLabel.setText("");
					}

				}

			}
		});
		//
		final ToggleButton addPolygonButton = new ToggleButton(
				"Start measuring area", "Stop measuring area");
		final Label measuringPolygonAreaLabel = new Label("");
		addPolygonButton.addClickHandler(new ClickHandler() {
			private MeasuringAreaInPolygon measurer;

			public void onClick(ClickEvent event) {
				if (addPolygonButton.isDown()) {
					if (measurer == null) {
						measurer = new MeasuringAreaInPolygon(mapPanel
								.getMapWidget(), measuringPolygonAreaLabel);
						measurer.addPolyline();
					} else {
						measurer.addPolyline();
					}
				} else {
					if (measurer != null) {
						measurer.clearOverlay();
						measuringPolygonAreaLabel.setText("");
					}

				}

			}
		});
		//
		Label channelColorLabel = new Label("Color Channels By: ");
		final ListBox channelColorOptions = new ListBox();
		channelColorOptions.addItem(MapPanel.CHANNEL_COLOR_PLAIN);
		channelColorOptions.addItem(MapPanel.CHANNEL_COLOR_MANNINGS);
		channelColorOptions.addItem(MapPanel.CHANNEL_COLOR_DISPERSION);
		channelColorOptions.addChangeHandler(new ChangeHandler() {

			public void onChange(ChangeEvent event) {
				mapPanel.setChannelColorScheme(channelColorOptions
						.getItemText(channelColorOptions.getSelectedIndex()));
			}
		});
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
				"Start adding text", "Stop adding text");
		addTextAnnotationButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				if (addTextAnnotationButton.isDown()) {
					mapPanel.turnOnTextAnnotation();
				} else {
					mapPanel.turnOffTextAnnotation();
				}
			}
		});
		//
		Button showHydroInputButton = new Button("Show Input");
		final TextArea hydroInputArea = new TextArea();
		hydroInputArea.setCharacterWidth(72);
		final TextArea gisInputArea = new TextArea();
		gisInputArea.setCharacterWidth(72);
		showHydroInputButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				mapPanel.displayHydroInput(hydroInputArea);
				mapPanel.displayGisInput(gisInputArea);
			}
		});

		Anchor uploadStudyLink = new Anchor("Upload study here",
				"/upload_study.html");
		Anchor uploadStudyDataLink = new Anchor("Upload study data here",
				"/upload_data.html");
		containerPanel.setWidget(0, 0, studyLabel);
		containerPanel.setWidget(0, 1, studyBox);
		containerPanel.setWidget(0, 2, saveEditModelButton);
		containerPanel.setWidget(1, 0, findNodeLabel);
		containerPanel.setWidget(1, 1, findNodeBox);
		containerPanel.setWidget(1, 2, nodeHideBox);
		containerPanel.setWidget(2, 0, findChannelLabel);
		containerPanel.setWidget(2, 1, findChannelBox);
		containerPanel.setWidget(2, 2, channelHideBox);
		containerPanel.setWidget(3, 2, outputMarkerHideBox);
		containerPanel.setWidget(4, 2, bathymetryHideBox);

		containerPanel.setWidget(3, 0, addPolylineButton);
		containerPanel.setWidget(3, 1, measuringPolylineLengthLabel);
		containerPanel.setWidget(4, 0, addPolygonButton);
		containerPanel.setWidget(4, 1, measuringPolygonAreaLabel);

		containerPanel.setWidget(5, 0, channelColorLabel);
		containerPanel.setWidget(5, 1, channelColorOptions);
		containerPanel.setWidget(6, 0, addTextAnnotationButton);
		containerPanel.setWidget(7, 0, showHydroInputButton);
		containerPanel.setWidget(8, 0, hydroInputArea);
		containerPanel.setWidget(9, 0, gisInputArea);

		containerPanel.setWidget(10, 0, uploadStudyLink);
		containerPanel.setWidget(11, 0, uploadStudyDataLink);
		containerPanel.getFlexCellFormatter().setColSpan(8, 0, 3);
		containerPanel.getFlexCellFormatter().setColSpan(9, 0, 3);
		initWidget(containerPanel);
	}

	public void setStudies(String[] studyNames) {
		for (String studyName : studyNames) {
			studyBox.addItem(studyName, studyName);
		}
		studyBox.setSelectedIndex(0);
	}

	public boolean getHideChannels() {
		return channelHideBox.getValue();
	}
}
