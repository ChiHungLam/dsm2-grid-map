package gov.ca.bdo.modeling.dsm2.map.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.ControlAnchor;
import com.google.gwt.maps.client.control.ControlPosition;
import com.google.gwt.maps.client.control.Control.CustomControl;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class GridVisibilityControl extends CustomControl {

	private final MapPanel mapPanel;
	private CheckBox channelHideBox;

	public GridVisibilityControl(MapPanel mapPanel) {
		super(new ControlPosition(ControlAnchor.TOP_RIGHT, 55, 30));
		this.mapPanel = mapPanel;
	}

	@Override
	protected Widget initialize(MapWidget map) {
		final Label hideLabel = new Label("Hide...");
		final CheckBox nodeHideBox = new CheckBox("Nodes", false);
		nodeHideBox.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				mapPanel.hideMarkers(nodeHideBox.getValue());
			}
		});
		channelHideBox = new CheckBox("Channels");
		channelHideBox.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				mapPanel.hideChannelLines(channelHideBox.getValue());
			}
		});
		//
		final CheckBox gatesHideBox = new CheckBox("Gates");
		gatesHideBox.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				mapPanel.hideGateMarkers(gatesHideBox.getValue());
			}
		});
		//
		final CheckBox reservoirsHideBox = new CheckBox("Reservoirs");
		reservoirsHideBox.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				mapPanel.hideReservoirMarkers(reservoirsHideBox.getValue());
			}
		});
		//
		final CheckBox outputMarkerHideBox = new CheckBox("Output Markers");
		outputMarkerHideBox.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				mapPanel.hideOutputMarkers(outputMarkerHideBox.getValue());
			}
		});
		//
		final CheckBox bathymetryHideBox = new CheckBox("Bathymetry");
		bathymetryHideBox.setEnabled(true);
		bathymetryHideBox.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				mapPanel.showBathymetry(bathymetryHideBox.getValue());
			}
		});

		VerticalPanel vpanel = new VerticalPanel();
		vpanel.add(hideLabel);
		vpanel.add(new HTML("<hr/>"));
		vpanel.add(nodeHideBox);
		vpanel.add(channelHideBox);
		vpanel.add(gatesHideBox);
		vpanel.add(reservoirsHideBox);
		vpanel.add(outputMarkerHideBox);
		vpanel.add(new HTML("<hr/>"));
		vpanel.add(bathymetryHideBox);
		DisclosurePanel panel = new DisclosurePanel("Visibility");
		panel.setStyleName("visibilityPanel");
		panel.setContent(vpanel);
		return panel;
	}

	@Override
	public boolean isSelectable() {
		return false;
	}

	public boolean getHideChannels() {
		return channelHideBox.getValue();
	}
}