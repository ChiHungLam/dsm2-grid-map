/*******************************************************************************
 *     Copyright (C) 2009, 2010 Nicky Sandhu, State of California, Department of Water Resources.
 *
 *     DSM2 Grid Map : An online map centric tool to visualize, create and modify 
 *                               DSM2 input and output 
 *     Version 1.0
 *     by Nicky Sandhu
 *     California Dept. of Water Resources
 *     Modeling Support Branch
 *     1416 Ninth Street
 *     Sacramento, CA 95814
 *     psandhu@water.ca.gov
 *
 *     Send bug reports to psandhu@water.ca.gov
 *
 *     This file is part of DSM2 Grid Map
 *     The DSM2 Grid Map is free software and is licensed to you under the terms of the GNU 
 *     General Public License, version 3, as published by the Free Software Foundation.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program; if not, contact the 
 *     Free Software Foundation, 675 Mass Ave, Cambridge, MA
 *     02139, USA.
 *
 *     THIS SOFTWARE AND DOCUMENTATION ARE PROVIDED BY THE CALIFORNIA
 *     DEPARTMENT OF WATER RESOURCES AND CONTRIBUTORS "AS IS" AND ANY
 *     EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *     IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *     PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE CALIFORNIA
 *     DEPARTMENT OF WATER RESOURCES OR ITS CONTRIBUTORS BE LIABLE FOR
 *     ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *     CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 *     OR SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA OR PROFITS; OR
 *     BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *     LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *     (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 *     USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 *     DAMAGE.
 *******************************************************************************/
package gov.ca.bdo.modeling.dsm2.map.client.display;

