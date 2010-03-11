/**
 *    Copyright (C) 2009, 2010 
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
 *    GNU General Public License for more details. [http://www.gnu.org/licenses]
 *    
 *    @author Nicky Sandhu
 *    
 */
package gov.ca.dsm2.input.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A container for all channels {@link #getChannels()} in DSM2 model. Allows for
 * quick retrieval of channel given the channel id {@link #getChannel(String)}.
 * Also maintains a list of up nodes {@link #getUpChannels(String)} and down
 * nodes {@link #getDownChannels(String)}
 * 
 * The container maintains the list in the order added.
 * 
 * @author psandhu
 * 
 */
@SuppressWarnings("serial")
public class Channels implements Serializable {
	private  ArrayList<Channel> channels;
	private  HashMap<String, Channel> channelIdMap;
	private  HashMap<String, String> upNodeMap = new HashMap<String, String>();
	private  HashMap<String, String> downNodeMap = new HashMap<String, String>();

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

}
