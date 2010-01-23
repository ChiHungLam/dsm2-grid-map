package gov.ca.modeling.dsm2.widgets.test.client;

import gov.ca.modeling.dsm2.widgets.client.ExpandContractMapControl;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

public class TestExpandContractMapControl implements EntryPoint {

	public void onModuleLoad() {
		Element bodyElement = RootPanel.getBodyElement();
		bodyElement.setInnerHTML("<div></div>");
		if (!Maps.isLoaded()) {
			Window
					.alert("The Maps API is not installed."
							+ "  The <script> tag that loads the Maps API may be missing or your Maps key may be wrong.");
			return;
		}

		if (!Maps.isBrowserCompatible()) {
			Window.alert("The Maps API is not compatible with this browser.");
			return;
		}
		Runnable mapLoadCallback = new Runnable() {

			public void run() {
				createUI();
			}
		};
		mapLoadCallback.run();
	}

	void createUI() {
		MapWidget map = new MapWidget(LatLng.newInstance(38.15, -121.70), 10);
		RootPanel.get().add(map);
		ExpandContractMapControl fullScreenControl = new ExpandContractMapControl();
		map.addControl(fullScreenControl);

	}

}
