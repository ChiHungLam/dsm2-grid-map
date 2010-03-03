package gov.ca.bdo.modeling.dsm2.map.client.display;

import gov.ca.bdo.modeling.dsm2.map.client.HeaderPanel;
import gov.ca.bdo.modeling.dsm2.map.client.map.MapControlPanel;
import gov.ca.bdo.modeling.dsm2.map.client.map.MapPanel;
import gov.ca.bdo.modeling.dsm2.map.client.presenter.DSM2GridMapViewPresenter.Display;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.maps.utility.client.DefaultPackage;
import com.google.gwt.maps.utility.client.GoogleMapsUtility;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class MapViewDisplay extends Composite implements Display {

	private HeaderPanel headerPanel;
	private MapPanel mapPanel;
	private DockLayoutPanel mainPanel;
	private String studyName = null;
	private MapControlPanel controlPanel;
	private FlowPanel infoPanel;
	private VerticalPanel controlPanelContainer;

	public MapViewDisplay() {
		mainPanel = new DockLayoutPanel(Unit.EM);
		headerPanel = new HeaderPanel();
		headerPanel.showMessage(true, "Loading...");
		mainPanel.addNorth(headerPanel, 2);
		mainPanel.addSouth(new HTML(""), 1);
		// layout top level things here
		controlPanel = new MapControlPanel();
		infoPanel = new FlowPanel();
		infoPanel.setStyleName("infoPanel");
		infoPanel.setWidth("646px");
		infoPanel.setHeight("400px");
		controlPanelContainer = new VerticalPanel();
		controlPanelContainer.add(controlPanel);
		controlPanelContainer.add(infoPanel);
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				loadMaps();
			}
		});
	}

	public Widget asWidget() {
		return mainPanel;
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
				mapPanel = new MapPanel();
				mainPanel.addEast(controlPanelContainer, 40);
				mainPanel.add(mapPanel);
				if (studyName != null) {
					mapPanel.setStudy(studyName);
				}
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

	public void setStudy(String studyName) {
		if (mapPanel != null) {
			mapPanel.setStudy(studyName);
			studyName = null;
		} else {
			this.studyName = studyName;
		}
	}

	public void showErrorMessage(String message) {
		headerPanel.showError(true, message);
	}

}
