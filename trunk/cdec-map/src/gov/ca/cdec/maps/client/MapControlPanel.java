package gov.ca.cdec.maps.client;

import java.util.Arrays;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

public class MapControlPanel extends Composite {
	private MapPanel mapPanel;
	private ListBox sensorDescriptionsBox;

	public MapControlPanel(MapPanel mapPanel) {
		this.mapPanel = mapPanel;
		FlowPanel panel = new FlowPanel();
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
		panel.add(new Label(
				"Choose sensor type to filter markers: "));
		panel.add(sensorDescriptionsBox);
		initWidget(panel);
	}

	public void refreshSensorDescriptions() {
		String[] sensorDescriptions = mapPanel.getSensorDescriptions();
		Arrays.sort(sensorDescriptions);
		sensorDescriptionsBox.addItem("ALL");
		for (int i = 0; i < sensorDescriptions.length; i++) {
			sensorDescriptionsBox.addItem(sensorDescriptions[i],
					sensorDescriptions[i]);
		}
	}
}
