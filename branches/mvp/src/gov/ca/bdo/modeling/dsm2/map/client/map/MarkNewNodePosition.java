/**
 *   Copyright (C) 2009, 2010 
 *    Nicky Sandhu
 *    State of California,
 *    Department of Water Resources.
 *    This file is part of DSM2 Grid Map
 *    The DSM2 Grid Map is free software: 
 *    you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *    DSM2 Grid Map is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.

 *    You should have received a copy of the GNU General Public License
 *    along with DSM2 Grid Map.  If not, see <http://www.gnu.org/licenses>.
 */
package gov.ca.bdo.modeling.dsm2.map.client.map;

import gov.ca.dsm2.input.model.Node;

import java.util.List;

import com.google.gwt.maps.client.event.MarkerDragEndHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Marker;

public class MarkNewNodePosition implements MarkerDragEndHandler {

	private final MapPanel mapPanel;

	public MarkNewNodePosition(MapPanel mapPanel) {
		this.mapPanel = mapPanel;
	}

	public void onDragEnd(MarkerDragEndEvent event) {
		Marker marker = event.getSender();
		LatLng latLng = marker.getLatLng();
		String id = marker.getTitle();
		Node nodeData = mapPanel.getNodeManager().getNodeData(id);
		nodeData.setLatitude(latLng.getLatitude());
		nodeData.setLongitude(latLng.getLongitude());
		// draw the channels for this updated position
		if (mapPanel.getChannelManager() == null) {
			return;
		}
		List<String> channelIds = mapPanel.getChannelManager()
				.getChannelsForNodeId(nodeData.getId());
		for (String channelId : channelIds) {
			mapPanel.getMap().removeOverlay(
					mapPanel.getChannelManager().getPolyline(channelId));
			mapPanel.getChannelManager().removePolyline(channelId);
			mapPanel.getChannelManager().addChannelPolyline(
					mapPanel.getChannelManager().getChannels().getChannel(
							channelId), mapPanel);
		}
	}

}
