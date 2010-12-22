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

import gov.ca.bdo.modeling.dsm2.map.client.map.MapPanel;
import gov.ca.dsm2.input.model.DSM2Model;

import com.google.gwt.event.logical.shared.HasInitializeHandlers;
import com.google.gwt.event.logical.shared.InitializeEvent;
import com.google.gwt.event.logical.shared.InitializeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.maps.utility.client.DefaultPackage;
import com.google.gwt.maps.utility.client.GoogleMapsUtility;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class MapDisplay extends ResizeComposite implements
		HasInitializeHandlers {
	protected SplitLayoutPanel mainPanel;
	protected ContainerDisplay containerDisplay;
	protected MapPanel mapPanel;
	protected Panel sidePanel;
	protected boolean viewOnly;

	public MapDisplay(ContainerDisplay display, boolean viewOnly,
			Panel sidePanel) {
		this.viewOnly = viewOnly;
		containerDisplay = display;
		this.sidePanel = sidePanel;
		mainPanel = new SplitLayoutPanel();
		mainPanel.setStyleName("map-split-layout-panel");

		initWidget(mainPanel);
		// layout top level things here
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
				initializeUI();
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

	protected void initializeUI() {
		mainPanel.addWest(sidePanel, 610);
		mainPanel.add(mapPanel = new MapPanel());
		mapPanel.setInfoPanel(sidePanel);
	}

	public Widget asWidget() {
		return this;
	}

	public MapPanel getMapPanel() {
		return mapPanel;
	}

	public Widget getSidePanel() {
		return sidePanel;
	}

	public DSM2Model getModel() {
		return mapPanel.getModel();
	}

	public void refresh() {
		if (mapPanel != null) {
			mapPanel.populateGrid();
		}
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
	}

}
