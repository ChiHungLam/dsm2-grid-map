package gov.ca.dsm2.input.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A container for all channels in DSM2 model. Allows for quick retrieval of
 * channel given the channel id. Also maintains a list of upnodes and down nodes
 * 
 * The container maintains the list in the order added.
 * 
 * @author psandhu
 * 
 */
@SuppressWarnings("serial")
public class Channels implements Serializable {
	private final ArrayList<Channel> channels;
	private final HashMap<String, Channel> channelIdMap;
	private final HashMap<String, String> upNodeMap = new HashMap<String, String>();
	private final HashMap<String, String> downNodeMap = new HashMap<String, String>();

	public Channels() {
		channels = new ArrayList<Channel>();
		channelIdMap = new HashMap<String, Channel>();
	}

	/**
	 * adds a channel to the list
	 * 
	 * @param channel
	 */
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

	/**
	 * gets the channel given its unique id
	 * 
	 * @param channelId
	 * @return
	 */
	public Channel getChannel(String channelId) {
		return channelIdMap.get(channelId);
	}

	/**
	 * removes channel from this list
	 * 
	 * @param channel
	 */
	public void removeChannel(Channel channel) {
		channels.remove(channel);
		channelIdMap.remove(channel.getId());
		// FIXME: remove from up/down nodes as well
	}

	/**
	 * returns the list of channels
	 * 
	 * @return
	 */
	public List<Channel> getChannels() {
		return channels;
	}

	/**
	 * returns the list of channels to which the given node id is the upstream
	 * node of
	 * 
	 * TODO: make it return a list of Channel objects instead
	 * 
	 * @param nodeId
	 * @return a comma separated list of channel ids
	 */
	public String getUpChannels(String nodeId) {
		return upNodeMap.get(nodeId);
	}

	/**
	 * returns the list of channels to which this node is a downstream node
	 * 
	 * @param nodeId
	 * @return a comma separted list of channel ids
	 */
	public String getDownChannels(String nodeId) {
		return downNodeMap.get(nodeId);
	}

	/**
	 * builds a table containing the GIS information for the shape of the
	 * channel line.
	 * 
	 * @return a string representation of the table.
	 */
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
