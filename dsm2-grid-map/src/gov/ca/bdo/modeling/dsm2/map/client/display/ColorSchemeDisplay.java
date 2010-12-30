package gov.ca.bdo.modeling.dsm2.map.client.display;

import gov.ca.bdo.modeling.dsm2.map.client.map.ChannelLineDataManager;
import gov.ca.dsm2.input.model.Channel;
import gov.ca.dsm2.input.model.Channels;

import java.util.HashMap;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.maps.client.overlay.PolyStyleOptions;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class ColorSchemeDisplay extends MapDisplay {

	public static interface ColorMapper {
		public String convertValueToColor(double value);
	}

	private FlowPanel controlPanel;
	private FlowPanel infoPanel;

	public ColorSchemeDisplay(ContainerDisplay display, EventBus eventBus) {
		super(display, true, eventBus);
		mapPanel.getGridVisibility().getHideNodes().setValue(true);
		mapPanel.getGridVisibility().getHideReservoirs().setValue(true);
		infoPanel = new FlowPanel();
	}

	@Override
	protected void initializeUI() {
		super.initializeUI();
		// layout top level things here
		mapPanel.setInfoPanel(infoPanel);
	}

	@Override
	public Widget getSidePanel() {
		if (controlPanel == null) {
			controlPanel = new FlowPanel();
		}
		return controlPanel;
	}

	public void colorizeChannels(HashMap<String, Double> channelIdToValueMap,
			ColorMapper colorMapper) {
		ChannelLineDataManager channelManager = mapPanel.getChannelManager();
		for (String id : channelIdToValueMap.keySet()) {
			Double value = channelIdToValueMap.get(id);
			String colorSpec = colorMapper.convertValueToColor(value);
			channelManager.getPolyline(id).setStrokeStyle(
					PolyStyleOptions.newInstance(colorSpec));
		}
	}

	public HashMap<String, Double> getManningsMap() {
		Channels channels = mapPanel.getChannelManager().getChannels();
		HashMap<String, Double> map = new HashMap<String, Double>();
		for (Channel c : channels.getChannels()) {
			map.put(c.getId(), c.getMannings());
		}
		return map;
	}

	public HashMap<String, Double> getDispersionMap() {
		Channels channels = mapPanel.getChannelManager().getChannels();
		HashMap<String, Double> map = new HashMap<String, Double>();
		for (Channel c : channels.getChannels()) {
			map.put(c.getId(), c.getDispersion());
		}
		return map;
	}
}