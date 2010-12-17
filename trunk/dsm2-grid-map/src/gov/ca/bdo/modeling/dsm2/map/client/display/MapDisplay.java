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

import gov.ca.bdo.modeling.dsm2.map.client.event.MessageEvent;
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
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.HasInitializeHandlers;
import com.google.gwt.event.logical.shared.InitializeEvent;
import com.google.gwt.event.logical.shared.InitializeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.maps.client.event.MapClickHandler;
import com.google.gwt.maps.client.event.PolylineLineUpdatedHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.GeoXmlLoadCallback;
import com.google.gwt.maps.client.overlay.GeoXmlOverlay;
import com.google.gwt.maps.client.overlay.PolyStyleOptions;
import com.google.gwt.maps.client.overlay.Polyline;
import com.google.gwt.maps.utility.client.DefaultPackage;
import com.google.gwt.maps.utility.client.GoogleMapsUtility;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class MapDisplay extends ResizeComposite implements Display,
		HasInitializeHandlers {
	private MapPanel mapPanel;
	private final MapControlPanel controlPanel;
	private final FlowPanel infoPanel;
	private final VerticalPanel controlPanelContainer;
	private MeasuringDistanceAlongLine lengthMeasurer;
	private MeasuringAreaInPolygon areaMeasurer;
	private ElevationDisplayer elevationDisplayer;
	private SplitLayoutPanel centerPanel;
	private ContainerDisplay containerDisplay;

	public MapDisplay(ContainerDisplay display, boolean viewOnly) {
		containerDisplay = display;
		// layout top level things here
		controlPanel = new MapControlPanel(viewOnly);
		infoPanel = new FlowPanel();
		infoPanel.setStyleName("infoPanel");

		controlPanelContainer = new VerticalPanel();
		controlPanelContainer.add(controlPanel);
		DisclosurePanel infoPanelContainer = new DisclosurePanel();
		infoPanelContainer.setOpen(true);
		infoPanelContainer.setHeader(new Label("Selected Element Info"));
		infoPanelContainer.add(infoPanel);
		controlPanelContainer.add(infoPanelContainer);
		centerPanel = new SplitLayoutPanel();
		centerPanel.setStyleName("map-split-layout-panel");
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			public void execute() {
				loadMaps();
			}
		});
		initWidget(centerPanel);
	}

	public Widget asWidget() {
		return this;
	}

	public void loadMaps() {
		if (!Maps.isLoaded()) {
			containerDisplay
					.showMessage(
							"The Maps API is not installed."
									+ "  The <script> tag that loads the Maps API may be missing or your Maps key may be wrong.",
							MessageEvent.ERROR, 0);
			return;
		}

		if (!Maps.isBrowserCompatible()) {
			containerDisplay.showMessage(
					"The Maps API is not compatible with this browser.",
					MessageEvent.ERROR, 0);
			return;
		}
		Runnable mapLoadCallback = new Runnable() {

			public void run() {
				mapPanel = new MapPanel();
				mapPanel.setInfoPanel(infoPanel);
				Widget westWidget = new ScrollPanel(controlPanelContainer);
				centerPanel.addWest(westWidget, 40);
				centerPanel.setWidgetSize(westWidget, 40);
				centerPanel.add(mapPanel);
				mapPanel.setStudy(containerDisplay.getCurrentStudy());
				mapPanel.setModel(containerDisplay.getModel());
				refresh();

				InitializeEvent.fire(MapDisplay.this);

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
			// FIXME: change this dependency on loading javascript libraries
			// from another site
			// when this fails, it causes app to behave as if it had broken.
			GoogleMapsUtility.loadUtilityApi(mapLoadCallback,
					DefaultPackage.MARKER_CLUSTERER,
					DefaultPackage.LABELED_MARKER,
					DefaultPackage.MAP_ICON_MAKER);
		} else {
			mapLoadCallback.run();
		}
	}

	public DSM2Model getModel() {
		return mapPanel.getModel();
	}

	public void refresh() {
		mapPanel.populateGrid();
	}

	public void setModel(DSM2Model result) {
		if (mapPanel != null) {
			mapPanel.setModel(result);
		}
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

	public HasClickHandlers getMeasureLengthButton() {
		return controlPanel.getMeasureLengthButton();
	}

	public HasClickHandlers getMeasureAreaButton() {
		return controlPanel.getMeasureAreaButton();
	}

	public HasClickHandlers getDisplayElevationButton() {
		return controlPanel.getDisplayElevationButton();
	}

	public HasClickHandlers getDisplayElevationProfileButton() {
		return controlPanel.getDisplayElevationProfileButton();
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
	private MapClickHandler deleteMapElementHandler;

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
				.startDrawingLine((ToggleButton) getDisplayElevationProfileButton());
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
		clearDeleteingMode();
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
		clearAddingMode();
		((ToggleButton) getAddButton()).setDown(false);
		if (!down) {
			mapPanel.getMap().removeMapClickHandler(deleteMapElementHandler);
		} else {
			if (deleteMapElementHandler == null) {
				deleteMapElementHandler = new DeleteMapElementClickHandler(
						mapPanel);
			}
			mapPanel.getMap().addMapClickHandler(deleteMapElementHandler);
		}
	}

	private void clearAddingMode() {
		((ToggleButton) getAddButton()).setDown(false);
		if (addMapElementHandler != null) {
			mapPanel.getMap().removeMapClickHandler(addMapElementHandler);
		}

	}

	private void clearDeleteingMode() {
		((ToggleButton) getDeleteButton()).setDown(false);
		if (deleteMapElementHandler != null) {
			mapPanel.getMap().removeMapClickHandler(deleteMapElementHandler);
		}
	}

	public void centerAndZoomOnChannel(String channelId) {
		mapPanel.centerAndZoomOnChannel(channelId);
	}

	public void centerAndZoomOnNode(String nodeId) {
		mapPanel.centerAndZoomOnNode(nodeId);
	}

	public HasClickHandlers getFindButton() {
		return controlPanel.getFindButton();
	}

	public HasText getFindTextBox() {
		return controlPanel.getFindTextBox();
	}

	public PushButton getDownloadHydroButton() {
		return controlPanel.getDownloadHydroButton();
	}

	public PushButton getDownloadGISButton() {
		return controlPanel.getDownloadGISButton();
	}

}
