package gov.ca.bdo.modeling.dsm2.map.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.ControlAnchor;
import com.google.gwt.maps.client.control.ControlPosition;
import com.google.gwt.maps.client.control.Control.CustomControl;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class GridVisibilityControl extends CustomControl {

	private final MapPanel mapPanel;

	public GridVisibilityControl(MapPanel mapPanel) {
		super(new ControlPosition(ControlAnchor.TOP_LEFT, 7, 7));
		this.mapPanel = mapPanel;
	}

	@Override
	protected Widget initialize(MapWidget map) {
		VerticalPanel vpanel = new VerticalPanel();
		final CheckBox nodeHideBox = new CheckBox("Hide Nodes", false);
		nodeHideBox.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				mapPanel.hideMarkers(nodeHideBox.getValue());
			}
		});
		//
		final CheckBox channelHideBox = new CheckBox("Hide Channels");
		channelHideBox.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				mapPanel.hideChannelLines(channelHideBox.getValue());
			}
		});
		//
		final CheckBox gatesHideBox = new CheckBox("Hide Gates");
		gatesHideBox.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				mapPanel.hideGateMarkers(gatesHideBox.getValue());
			}
		});
		//
		final CheckBox reservoirsHideBox = new CheckBox("Hide Reservoirs");
		reservoirsHideBox.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				mapPanel.hideReservoirMarkers(reservoirsHideBox.getValue());
			}
		});
		//
		final CheckBox outputMarkerHideBox = new CheckBox("Hide Output Markers");
		outputMarkerHideBox.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				mapPanel.hideOutputMarkers(outputMarkerHideBox.getValue());
			}
		});
		return vpanel;
	}

	@Override
	public boolean isSelectable() {
		return false;
	}
}