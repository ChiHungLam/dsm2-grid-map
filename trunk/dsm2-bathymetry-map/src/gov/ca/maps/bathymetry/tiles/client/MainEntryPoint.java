package gov.ca.maps.bathymetry.tiles.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootLayoutPanel;

public class MainEntryPoint implements EntryPoint {

	private DockLayoutPanel mainPanel;
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
		mapLoadCallback.run();

	}

	protected void createUI() {
		mainPanel = new DockLayoutPanel(Unit.EM);
		mainPanel
				.addNorth(
						new HTML(
								"<h3>Bathymetry Data for the Sacramento-San Jaoquin Delta</h3>"),
						5);
		Grid legend = new Grid(8, 2);
		legend.setStyleName("legend");
		String[] legendColors = new String[] { "black", "blue", "cyan",
				"green", "yellow", "orange", "red" };
		String[] legendDepth = new String[] { "< -100", "-40", "-30", "-15",
				"-10", "-5", "> 0" };
		for (int i = 0; i < legendColors.length; i++) {
			legend.setHTML(i, 0, "&nbsp;");
			legend.getCellFormatter().setWidth(i, 0, "1em");
			legend.getCellFormatter().getElement(i, 0).setAttribute("bgcolor",
					legendColors[i]);
			legend.setHTML(i, 1, legendDepth[i]);
		}
		FlowPanel sidePanel = new FlowPanel();
		sidePanel.add(new HTML(
				"<p> Transparency is mapped from 1925 to 2010</p>"));
		sidePanel
				.add(new HTML(
						"<p> The color scale below defines how depth is represented on the map. </p>"));
		sidePanel.add(legend);
		mainPanel.addEast(sidePanel, 10);
		mainPanel.add(mapPanel = new MapPanel());
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
