package gov.ca.bdo.modeling.dsm2.map.client.map;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class MapControlPanel extends Composite {

	private static MapControlPanelUiBinder uiBinder = GWT
			.create(MapControlPanelUiBinder.class);

	interface MapControlPanelUiBinder extends UiBinder<Widget, MapControlPanel> {
	}

	private boolean viewOnly;
	@UiField
	ListBox studyBox;
	@UiField
	ToggleButton saveEditButton;
	@UiField
	CaptionPanel elementEditPanel;
	@UiField
	ToggleButton addButton;
	@UiField
	ToggleButton deleteButton;
	@UiField
	ListBox elementTypeBox;
	@UiField
	PushButton downloadHydroButton;
	@UiField
	PushButton downloadGISButton;
	@UiField
	ToggleButton measureLengthButton;
	@UiField
	ToggleButton measureAreaButton;
	@UiField
	ToggleButton displayElevationButton;
	@UiField
	ToggleButton displayElevationProfileButton;
	@UiField
	PushButton findButton;
	@UiField
	TextBox findTextBox;
	@UiField
	ToggleButton flowlineButton;

	private String[] studies;
	private HashMap<String, Integer> mapTypeToId;

	public MapControlPanel(boolean viewOnly) {
		this.viewOnly = viewOnly;
		initWidget(uiBinder.createAndBindUi(this));
		mapTypeToId = new HashMap<String, Integer>();
		mapTypeToId.put("Node", ElementType.NODE);
		mapTypeToId.put("Channel", ElementType.CHANNEL);
		mapTypeToId.put("Reservoir", ElementType.RESERVOIR);
		mapTypeToId.put("Gate", ElementType.GATE);
		mapTypeToId.put("XSection", ElementType.XSECTION);
		mapTypeToId.put("Output", ElementType.OUTPUT);
		mapTypeToId.put("Text", ElementType.TEXT);
		mapTypeToId.put("KML", ElementType.KML);
		String[] elementTypes = new String[] { "Node", "Channel", "Reservoir",
				"Gate", "XSection", "Output", "Text", "KML" };
		for (String item : elementTypes) {
			elementTypeBox.addItem(item);
		}
		elementEditPanel.setVisible(false);
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

	public HasText getFindTextBox() {
		return findTextBox;
	}

	public HasClickHandlers getFindButton() {
		return findButton;
	}

	public PushButton getDownloadHydroButton() {
		return downloadHydroButton;
	}

	public PushButton getDownloadGISButton() {
		return downloadGISButton;
	}

	public HasClickHandlers getMeasureAreaButton() {
		return measureAreaButton;
	}

	public HasClickHandlers getMeasureLengthButton() {
		return measureLengthButton;
	}

	public HasClickHandlers getSaveEditButton() {
		return saveEditButton;
	}

	public void setStudy(String studyName) {
		int count = studyBox.getItemCount();
		for (int i = 0; i < count; i++) {
			if (studyName.equals(studyBox.getItemText(i))) {
				studyBox.setSelectedIndex(i);
				break;
			}
		}
	}

	public String[] getStudies() {
		return studies;
	}

	public HasClickHandlers getFlowLineButton() {
		return flowlineButton;
	}

	public void setEditMode(boolean editMode) {
		elementEditPanel.setVisible(editMode);
	}

	public HasClickHandlers getDisplayElevationButton() {
		return displayElevationButton;
	}

	public HasClickHandlers getDisplayElevationProfileButton() {
		return displayElevationProfileButton;
	}

	public HasClickHandlers getAddButton() {
		return addButton;
	}

	public HasClickHandlers getDeleteButton() {
		return deleteButton;
	}

	/**
	 * Returns the type of element to be added
	 */
	public int getAddTypeSelected() {
		return mapTypeToId.get(
				elementTypeBox.getItemText(elementTypeBox.getSelectedIndex()))
				.intValue();
	}

}
