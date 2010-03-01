package gov.ca.bdo.modeling.dsm2.map.client.map;

import gov.ca.bdo.modeling.dsm2.map.client.HeaderPanel;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.maps.utility.client.DefaultPackage;
import com.google.gwt.maps.utility.client.GoogleMapsUtility;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootLayoutPanel;

public class MapUI {
	private HeaderPanel headerPanel;
	private MapPanel mapPanel;
	private DockLayoutPanel mainPanel;

	public void bind() {
		createUI();
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				loadMaps();
			}
		});
	}

	public void unBind() {

	}

	public void loadMaps() {
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
				mapPanel = new MapPanel(headerPanel);
				mainPanel.addEast(mapPanel.getControlPanelContainer(), 40);
				mainPanel.add(mapPanel);
				RootLayoutPanel.get().animate(0, new AnimationCallback() {
					public void onLayout(Layer layer, double progress) {
					}

					public void onAnimationComplete() {
						mapPanel.onResize();
					}
				});
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
		mainPanel = new DockLayoutPanel(Unit.EM);
		headerPanel = new HeaderPanel();
		headerPanel.showMessage(true, "Loading...");
		mainPanel.addNorth(headerPanel, 2);
		mainPanel.addSouth(new HTML(""), 1);
		RootLayoutPanel.get().clear();
		RootLayoutPanel.get().add(mainPanel);
	}

}
