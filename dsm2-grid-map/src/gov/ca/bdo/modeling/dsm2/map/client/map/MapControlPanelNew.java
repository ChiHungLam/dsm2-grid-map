package gov.ca.bdo.modeling.dsm2.map.client.map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class MapControlPanelNew extends Composite {

	private static MapControlPanelNewUiBinder uiBinder = GWT
			.create(MapControlPanelNewUiBinder.class);

	interface MapControlPanelNewUiBinder extends
			UiBinder<Widget, MapControlPanelNew> {
	}

	private boolean viewOnly;
	@UiField
	ListBox studyBox;
	@UiField
	ToggleButton saveEditButton;
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
	Panel infoPanel;

	public MapControlPanelNew(boolean viewOnly) {
		this.viewOnly = viewOnly;
		initWidget(uiBinder.createAndBindUi(this));
		String[] elementTypes = new String[]{"Node", "Channel", "Reservoir", "Gate", "XSection", "Output", "KML"};
		for(String item: elementTypes){
			elementTypeBox.addItem(item);			
		}
	}

}
