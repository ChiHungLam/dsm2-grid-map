package gov.ca.bdo.modeling.dsm2.map.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.maps.utility.client.DefaultPackage;
import com.google.gwt.maps.utility.client.GoogleMapsUtility;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;

public class MainEntryPoint implements EntryPoint {

	private SplitLayoutPanel mainPanel;
	private MapPanel mapPanel;

	public void onModuleLoad() {
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
		if (!GoogleMapsUtility.isLoaded(DefaultPackage.MARKER_CLUSTERER,
				DefaultPackage.LABELED_MARKER, DefaultPackage.MAP_ICON_MAKER)) {
			GoogleMapsUtility.loadUtilityApi(mapLoadCallback,
					DefaultPackage.MARKER_CLUSTERER,
					DefaultPackage.LABELED_MARKER,
					DefaultPackage.MAP_ICON_MAKER);
		} else {
			mapLoadCallback.run();
		}
	}

	protected void createUI() {
		mainPanel = new SplitLayoutPanel();
		mapPanel = new MapPanel();
		mainPanel.addNorth(new HeaderPanel(), 72);
		mainPanel.addEast(mapPanel.getControlPanelContainer(), 646);
		mainPanel.addSouth(new HTML(""), 36);
		mainPanel.add(mapPanel);
		RootLayoutPanel.get().add(mainPanel);
		RootLayoutPanel.get().animate(0, new AnimationCallback() {
			public void onLayout(Layer layer, double progress) {
			}

			public void onAnimationComplete() {
				mapPanel.onResize();
			}
		});
	}
}
