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
package gov.ca.bdo.modeling.dsm2.map.client.map;

import gov.ca.bdo.modeling.dsm2.map.client.WindowUtils;
import gov.ca.dsm2.input.model.Channel;
import gov.ca.dsm2.input.model.Channels;
import gov.ca.dsm2.input.model.Node;
import gov.ca.dsm2.input.model.XSection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.maps.client.event.PolylineMouseOverHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Overlay;
import com.google.gwt.maps.client.overlay.PolyStyleOptions;
import com.google.gwt.maps.client.overlay.Polyline;

public class ChannelLineDataManager {
	private Channels channels;
	private final HashMap<String, Polyline> lineMap = new HashMap<String, Polyline>();
	private HashMap<Polyline, String> lineToIdMap = null;
	private final PolylineEncoder encoder;
	private static final boolean ENCODE_POLYLINES = false;
	private final int weight = 3;
	private final double opacity = 0.35;
	private MapPanel mapPanel;
	private HashMap<XSection, Polyline> xsectionLineMap;

	public ChannelLineDataManager(MapPanel mapPanel, Channels channels) {
		this.mapPanel = mapPanel;
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
		if (lineToIdMap != null) {
			lineToIdMap.put(line, channelId);
		}
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
		Polyline polyline = getPolyline(channelId);
		mapPanel.getMap().removeOverlay(polyline);
		lineMap.remove(channelId);
		if (lineToIdMap != null) {
			lineToIdMap.remove(polyline);
		}
	}

	public void addLines() {
		for (Channel data : getChannels().getChannels()) {
			addPolylineForChannel(data);
		}
	}

	public void addChannel(Channel channel) {
		channels.addChannel(channel);
		addPolylineForChannel(channel);
	}

	public Polyline addPolylineForChannel(Channel channel) {

		Node upNode = mapPanel.getNodeManager().getNodeData(
				channel.getUpNodeId());
		Node downNode = mapPanel.getNodeManager().getNodeData(
				channel.getDownNodeId());
		if ((upNode == null) || (downNode == null)) {
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
				WindowUtils.changeCursor("pointer");
			}

		});
		line
				.addPolylineClickHandler(new ChannelClickHandler(channel,
						mapPanel));
		mapPanel.getMap().addOverlay(line);
		return line;
	}

	protected String getLineColor() {
		return "#110077";
	}

	public String getNewChannelId() {
		return (channels.getMaxChannelId() + 1) + "";
	}

	public String getChannelId(Overlay overlay) {
		if (!(overlay instanceof Polyline)) {
			return null;
		}
		if (lineToIdMap == null) {
			lineToIdMap = new HashMap<Polyline, String>();
			for (String id : lineMap.keySet()) {
				lineToIdMap.put(lineMap.get(id), id);
			}
		}
		Polyline line = (Polyline) overlay;
		return lineToIdMap.get(line);
	}

	public XSection getXSectionFor(Overlay overlay) {
		for (XSection xs : xsectionLineMap.keySet()) {
			Polyline polyline = xsectionLineMap.get(xs);
			if (polyline == overlay) {
				return xs;
			}
		}
		return null;
	}

	public Collection<Polyline> getXSectionLines() {
		if (xsectionLineMap != null) {
			return xsectionLineMap.values();
		} else {
			return null;
		}
	}

	public void addXSectionLine(XSection xSection, Polyline line) {
		if (xsectionLineMap == null) {
			xsectionLineMap = new HashMap<XSection, Polyline>();
		}
		xsectionLineMap.put(xSection, line);
	}

	public Collection<XSection> getXSections() {
		return xsectionLineMap.keySet();
	}

	public Polyline getXsectionLineFor(XSection xs) {
		return xsectionLineMap.get(xs);
	}

	public void removeXSection(XSection xSection) {
		Polyline polyline = xsectionLineMap.get(xSection);
		mapPanel.getMap().removeOverlay(polyline);
		Channel channel = channels.getChannel(xSection.getChannelId());
		channel.getXsections().remove(xSection);
		xsectionLineMap.remove(xSection);
	}

	public String getChannelIdForFlowline(Polyline line) {
		// TODO Auto-generated method stub
		return null;
	}

	public void clearXSectionLines() {
		if (xsectionLineMap != null) {
			for (Polyline line : xsectionLineMap.values()) {
				mapPanel.getMap().removeOverlay(line);
			}
			xsectionLineMap.clear();
		}
	}

	public void removeChannel(Channel channel) {
		removePolyline(channel.getId());
		clearXSectionLines();
		channels.removeChannel(channel);
	}
}
