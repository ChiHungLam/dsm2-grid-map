package gov.ca.dsm2.input.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A container for all channels in DSM2 model
 * 
 * @author psandhu
 * 
 */
@SuppressWarnings("serial")
public class Channels implements Serializable {
	private ArrayList<Channel> channels;
	private HashMap<String, Channel> channelIdMap;
	private HashMap<String, String> upNodeMap = new HashMap<String, String>();
	private HashMap<String, String> downNodeMap = new HashMap<String, String>();

	public Channels() {
		channels = new ArrayList<Channel>();
		channelIdMap = new HashMap<String, Channel>();
	}

	public void addChannel(Channel channel) {
		channels.add(channel);
		channelIdMap.put(channel.getId(), channel);
		String upChannels = upNodeMap.get(channel.getUpNodeId());
		if (upChannels == null) {
			upNodeMap.put(channel.getUpNodeId(), channel.getId());
		} else {
			upNodeMap.put(channel.getUpNodeId(), upChannels + ","
					+ channel.getId());
		}
		String downChannels = downNodeMap.get(channel.getDownNodeId());
		if (downChannels == null) {
			downNodeMap.put(channel.getDownNodeId(), channel.getId());
		} else {
			downNodeMap.put(channel.getDownNodeId(), downChannels + ","
					+ channel.getId());
		}
	}

	public Channel getChannel(String channelId) {
		return channelIdMap.get(channelId);
	}

	public void removeChannel(Channel channel) {
		channels.remove(channel);
		channelIdMap.remove(channel.getId());
	}

	public List<Channel> getChannels() {
		return channels;
	}

	public String getUpChannels(String nodeId) {
		return upNodeMap.get(nodeId);
	}

	public String getDownChannels(String nodeId) {
		return downNodeMap.get(nodeId);
	}

	public String buildGISTable() {
		StringBuilder buf = new StringBuilder();
		buf.append("CHANNEL_GIS\n");
		buf.append("ID\tINTERIOR_LAT_LNG\n");
		for (Channel channel : channels) {
			buf.append(channel.getId()).append("\t").append(
					TableUtil.buildInteriorLatLngPoints(channel
							.getLatLngPoints())).append("\n");
		}
		buf.append("END\n");
		return buf.toString();
	}
	
}
