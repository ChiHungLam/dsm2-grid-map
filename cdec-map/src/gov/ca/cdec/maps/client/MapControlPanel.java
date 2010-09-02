package gov.ca.cdec.maps.client;

import java.util.Arrays;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class MapControlPanel extends Composite {
	private MapPanel mapPanel;
	private ListBox sensorDescriptionsBox;
	private SuggestBox sensorSuggestBox;
	private MultiWordSuggestOracle suggestOracle;

	public MapControlPanel(MapPanel mapPanel) {
		this.mapPanel = mapPanel;
		HorizontalPanel panel = new HorizontalPanel();
		suggestOracle = new MultiWordSuggestOracle(" ,");
		sensorSuggestBox = new SuggestBox(suggestOracle);
		sensorSuggestBox
				.addSelectionHandler(new SelectionHandler<Suggestion>() {

					@Override
					public void onSelection(SelectionEvent<Suggestion> event) {
						String sensorSelected = event.getSelectedItem()
								.getReplacementString();
						MapControlPanel.this.mapPanel
								.setSensorDescription(sensorSelected);
					}
				});
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
		CaptionPanel sensorDescPanel = new CaptionPanel(
				"Select type of data from list box");
		sensorDescPanel.add(sensorDescriptionsBox);
		CaptionPanel sensorSuggestPanel = new CaptionPanel(
				"Type in words to search sensor type description");
		sensorSuggestPanel.add(sensorSuggestBox);
		panel.add(sensorDescPanel);
		panel.add(sensorSuggestPanel);
		initWidget(panel);
	}

	public void refreshSensorDescriptions() {
		String[] sensorDescriptions = mapPanel.getSensorDescriptions();
		Arrays.sort(sensorDescriptions);
		suggestOracle.clear();
		suggestOracle.addAll(Arrays.asList(sensorDescriptions));
		for (String sensorDescription : sensorDescriptions) {
			sensorDescriptionsBox.addItem(sensorDescription, sensorDescription);
		}
		sensorDescriptionsBox.addItem("ALL");
		mapPanel.setSensorDescription(sensorDescriptionsBox
				.getItemText(sensorDescriptionsBox.getSelectedIndex()));
	}
}
