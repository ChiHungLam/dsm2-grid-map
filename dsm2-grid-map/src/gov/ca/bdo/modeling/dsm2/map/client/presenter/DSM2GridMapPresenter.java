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
package gov.ca.bdo.modeling.dsm2.map.client.presenter;

import gov.ca.bdo.modeling.dsm2.map.client.service.DSM2InputServiceAsync;
import gov.ca.modeling.dsm2.widgets.client.events.MessageEvent;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;

public class DSM2GridMapPresenter extends DSM2ModelBasePresenter {
	public interface Display extends DSM2ModelBasePresenter.Display {
		public boolean isInEditMode();

		public void setEditMode(boolean editMode);

		public HasClickHandlers getAddButton();

		public HasClickHandlers getDeleteButton();

		public void setAddingMode(boolean down);

		public void setDeletingMode(boolean down);

		public HasClickHandlers getMeasureLengthButton();

		public void stopMeasuringDistanceAlongLine();

		public void startMeasuringDistanceAlongLine();

		public void stopMeasuringAreaInPolygon();

		public void startMeasuringAreaInPolygon();

		public HasClickHandlers getMeasureAreaButton();

		public HasClickHandlers getShowFlowlinesButton();

		public void showFlowLines();

		public void hideFlowLines();

		public HasClickHandlers getDisplayElevationButton();

		public HasClickHandlers getDisplayElevationProfileButton();

		public void startClickingForElevation();

		public void stopClickingForElevation();

		public void startDrawingElevationProfileLine();

		public void stopDrawingElevationProfileLine();

		public HasClickHandlers getFindNodeButton();

		public HasClickHandlers getFindChannelButton();

		public TextBox getFindTextBox();

		public void centerAndZoomOnNode(String string);

		public void centerAndZoomOnChannel(String string);

		public HasClickHandlers getCancelEditButton();

		public void showInfoPanel();

	}

	public DSM2GridMapPresenter(DSM2InputServiceAsync dsm2InputService,
			SimpleEventBus eventBus, Display display,
			ContainerPresenter containerPresenter) {
		super(dsm2InputService, eventBus, display, containerPresenter, false);
	}

	public DSM2GridMapPresenter(DSM2InputServiceAsync dsm2InputService,
			SimpleEventBus eventBus, Display display,
			ContainerPresenter containerPresenter, boolean viewOnly) {
		super(dsm2InputService, eventBus, display, containerPresenter, viewOnly);
	}

	public void go(HasWidgets container) {
		super.go(container);
	}

	protected void bind() {
		super.bind();
		if (bound) {
			return;
		}
		final Display d = (Display) display;

		d.getCancelEditButton().addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				display.setEditMode(false);
				display.refresh();
			}
		});
		d.getAddButton().addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				boolean down = ((ToggleButton) event.getSource()).isDown();
				d.setAddingMode(down);
			}
		});

		d.getDeleteButton().addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				boolean down = ((ToggleButton) event.getSource()).isDown();
				d.setDeletingMode(down);
			}
		});

		d.getMeasureLengthButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				Object source = event.getSource();
				if (source instanceof ToggleButton) {
					ToggleButton button = (ToggleButton) source;
					if (button.isDown()) {
						d.startMeasuringDistanceAlongLine();
					} else {
						d.stopMeasuringDistanceAlongLine();
					}
				}
			}
		});
		d.getMeasureAreaButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				Object source = event.getSource();
				if (source instanceof ToggleButton) {
					ToggleButton button = (ToggleButton) source;
					if (button.isDown()) {
						d.startMeasuringAreaInPolygon();
					} else {
						d.stopMeasuringAreaInPolygon();
					}
				}
			}
		});
		d.getShowFlowlinesButton().addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				HasClickHandlers flowLineButton = d.getShowFlowlinesButton();
				if (flowLineButton instanceof ToggleButton) {
					ToggleButton flowButton = (ToggleButton) flowLineButton;
					if (flowButton.isDown()) {
						d.showFlowLines();
					} else {
						d.hideFlowLines();
					}
				} else {
					Window.alert("This button should be a toggle!!");
				}
			}
		});

		d.getDisplayElevationButton().addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				HasClickHandlers button = d.getDisplayElevationButton();
				if (button instanceof ToggleButton) {
					ToggleButton tb = (ToggleButton) button;
					if (tb.isDown()) {
						d.startClickingForElevation();
					} else {
						d.stopClickingForElevation();
					}
				}
			}
		});
		d.getDisplayElevationProfileButton().addClickHandler(
				new ClickHandler() {

					public void onClick(ClickEvent event) {
						HasClickHandlers button = d
								.getDisplayElevationProfileButton();
						if (button instanceof ToggleButton) {
							ToggleButton tb = (ToggleButton) button;
							if (tb.isDown()) {
								d.startDrawingElevationProfileLine();
							} else {
								d.stopDrawingElevationProfileLine();
							}
						}
					}
				});
		d.getFindTextBox().addKeyPressHandler(new KeyPressHandler() {

			public void onKeyPress(KeyPressEvent event) {
				if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
					zoomAndCenterOnElement("node");
				}
			}
		});
		d.getFindNodeButton().addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				zoomAndCenterOnElement("node");
			}

		});

		d.getFindChannelButton().addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				zoomAndCenterOnElement("channel");
				d.showInfoPanel();
			}

		});

		bound = true;
	}

	private void zoomAndCenterOnElement(String type) {
		Display d = (Display) display;
		String findText = d.getFindTextBox().getText();
		if ((findText == null) || findText.trim().equals("")) {
			flashHelpMessage();
			return;
		}
		if (type.equalsIgnoreCase("node")) {
			d.centerAndZoomOnNode(findText);
		} else if (type.equalsIgnoreCase("channel")) {
			d.centerAndZoomOnChannel(findText);
		} else {
			flashHelpMessage();
		}

	}

	private void flashHelpMessage() {
		eventBus.fireEvent(new MessageEvent(
				"Enter the id for node or channel to zoom onto it!"));
	}
}
