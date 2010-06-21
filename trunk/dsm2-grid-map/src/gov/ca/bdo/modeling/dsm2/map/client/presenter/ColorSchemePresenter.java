package gov.ca.bdo.modeling.dsm2.map.client.presenter;

import java.util.HashMap;

import com.google.gwt.user.client.ui.Widget;

public class ColorSchemePresenter {
	public interface Display {
		public int SCHEME_DIVERGING = 100;
		public int SCHEME_QUALITATIVE = 200;
		public int SCHEME_SEQUENTIAL = 300;

		public Widget asWidget();

		public void setValueRange(double max, double min);

		public void setChannelValue(String id, double value);

		public void setColorScheme(int schemeDiverging);
	}

	private Display display;

	public ColorSchemePresenter(Display display) {
		this.display = display;
	}

	public void setChannelColors(HashMap<String, Double> channelIdToValueMap) {
		double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
		for (Double value : channelIdToValueMap.values()) {
			min = Math.min(value, min);
			max = Math.max(value, max);
		}
		display.setValueRange(max, min);
		display.setColorScheme(Display.SCHEME_DIVERGING);
		for (String id : channelIdToValueMap.keySet()) {
			display.setChannelValue(id, channelIdToValueMap.get(id));
		}
	}
}
