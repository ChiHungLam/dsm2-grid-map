package gov.ca.bdo.modeling.dsm2.map.client.display;

import gov.ca.bdo.modeling.dsm2.map.client.map.ElementType;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class MapControlPanel extends Composite {

	private static MapControlPanelUiBinder uiBinder = GWT
			.create(MapControlPanelUiBinder.class);

	interface MapControlPanelUiBinder extends UiBinder<Widget, MapControlPanel> {
	}

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
	private HashMap<String, Integer> mapTypeToId;

	public MapControlPanel(boolean viewOnly) {
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
		saveEditButton.setEnabled(!viewOnly);
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


	public HasClickHandlers getSaveEditButton() {
		return saveEditButton;
	}

}