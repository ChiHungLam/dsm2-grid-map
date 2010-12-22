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
import gov.ca.bdo.modeling.dsm2.map.client.map.ElementType;
import gov.ca.bdo.modeling.dsm2.map.client.presenter.DSM2GridMapPresenter.Display;

import java.util.HashMap;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.InitializeEvent;
import com.google.gwt.event.logical.shared.InitializeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.maps.client.event.MapClickHandler;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class DSM2GridMapDisplay extends MapDisplay implements Display {
	private AddMapElementClickHandler addMapElementHandler;
	private MapClickHandler deleteMapElementHandler;
	private FlowPanel controlPanel;
	private FlowPanel infoPanel;
	private ToggleButton saveEditButton;
	private Panel addRemovePanel;
	private ToggleButton addButton;
	private ToggleButton deleteButton;
	private ListBox elementTypeBox;
	private HashMap<String, Integer> mapTypeToId;

	public DSM2GridMapDisplay(ContainerDisplay display, boolean viewOnly) {
		super(display, viewOnly, new VerticalPanel());
		controlPanel = new FlowPanel();
		controlPanel.setStyleName("control-panel");
		HorizontalPanel editPanel = new HorizontalPanel();
		editPanel.setStyleName("edit-panel");
		controlPanel.add(editPanel);
		editPanel.add(saveEditButton = new ToggleButton("Edit", "Save"));
		addRemovePanel = new HorizontalPanel();
		final CaptionPanel addRemoveCaptionPanel = new CaptionPanel(
				"Add/Remove");
		addRemoveCaptionPanel.setVisible(false);
		addRemoveCaptionPanel.add(addRemovePanel);
		editPanel.add(addRemoveCaptionPanel);
		VerticalPanel buttonPanel = new VerticalPanel();
		buttonPanel.add(addButton = new ToggleButton("Add", "Adding..."));
		buttonPanel
				.add(deleteButton = new ToggleButton("Delete", "Deleting..."));
		addRemovePanel.add(buttonPanel);
		elementTypeBox = new ListBox();
		mapTypeToId = new HashMap<String, Integer>();
		mapTypeToId.put("Node", ElementType.NODE);
		mapTypeToId.put("Channel", ElementType.CHANNEL);
		mapTypeToId.put("Reservoir", ElementType.RESERVOIR);
		mapTypeToId.put("Gate", ElementType.GATE);
		mapTypeToId.put("XSection", ElementType.XSECTION);
		mapTypeToId.put("Output", ElementType.OUTPUT);
		mapTypeToId.put("Text", ElementType.TEXT);
		mapTypeToId.put("KML", ElementType.KML);
		String[] elementTypes = new String[] { "Node", "Channel", "Reservoir",
				"Gate", "XSection", "Output", "Text", "KML" };
		for (String item : elementTypes) {
			elementTypeBox.addItem(item);
		}
		addRemovePanel.add(elementTypeBox);
		saveEditButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				if (saveEditButton.isDown()) {
					addRemoveCaptionPanel.setVisible(true);
				} else {
					addRemoveCaptionPanel.setVisible(false);
				}
			}
		});
		saveEditButton.setEnabled(!viewOnly);
		infoPanel = new FlowPanel();
		infoPanel.setStyleName("info-panel");
		CaptionPanel captionInfoPanel = new CaptionPanel();
		captionInfoPanel.setCaptionText("Info");
		captionInfoPanel.add(infoPanel);
		controlPanel.add(captionInfoPanel);
	}

	@Override
	protected void initializeUI() {
		super.initializeUI();
		// layout top level things here
		VerticalPanel sidePanel = (VerticalPanel) super.getSidePanel();
		sidePanel.add(new ScrollPanel(controlPanel));
		mapPanel.setInfoPanel(infoPanel);
	}

	public Widget asWidget() {
		return this;
	}

	public HandlerRegistration addInitializeHandler(
			InitializeHandler initializeHandler) {
		return this.addHandler(initializeHandler, InitializeEvent.getType());
	}

	public HasClickHandlers getSaveEditButton() {
		return saveEditButton;
	}

	public HasClickHandlers getAddButton() {
		return addButton;
	}

	public HasClickHandlers getDeleteButton() {
		return deleteButton;
	}

	public int getAddTypeSelected() {
		return mapTypeToId.get(
				elementTypeBox.getItemText(elementTypeBox.getSelectedIndex()))
				.intValue();
	}

	public void setAddingMode(boolean down) {
		clearDeleteingMode();
		if (!down) {
			mapPanel.getMap().removeMapClickHandler(addMapElementHandler);
		} else {
			int addTypeSelected = getAddTypeSelected();
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

}
