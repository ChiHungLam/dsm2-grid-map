package gov.ca.bdo.modeling.dsm2.map.client;


import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.maps.utility.client.DefaultPackage;
import com.google.gwt.maps.utility.client.GoogleMapsUtility;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.AnnotatedTimeLine;
import com.google.gwt.visualization.client.visualizations.ScatterChart;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GridMap implements EntryPoint {

	private MapPanel mapPanel;
	private UserProfilePanel userProfilePanel;
	private String currentStudy = null;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		History.addValueChangeHandler(new ValueChangeHandler<String>() {

			public void onValueChange(ValueChangeEvent<String> event) {
				String historyToken = event.getValue();
				if (historyToken.startsWith("user")) {
					showUserProfilePanel();
				} else {
					if (historyToken.startsWith("study")) {
						String[] fields = historyToken.split("=");
						if (fields.length == 2) {
							currentStudy = fields[1];
						}
					}
					showMapPanel();
				}
			}

		});
		History.fireCurrentHistoryState();
	}

	protected void showUserProfilePanel() {
		if (userProfilePanel == null) {
			userProfilePanel = new UserProfilePanel();
		}
		replaceWidget(userProfilePanel);
	}

	protected void replaceWidget(Widget newWidget) {
		RootPanel rootPanel = RootPanel.get("mapcontainer");
		if (rootPanel.getWidgetCount() > 0) {
			Widget widget = rootPanel.getWidget(0);
			if (widget != null) {
				rootPanel.remove(widget);
			}
		}
		rootPanel.add(newWidget);
	}

	protected void showMapPanel() {
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
				if (mapPanel == null) {
					mapPanel = new MapPanel();
				}
				if (currentStudy != null) {
					mapPanel.setStudy(currentStudy);
				}
				replaceWidget(mapPanel);
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
		Runnable visualizationLoadCallback = new Runnable() {
			public void run() {
				Window.setStatus("visualization api loaded");
			}
		};
		// Load the visualization api, passing the onLoadCallback to be called
		// when loading is done.
		VisualizationUtils.loadVisualizationApi(visualizationLoadCallback,
				ScatterChart.PACKAGE, AnnotatedTimeLine.PACKAGE);
	}
}
