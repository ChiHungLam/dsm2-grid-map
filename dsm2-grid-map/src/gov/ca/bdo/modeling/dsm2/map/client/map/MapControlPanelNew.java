package gov.ca.bdo.modeling.dsm2.map.client.map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class MapControlPanelNew extends Composite {

	private static MapControlPanelNewUiBinder uiBinder = GWT
			.create(MapControlPanelNewUiBinder.class);

	interface MapControlPanelNewUiBinder extends
			UiBinder<Widget, MapControlPanelNew> {
	}

	@UiField
	Button button;

	public MapControlPanelNew(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));
		button.setText(firstName);
	}

	@UiHandler("button")
	void onClick(ClickEvent e) {
		Window.alert("Hello!");
	}

}