import gov.ca.bdo.modeling.dsm2.map.client.HeaderPanel;
import gov.ca.bdo.modeling.dsm2.map.client.map.AddMapElementClickHandler;
import gov.ca.bdo.modeling.dsm2.map.client.map.MapControlPanel;
import gov.ca.bdo.modeling.dsm2.map.client.map.MapPanel;
import gov.ca.bdo.modeling.dsm2.map.client.map.MeasuringAreaInPolygon;
import gov.ca.bdo.modeling.dsm2.map.client.map.MeasuringDistanceAlongLine;
import gov.ca.bdo.modeling.dsm2.map.client.presenter.DSM2GridMapPresenter.Display;
import gov.ca.dsm2.input.model.Channel;
import gov.ca.dsm2.input.model.DSM2Model;
import gov.ca.modeling.maps.elevation.client.BathymetryDisplayer;
import gov.ca.modeling.maps.elevation.client.ElevationDisplayer;
import gov.ca.modeling.maps.elevation.client.ElevationProfileDisplayer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.HasInitializeHandlers;
import com.google.gwt.event.logical.shared.InitializeEvent;
import com.google.gwt.event.logical.shared.InitializeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.maps.client.event.PolylineLineUpdatedHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.GeoXmlLoadCallback;
import com.google.gwt.maps.client.overlay.GeoXmlOverlay;
import com.google.gwt.maps.client.overlay.PolyStyleOptions;
import com.google.gwt.maps.client.overlay.Polyline;
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
	private ElevationDisplayer elevationDisplayer;

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
				controlPanel.getFindButton().addClickHandler(
						new ClickHandler() {

							public void onClick(ClickEvent event) {
								String findText = controlPanel.getFindTextBox()
										.getText();
								if ((findText == null)
										|| findText.trim().equals("")) {
									return;
								}
								String fields[] = findText.split("\\s");
								if (fields.length >= 2) {
									if (fields[0].equalsIgnoreCase("node")) {
										mapPanel.centerAndZoomOnNode(fields[1]);
									} else if (fields[0]
											.equalsIgnoreCase("channel")) {
										mapPanel
												.centerAndZoomOnChannel(fields[1]);
									} else {
										// FIXME: do other searches
									}
								} else {
									// FIXME: do other searches
								}
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
		return controlPanel.getSaveEditButton();
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

	public HasClickHandlers getClickForElevationButton() {
		return controlPanel.getClickForElevationButton();
	}

	public HasClickHandlers getDrawXSectionButton() {
		return controlPanel.getDrawXSectionButton();
	}

	public void startMeasuringDistanceAlongLine() {
		if (lengthMeasurer == null) {
			lengthMeasurer = new MeasuringDistanceAlongLine(mapPanel
					.getMapWidget());
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
			areaMeasurer = new MeasuringAreaInPolygon(mapPanel.getMap());
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

	private Polyline line;
	private ElevationProfileDisplayer elevationProfileDisplayer;
	private BathymetryDisplayer bathymetryDisplayer;
	private AddMapElementClickHandler addMapElementHandler;

	public void addLine(Channel channel) {
		MapWidget map = mapPanel.getMap();
		PolyStyleOptions style = PolyStyleOptions.newInstance("#0000ff", 3, 1);
		if (line != null) {
			map.removeOverlay(line);
		}
		line = new Polyline(new LatLng[0]);
		map.addOverlay(line);
		line.setDrawingEnabled();
		line.setEditingEnabled(true);
		line.setStrokeStyle(style);
		line.addPolylineLineUpdatedHandler(new PolylineLineUpdatedHandler() {

			public void onUpdate(PolylineLineUpdatedEvent event) {
				if (line.getVertexCount() == 2) {
					line.setEditingEnabled(false);

					// drawLineButton.setDown(false);
					// drawXSection(line.getVertex(0),line.getVertex(1));
				}
			}
		});

	}

	public double getLengthInFeet() {
		return Math.round(line.getLength() * 3.2808399 * 100) / 100;
	}

	public void startClickingForElevation() {
		if (elevationDisplayer == null) {
			elevationDisplayer = new ElevationDisplayer(mapPanel.getMap());
		}
		elevationDisplayer.start();
	}

	public void stopClickingForElevation() {
		if (elevationDisplayer != null) {
			elevationDisplayer.stop();
		}
	}

	public void startDrawingElevationProfileLine() {
		if (elevationProfileDisplayer == null) {
			elevationProfileDisplayer = new ElevationProfileDisplayer(mapPanel
					.getMap(), infoPanel);
		}
		elevationProfileDisplayer
				.startDrawingLine((ToggleButton) getDrawXSectionButton());
	}

	public void stopDrawingElevationProfileLine() {
		if (elevationProfileDisplayer != null) {
			elevationProfileDisplayer.stopDrawingLine();
		}
	}

	public HasClickHandlers getAddButton() {
		return controlPanel.getAddButton();
	}

	public HasClickHandlers getDeleteButton() {
		return controlPanel.getDeleteButton();
	}

	public HasClickHandlers getShowBathymetryPointsButton() {
		return controlPanel.getShowBathymetryPointsButton();
	}

	public void startShowingBathymetryPoints() {
		if (bathymetryDisplayer == null) {
			bathymetryDisplayer = new BathymetryDisplayer(mapPanel.getMap());
		}
		bathymetryDisplayer.activateShowDataHandler(true);
	}

	public void stopShowingBathymetryPoints() {
		if (bathymetryDisplayer != null) {
			bathymetryDisplayer.activateShowDataHandler(false);
		}
	}

	public void setAddingMode(boolean down) {
		if (!down) {
			mapPanel.getMap().removeMapClickHandler(addMapElementHandler);
		} else {
			int addTypeSelected = controlPanel.getAddTypeSelected();
			if (addMapElementHandler == null) {
				addMapElementHandler = new AddMapElementClickHandler(mapPanel,
						addTypeSelected);
			} else {
				addMapElementHandler.setType(addTypeSelected);
			}
			mapPanel.getMap().addMapClickHandler(addMapElementHandler);
		}
	}

	public void setDeletingMode(boolean down) {
		// TODO Auto-generated method stub

	}

}
