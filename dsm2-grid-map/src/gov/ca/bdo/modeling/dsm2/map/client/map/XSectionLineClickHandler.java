package gov.ca.bdo.modeling.dsm2.map.client.map;

import gov.ca.dsm2.input.model.Channel;
import gov.ca.dsm2.input.model.XSection;
import gov.ca.dsm2.input.model.XSectionProfile;

import java.util.ArrayList;

import com.google.gwt.maps.client.event.PolylineClickHandler;
import com.google.gwt.maps.client.event.PolylineLineUpdatedHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.PolyEditingOptions;
import com.google.gwt.maps.client.overlay.PolyStyleOptions;
import com.google.gwt.maps.client.overlay.Polyline;

public class XSectionLineClickHandler implements PolylineClickHandler {
	private static final PolyStyleOptions blueLineStyle = PolyStyleOptions
			.newInstance("blue");
	private static final PolyStyleOptions greenLineStyle = PolyStyleOptions
			.newInstance("green");

	private CrossSectionEditorPanel xsEditorPanel;
	private final XSection xSection;
	private int xSectionIndex;
	private boolean edit;
	private Channel channel;
	private MapPanel mapPanel;
	private ChannelInfoPanel channelInfoPanel;

	public XSectionLineClickHandler(MapPanel mapPanel,
			ChannelInfoPanel infoPanel, Channel channel, XSection xSection,
			int index, boolean edit) {
		this.mapPanel = mapPanel;
		channelInfoPanel = infoPanel;
		this.channel = channel;
		this.xSection = xSection;
		xSectionIndex = index;
		this.edit = edit;
	}

	public void onClick(PolylineClickEvent event) {
		if (mapPanel.isInEditMode() && mapPanel.isInEditMode()) {
			mapPanel.getChannelManager().removeXSection(xSection);
			return;
		}
		for (XSection xs : mapPanel.getChannelManager().getXSections()) {
			Polyline line = mapPanel.getChannelManager().getXsectionLineFor(xs);
			if (xs == xSection) {
				line.setStrokeStyle(blueLineStyle);
				channelInfoPanel.drawXSection(channel, xSectionIndex);
			} else {
				line.setStrokeStyle(greenLineStyle);
			}
		}
		final Polyline line = mapPanel.getChannelManager().getXsectionLineFor(
				xSection);
		line.setStrokeStyle(PolyStyleOptions.newInstance("red"));
		if (!edit) {
			line.setEditingEnabled(false);
			channelInfoPanel.drawXSection(channel, xSectionIndex);
		} else {
			if (xsEditorPanel == null) {
				xsEditorPanel = new CrossSectionEditorPanel();
			}
			line.setEditingEnabled(PolyEditingOptions.newInstance(2));
			line
					.addPolylineLineUpdatedHandler(new PolylineLineUpdatedHandler() {

						public void onUpdate(PolylineLineUpdatedEvent event) {
							XSectionProfile profile = xSection.getProfile();
							if (profile == null) {
								return;
							}
							ArrayList<double[]> endPoints = new ArrayList<double[]>();
							for (int i = 0; i < 2; i++) {
								double[] points = new double[2];
								LatLng vertex = line.getVertex(i);
								points[0] = vertex.getLatitude();
								points[1] = vertex.getLongitude();
								endPoints.add(points);
							}
							profile.setEndPoints(endPoints);

							ModelUtils.updateXSectionPosition(channel, mapPanel
									.getNodeManager().getNodes(), xSection);
						}
					});
			mapPanel.getInfoPanel().clear();
			mapPanel.getInfoPanel().add(xsEditorPanel);
			mapPanel.getInfoPanel().add(xsEditorPanel);
			xsEditorPanel.draw(channel, xSectionIndex, mapPanel);
		}
	}

}
