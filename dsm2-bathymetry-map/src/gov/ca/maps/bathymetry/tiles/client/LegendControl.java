package gov.ca.maps.bathymetry.tiles.client;

import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.ControlAnchor;
import com.google.gwt.maps.client.control.ControlPosition;
import com.google.gwt.maps.client.control.Control.CustomControl;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Widget;

public class LegendControl extends CustomControl {

	public LegendControl() {
		super(new ControlPosition(ControlAnchor.TOP_RIGHT, 125, 5));
	}

	@Override
	protected Widget initialize(MapWidget map) {
		DisclosurePanel p=new DisclosurePanel("Legend");
		p.setAnimationEnabled(true);
		p.setOpen(true);
		p.add(ExportOverlays.getLegendPanel());
		p.getElement().getStyle().setBackgroundColor("#fffff0");// ivory svg color
		return p;
	}

	@Override
	public boolean isSelectable() {
		return false;
	}

}
