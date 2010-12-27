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

import gov.ca.bdo.modeling.dsm2.map.client.map.AddMapElementClickHandler;
import gov.ca.bdo.modeling.dsm2.map.client.map.DeleteMapElementClickHandler;
import gov.ca.bdo.modeling.dsm2.map.client.map.MeasuringAreaInPolygon;
import gov.ca.bdo.modeling.dsm2.map.client.map.MeasuringDistanceAlongLine;
import gov.ca.bdo.modeling.dsm2.map.client.presenter.DSM2GridMapPresenter.Display;
import gov.ca.modeling.maps.elevation.client.BathymetryDisplayer;
import gov.ca.modeling.maps.elevation.client.ElevationDisplayer;
import gov.ca.modeling.maps.elevation.client.ElevationProfileDisplayer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.InitializeEvent;
import com.google.gwt.event.logical.shared.InitializeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.maps.client.event.MapClickHandler;
import com.google.gwt.maps.client.overlay.GeoXmlLoadCallback;
import com.google.gwt.maps.client.overlay.GeoXmlOverlay;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class DSM2GridMapDisplay extends MapDisplay implements Display {
	private AddMapElementClickHandler addMapElementHandler;
	private MapClickHandler deleteMapElementHandler;
	private MapControlPanel controlPanel;
	private MeasuringDistanceAlongLine lengthMeasurer;
	private MeasuringAreaInPolygon areaMeasurer;
	private ElevationDisplayer elevationDisplayer;
	private FlowPanel infoPanel;
	private ElevationProfileDisplayer elevationProfileDisplayer;
	private BathymetryDisplayer bathymetryDisplayer;

	public DSM2GridMapDisplay(ContainerDisplay display, boolean viewOnly, EventBus eventBus) {
		super(display, viewOnly, eventBus);
		controlPanel = new MapControlPanel(viewOnly);
	}

	@Override
	protected void initializeUI() {
		super.initializeUI();
		// layout top level things here
		mapPanel.setInfoPanel(controlPanel.getInfoPanel());
	}

	public Widget asWidget() {
		return this;
	}

	public HandlerRegistration addInitializeHandler(
			InitializeHandler initializeHandler) {
		return this.addHandler(initializeHandler, InitializeEvent.getType());
	}
	
	@Override
	public void setEditMode(boolean editMode) {
		super.setEditMode(editMode);
		clearAddingMode();
		clearDeleteingMode();
	}

	public HasClickHandlers getSaveEditButton() {
		return controlPanel.getSaveEditButton();
	}

	public HasClickHandlers getAddButton() {
		return controlPanel.getAddButton();
	}

	public HasClickHandlers getDeleteButton() {
		return controlPanel.getDeleteButton();
	}

	public int getAddTypeSelected() {
		return controlPanel.getAddTypeSelected();
	}

	public void setAddingMode(boolean down) {
		clearDeleteingMode();
		if (!down) {
			mapPanel.getMap().removeMapClickHandler(addMapElementHandler);
		} else {
			int addTypeSelected = getAddTypeSelected();
			if (addMapElementHandler == null) {
				addMapElementHandler = new AddMapElementClickHandler(mapPanel,
						addTypeSelected, eventBus);
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

	//
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

	public TextBox getFindTextBox() {
		return controlPanel.getFindTextBox();
	}

	public HasClickHandlers getFindButton() {
		return controlPanel.getFindButton();
	}

	public HasClickHandlers getShowFlowlinesButton() {
		return controlPanel.getFlowlineButton();
	}

	public HasClickHandlers getMeasureLengthButton() {
		return controlPanel.getMeasureLengthButton();
	}

	public HasClickHandlers getMeasureAreaButton() {
		return controlPanel.getMeasureAreaButton();
	}

	public HasClickHandlers getMeasureVolumeButton() {
		// FIXME: implement this
		return null;
	}

	public HasClickHandlers getMeasureAverageElevationButton() {
		// FIXME: implement this
		return null;
	}

	public HasClickHandlers getDisplayElevationProfileButton() {
		return controlPanel.getDisplayElevationProfileButton();
	}

	public HasClickHandlers getDisplayElevationButton() {
		return controlPanel.getDisplayElevationButton();
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

	public void hideFlowLines() {
		mapPanel.hideFlowLines();
	}

	public void showFlowLines() {
		mapPanel.showFlowLines();
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

	public void centerAndZoomOnChannel(String channelId) {
		mapPanel.centerAndZoomOnChannel(channelId);
	}

	public void centerAndZoomOnNode(String nodeId) {
		mapPanel.centerAndZoomOnNode(nodeId);
	}

	@Override
	public Widget getSidePanel() {
		return controlPanel;
	}
}
