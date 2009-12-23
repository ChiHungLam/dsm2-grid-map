package gov.ca.bdo.modeling.dsm2.map.client.model;

import gov.ca.bdo.modeling.dsm2.map.client.ChannelClickHandler;
import gov.ca.bdo.modeling.dsm2.map.client.MapPanel;
import gov.ca.bdo.modeling.dsm2.map.client.PolylineEncoder;
import gov.ca.dsm2.input.model.Channel;
import gov.ca.dsm2.input.model.Channels;
import gov.ca.dsm2.input.model.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.maps.client.event.PolylineMouseOverHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.PolyStyleOptions;
import com.google.gwt.maps.client.overlay.Polyline;

public class ChannelLineDataManager {
	private Channels channels;
	private final HashMap<String, Polyline> lineMap = new HashMap<String, Polyline>();
	private final NodeMarkerDataManager nodeManager;
	private final PolylineEncoder encoder;
	private static final boolean ENCODE_POLYLINES = true;
	private final int weight = 3;
	private final double opacity = 0.35;

	public ChannelLineDataManager(NodeMarkerDataManager nodeManager,
			Channels channels) {
		this.nodeManager = nodeManager;
		this.channels = channels;
		encoder = PolylineEncoder.newInstance(4, 12, 0.00001, false);
	}

	public void clear() {
		channels = new Channels();
	}

	public Channels getChannels() {
		return channels;
	}

	public void addPolyline(String channelId, Polyline line) {
		lineMap.put(channelId, line);
	}

	public Polyline getPolyline(String channelId) {
		return lineMap.get(channelId);
	}

	public List<Polyline> getLinesForNodeId(String nodeId) {
		ArrayList<Polyline> connectedChannels = new ArrayList<Polyline>();
		String upChannels = channels.getUpChannels(nodeId);
		String downChannels = channels.getDownChannels(nodeId);
		if (upChannels != null) {
			String[] channelIds = upChannels.split(",");
			for (String channelId : channelIds) {
				connectedChannels.add(getPolyline(channelId));
			}
		}
		if (downChannels != null) {
			String[] channelIds = downChannels.split(",");
			for (String channelId : channelIds) {
				connectedChannels.add(getPolyline(channelId));
			}
		}
		return connectedChannels;
	}

	public List<String> getChannelsForNodeId(String nodeId) {
		String downChannels = channels.getDownChannels(nodeId);
		String upChannels = channels.getUpChannels(nodeId);
		ArrayList<String> channelIds = new ArrayList<String>();
		parseIdsToList(downChannels, channelIds);
		parseIdsToList(upChannels, channelIds);
		return channelIds;
	}

	private void parseIdsToList(String channelIdString,
			ArrayList<String> channelIds) {
		if (channelIdString != null) {
			String[] ids = channelIdString.split(",");
			for (String id : ids) {
				channelIds.add(id.trim());
			}
		}
	}

	public Collection<Polyline> getPolylines() {
		return lineMap.values();
	}

	public void removePolyline(String channelId) {
		lineMap.remove(channelId);
	}

	public void addLines(MapPanel mapPanel) {
		for (Channel data : getChannels().getChannels()) {
			addChannelPolyline(data, mapPanel);
		}
	}

	public Polyline addChannelPolyline(Channel channel, MapPanel mapPanel) {

		Node upNode = getNodeManager().getNodeData(channel.getUpNodeId());
		Node downNode = getNodeManager().getNodeData(channel.getDownNodeId());
		if (upNode == null || downNode == null) {
			return null;
		}
		LatLng upPoint = LatLng.newInstance(upNode.getLatitude(), upNode
				.getLongitude());
		LatLng downPoint = LatLng.newInstance(downNode.getLatitude(), downNode
				.getLongitude());
		LatLng[] points = new LatLng[] { upPoint, downPoint };
		Polyline line = null;
		if (!ENCODE_POLYLINES) {
			line = new Polyline(points);
			PolyStyleOptions style = PolyStyleOptions
					.newInstance(getLineColor());
			style.setOpacity(opacity);
			style.setWeight(weight);
			line.setStrokeStyle(style);
		} else {
			line = encoder.dpEncodeToGPolyline(points, getLineColor(), weight,
					opacity);
		}
		addPolyline(channel.getId(), line);
		line.addPolylineMouseOverHandler(new PolylineMouseOverHandler() {

			public void onMouseOver(PolylineMouseOverEvent event) {
				changeCursor("pointer");
			}

			public native void changeCursor(String cursor)/*-{
		this.cursor=cursor;
	}-*/;
		});
		line
				.addPolylineClickHandler(new ChannelClickHandler(channel,
						mapPanel));
		mapPanel.getMap().addOverlay(line);
		return line;
	}

	private NodeMarkerDataManager getNodeManager() {
		return nodeManager;
	}

	protected String getLineColor() {
		return "#110077";
	}

}
