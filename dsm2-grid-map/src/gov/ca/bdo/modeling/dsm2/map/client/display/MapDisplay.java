package gov.ca.bdo.modeling.dsm2.map.client.display;

import gov.ca.bdo.modeling.dsm2.map.client.HeaderPanel;
import gov.ca.bdo.modeling.dsm2.map.client.map.MapControlPanel;
import gov.ca.bdo.modeling.dsm2.map.client.map.MapPanel;
import gov.ca.bdo.modeling.dsm2.map.client.map.MeasuringAreaInPolygon;
import gov.ca.bdo.modeling.dsm2.map.client.map.MeasuringDistanceAlongLine;
import gov.ca.bdo.modeling.dsm2.map.client.presenter.DSM2GridMapPresenter.Display;
import gov.ca.dsm2.input.model.DSM2Model;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.HasInitializeHandlers;
import com.google.gwt.event.logical.shared.InitializeEvent;
import com.google.gwt.event.logical.shared.InitializeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.maps.client.overlay.GeoXmlLoadCallback;
import com.google.gwt.maps.client.overlay.GeoXmlOverlay;
import com.google.gwt.maps.utility.client.DefaultPackage;
import com.google.gwt.maps.utility.client.GoogleMapsUtility;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class MapDisplay extends Composite implements Display,
		HasInitializeHandlers {
	private HeaderPanel headerPanel;
	private MapPanel mapPanel;
	private DockLayoutPanel mainPanel;
	private String studyName = null;
	private final MapControlPanel controlPanel;
	private final FlowPanel infoPanel;
	private final VerticalPanel controlPanelContainer;
	private MeasuringDistanceAlongLine lengthMeasurer;
	private MeasuringAreaInPolygon areaMeasurer;
	private DSM2Model model;

	public MapDisplay(boolean viewOnly) {
		mainPanel = new DockLayoutPanel(Unit.EM);
		headerPanel = new HeaderPanel();
		headerPanel.showMessage(true, "Loading...");
		// layout top level things here
		controlPanel = new MapControlPanel(viewOnly);
		infoPanel = new FlowPanel();
		infoPanel.setStyleName("infoPanel");

		controlPanelContainer = new VerticalPanel();
		controlPanelContainer.add(controlPanel);
		controlPanelContainer.add(infoPanel);
		mainPanel.addWest(new FlowPanel(), 5);
		mainPanel.addEast(new FlowPanel(), 5);
		mainPanel.addNorth(headerPanel, 2);
		mainPanel.addSouth(new HTML(""), 1);
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				loadMaps();
			}
		});
		initWidget(mainPanel);
	}

	public Widget asWidget() {
		return this;
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
				mapPanel.setInfoPanel(infoPanel);
				mainPanel.addEast(controlPanelContainer, 40);
				mainPanel.add(mapPanel);
				if (studyName != null) {
					mapPanel.setStudy(studyName);
				}
				if (model != null) {
					mapPanel.setModel(model);
					refresh();
				}

				InitializeEvent.fire(MapDisplay.this);
				RootLayoutPanel.get().animate(0, new AnimationCallback() {
					public void onLayout(Layer layer, double progress) {
					}

					public void onAnimationComplete() {
						mapPanel.onResize();
					}
				});
				controlPanel.getNodeIdBox().addValueChangeHandler(
						new ValueChangeHandler<String>() {

							public void onValueChange(
									ValueChangeEvent<String> event) {
								String nodeId = event.getValue();
								mapPanel.centerAndZoomOnNode(nodeId);
							}
						});
				controlPanel.getChannelIdBox().addValueChangeHandler(
						new ValueChangeHandler<String>() {

							public void onValueChange(
									ValueChangeEvent<String> event) {
								String channelId = event.getValue();
								mapPanel.centerAndZoomOnChannel(channelId);
							}
						});
				getSaveEditButton().addClickHandler(new ClickHandler() {

					public void onClick(ClickEvent event) {
						setEditMode(((ToggleButton) getSaveEditButton())
								.isDown());
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

	public void setCurrentStudy(String studyName) {
		if (mapPanel != null) {
			mapPanel.setStudy(studyName);
			controlPanel.setStudy(studyName);
			studyName = null;
		} else {
			this.studyName = studyName;
		}
	}

	public void setStudies(String[] studyNames) {
		headerPanel.clearMessages();
		controlPanel.setStudies(studyNames);
		if (studyNames.length > 0) {
			setCurrentStudy(studyNames[0]);
		}
	}

	public void clearMessages() {
		headerPanel.clearMessages();
	}

	public DSM2Model getModel() {
		return mapPanel.getModel();
	}

	public void refresh() {
		mapPanel.populateGrid();
	}

	public void setModel(DSM2Model result) {
		model = result;
		if (mapPanel != null) {
			mapPanel.setModel(result);
		}
	}

	public void showError(String message) {
		headerPanel.showError(true, message);
	}

	public void showMessage(String message) {
		headerPanel.showError(true, message);
	}

	public void showMessageFor(String message, int delayInMillisecs) {
		headerPanel.showMessageFor(message, delayInMillisecs);
	}

	public HasChangeHandlers onStudyChange() {
		return controlPanel.getStudyBox();
	}

	public String getCurrentStudy() {
		return controlPanel.getStudyChoice();
	}

	public String[] getStudies() {
		return controlPanel.getStudies();
	}

	public HandlerRegistration addInitializeHandler(
			InitializeHandler initializeHandler) {
		return this.addHandler(initializeHandler, InitializeEvent.getType());
	}

	public boolean isInEditMode() {
		return mapPanel.isInEditMode();
	}

	public void setEditMode(boolean editMode) {
		mapPanel.setEditMode(editMode);
		controlPanel.setEditMode(editMode);
	}

	public HasClickHandlers getSaveEditButton() {
		return controlPanel.getSaveEditModelButton();
	}

	public HasClickHandlers getAddKmlButton() {
		return controlPanel.getAddKmlButton();
	}

	public HasText getKmlUrlBox() {
		return controlPanel.getKmlUrlBox();
	}

	public void updateLinks() {
		controlPanel.updateLinks();
	}

	public HasClickHandlers getAddPolylineButton() {
		return controlPanel.getAddPolylineButton();
	}

	public HasClickHandlers getAddPolygonButton() {
		return controlPanel.getAddPolygonButton();
	}

	public HasClickHandlers getTextAnnotationButton() {
		return controlPanel.getAddTextAnnonationButton();
	}

	public void startMeasuringDistanceAlongLine() {
		if (lengthMeasurer == null) {
			lengthMeasurer = new MeasuringDistanceAlongLine(mapPanel
					.getMapWidget(), controlPanel.getMeasurementLabel());
			lengthMeasurer.addPolyline();
		}
	}

	public void stopMeasuringDistanceAlongLine() {
		if (lengthMeasurer != null) {
			lengthMeasurer.clearOverlay();
			controlPanel.getMeasurementLabel().setText("");
			lengthMeasurer = null;
		}
	}

	public void startMeasuringAreaInPolygon() {
		if (areaMeasurer == null) {
			areaMeasurer = new MeasuringAreaInPolygon(mapPanel.getMap(),
					controlPanel.getMeasurementLabel());
			areaMeasurer.addPolyline();
		}
	}

	public void stopMeasuringAreaInPolygon() {
		if (areaMeasurer != null) {
			areaMeasurer.clearOverlay();
			controlPanel.getMeasurementLabel().setText("");
			areaMeasurer = null;
		}
	}

	public void turnOnTextAnnotation() {
		mapPanel.turnOnTextAnnotation();
	}

	public void turnOffTextAnnotation() {
		mapPanel.turnOffTextAnnotation();
	}

	public void addKmlOverlay(final String url) {
		GeoXmlOverlay.load(url, new GeoXmlLoadCallback() {

			@Override
			public void onFailure(String url, Throwable e) {
				StringBuffer message = new StringBuffer("KML File " + url
						+ " failed to load");
				if (e != null) {
					message.append(e.toString());
				}
				Window.alert(message.toString());
			}

			@Override
			public void onSuccess(String url, GeoXmlOverlay overlay) {
				if (overlay == null) {
					return;
				}
				GWT.log("KML File " + url + " loaded successfully", null);
				mapPanel.getMap().addOverlay(overlay);
			}
		});
	}

	public HasClickHandlers getFlowLineButton() {
		return controlPanel.getFlowLineButton();
	}

	public void hideFlowLines() {
		mapPanel.hideFlowLines();
	}

	public void showFlowLines() {
		mapPanel.showFlowLines();
	}

}
