package gov.ca.cdec.maps.client;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

public class MapControlPanel extends Composite {
	private MapPanel mapPanel;
	private ListBox sensorDescriptionsBox;

	public MapControlPanel(MapPanel mapPanel) {
		this.mapPanel = mapPanel;
		FlexTable table = new FlexTable();
		sensorDescriptionsBox = new ListBox(false);
		sensorDescriptionsBox.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				int index = sensorDescriptionsBox.getSelectedIndex();
				String sensorSelected = sensorDescriptionsBox
						.getItemText(index);
				MapControlPanel.this.mapPanel
						.setSensorDescription(sensorSelected);
			}
		});
		table.setWidget(0, 0, new Label(
				"Choose sensor type to filter markers: "));
		table.setWidget(0, 1, sensorDescriptionsBox);
		initWidget(table);
	}

	public void refreshSensorDescriptions() {
		String[] sensorDescriptions = mapPanel.getSensorDescriptions();
		for (int i = 0; i < sensorDescriptions.length; i++) {
			sensorDescriptionsBox.addItem(sensorDescriptions[i],
					sensorDescriptions[i]);
		}
	}
}
