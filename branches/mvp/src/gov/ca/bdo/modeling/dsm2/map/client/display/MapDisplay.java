package gov.ca.bdo.modeling.dsm2.map.client.display;

import gov.ca.bdo.modeling.dsm2.map.client.HeaderPanel;
import gov.ca.bdo.modeling.dsm2.map.client.map.MapControlPanel;
import gov.ca.bdo.modeling.dsm2.map.client.map.MapPanel;
import gov.ca.bdo.modeling.dsm2.map.client.presenter.DSM2GridMapPresenter.Display;
import gov.ca.dsm2.input.model.DSM2Model;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.logical.shared.HasInitializeHandlers;
import com.google.gwt.event.logical.shared.InitializeEvent;
import com.google.gwt.event.logical.shared.InitializeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
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

public class MapDisplay extends Composite implements Display,
		HasInitializeHandlers {
	private HeaderPanel headerPanel;
	private MapPanel mapPanel;
	private DockLayoutPanel mainPanel;
	private String studyName = null;
	private final MapControlPanel controlPanel;
	private final FlowPanel infoPanel;
	private final VerticalPanel controlPanelContainer;

	public MapDisplay() {
		mainPanel = new DockLayoutPanel(Unit.EM);
		headerPanel = new HeaderPanel();
		headerPanel.showMessage(true, "Loading...");
		// layout top level things here
		controlPanel = new MapControlPanel();
		infoPanel = new FlowPanel();
		infoPanel.setStyleName("infoPanel");
		infoPanel.setWidth("646px");
		infoPanel.setHeight("400px");
		controlPanelContainer = new VerticalPanel();
		controlPanelContainer.add(controlPanel);
		controlPanelContainer.add(infoPanel);
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
				/*
				 * controlPanel.getAddPolylineButton().addClickHandler( new
				 * ClickHandler() { private MeasuringDistanceAlongLine measurer;
				 * 
				 * public void onClick(ClickEvent event) { if
				 * (addPolylineButton.isDown()) { if (measurer == null) {
				 * measurer = new MeasuringDistanceAlongLine(
				 * mapPanel.getMapWidget(), measurementLabel);
				 * measurer.addPolyline(); } else { measurer.addPolyline(); } }
				 * else { if (measurer != null) { measurer.clearOverlay();
				 * measurementLabel.setText(""); }
				 * 
				 * }
				 * 
				 * } }); addPolygonButton.addClickHandler(new ClickHandler() {
				 * private MeasuringAreaInPolygon measurer;
				 * 
				 * public void onClick(ClickEvent event) { if
				 * (addPolygonButton.isDown()) { if (measurer == null) {
				 * measurer = new MeasuringAreaInPolygon(mapPanel
				 * .getMapWidget(), measurementLabel); measurer.addPolyline(); }
				 * else { measurer.clearOverlay(); measurementLabel.setText("");
				 * measurer.addPolyline(); } } else { if (measurer != null) {
				 * measurer.clearOverlay(); measurementLabel.setText(""); }
				 * 
				 * }
				 * 
				 * } }); addTextAnnotationButton.addClickHandler(new
				 * ClickHandler() {
				 * 
				 * public void onClick(ClickEvent event) { if
				 * (addTextAnnotationButton.isDown()) {
				 * mapPanel.turnOnTextAnnotation(); } else {
				 * mapPanel.turnOffTextAnnotation(); } } }); colorSchemeHandler
				 * = new ChangeHandler() {
				 * 
				 * public void onChange(ChangeEvent event) {
				 * mapPanel.setChannelColorScheme(channelColorOptions
				 * .getItemText(channelColorOptions .getSelectedIndex()),
				 * colorArraySchemeOptions .getItemText(colorArraySchemeOptions
				 * .getSelectedIndex())); } };
				 * saveEditModelButton.addClickHandler(new ClickHandler() {
				 * 
				 * public void onClick(ClickEvent event) { if
				 * (saveEditModelButton.isDown()) { mapPanel.setEditMode(true);
				 * } else { mapPanel.saveCurrentStudy();
				 * mapPanel.setEditMode(false); } } });
				 */
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
			setStudy(studyNames[0]);
		}
	}

	public void clearMessages() {
		headerPanel.clearMessages();
	}

	public DSM2Model getModel() {
		return mapPanel.getModel();
	}

	public void populateGrid() {
		mapPanel.populateGrid();
	}

	public void setModel(DSM2Model result) {
		mapPanel.setModel(result);
	}

	public void showError(String message) {
		headerPanel.showError(true, message);
	}

	public void showMessage(String message) {
		headerPanel.showError(true, message);
	}

	public HasChangeHandlers getStudyBox() {
		return controlPanel.getStudyBox();
	}

	public String getStudyChoice() {
		return controlPanel.getStudyChoice();
	}

	public String[] getStudies() {
		return controlPanel.getStudies();
	}

	public HandlerRegistration addInitializeHandler(
			InitializeHandler initializeHandler) {
		return this.addHandler(initializeHandler, InitializeEvent.getType());
	}

}
