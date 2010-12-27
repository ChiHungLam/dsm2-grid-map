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

import gov.ca.dsm2.input.model.Channel;
import gov.ca.dsm2.input.model.Gate;
import gov.ca.dsm2.input.model.Node;
import gov.ca.dsm2.input.model.Reservoir;
import gov.ca.modeling.dsm2.widgets.client.events.MessageEvent;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.maps.client.event.MapClickHandler;
import com.google.gwt.maps.client.event.MapMouseMoveHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Overlay;
import com.google.gwt.maps.client.overlay.Polyline;

/**
 * @author nsandhu
 * 
 */
public class AddMapElementClickHandler implements MapClickHandler {

	private final class ChannelLineMouseMoveHandler implements
			MapMouseMoveHandler {
		private Polyline channelLine;

		public ChannelLineMouseMoveHandler() {
			channelLine = null;
		}
		
		public void startLine(){
			mapPanel.getMap().addMapMouseMoveHandler(this);
		}

		public void onMouseMove(MapMouseMoveEvent event) {
			if (channelLine == null) {
				LatLng p1 = LatLng.newInstance(previousNode.getLatitude(),
						previousNode.getLongitude());
				LatLng p2 = event.getLatLng();
				channelLine = new Polyline(new LatLng[] { p1, p2 },"blue");
				mapPanel.getMap().addOverlay(channelLine);
			}
			channelLine.deleteVertex(1);
			channelLine.insertVertex(1, event.getLatLng());
		}

		public void clearLine() {
			if (channelLine == null) {
				return;
			}
			mapPanel.getMap().removeMapMouseMoveHandler(this);
			mapPanel.getMap().removeOverlay(channelLine);
			
			channelLine=null;
		}
	}

	private int type;
	private MapPanel mapPanel;
	private Node previousNode;
	private EventBus eventBus;
	private ChannelLineMouseMoveHandler channelLineHandler;

	public AddMapElementClickHandler(MapPanel mapPanel, int type,
			EventBus eventBus) {
		this.mapPanel = mapPanel;
		this.type = type;
		this.eventBus = eventBus;
	}

	public void onClick(MapClickEvent event) {
		if (type != ElementType.CHANNEL) {
			LatLng latLng = event.getLatLng();
			if (latLng == null) {
				String msg = "You clicked on a marker. If you'd like to add a node, click on the map instead. You can move it overlap later.";
				eventBus.fireEvent(new MessageEvent(msg));
				return;
			}
			if (type == ElementType.NODE) {
				NodeMarkerDataManager nodeManager = mapPanel.getNodeManager();
				Node n = new Node();
				n.setId(nodeManager.getNewNodeId());
				n.setLatitude(latLng.getLatitude());
				n.setLongitude(latLng.getLongitude());
				mapPanel.getNodeManager().addNode(n);
			} else if (type == ElementType.GATE) {
				GateOverlayManager gateManager = mapPanel.getGateManager();
				Gate g = new Gate();
				g.setName("GATE_" + (gateManager.getNumberOfGates() + 1));
				g.setLatitude(latLng.getLatitude());
				g.setLongitude(latLng.getLongitude());
				// FIXME: how is the gate to be associated with a channel or
				// reservoir?
				// g.setFromObject(fromObject);
				// g.setFromIdentifier();
				gateManager.addGate(g);
			} else if (type == ElementType.RESERVOIR) {
				ReservoirOverlayManager reservoirManager = mapPanel
						.getReservoirManager();
				Reservoir r = new Reservoir();
				r.setLatitude(latLng.getLatitude());
				r.setLongitude(latLng.getLongitude());
				r.setName("RESERVOIR_"
						+ (reservoirManager.getNumberOfReservoirs() + 1));
				reservoirManager.addReservoir(r);
			} else if (type == ElementType.OUTPUT) {

			} else if (type == ElementType.XSECTION){
				Overlay overlay = event.getOverlay();
				if (overlay == null){
					String msg = "To add a xsection, first click on a channel line to select it";
					eventBus.fireEvent(new MessageEvent(msg));
					return;
				}
				if (!(overlay instanceof Polyline)){
					String msg = "To add a xsection, first click on a channel line to select it. You clicked on a marker?";
					eventBus.fireEvent(new MessageEvent(msg));
					return;
				}
				Polyline line = (Polyline) overlay;
				String channelId = mapPanel.getChannelManager().getChannelId(line);
				if (channelId == null){
					String channelIdForFlowline = mapPanel.getChannelManager().getChannelIdForFlowline(line);
					if (channelIdForFlowline == null){
						String msg = "To add a xsection, you must click on a spot on the channel's flowline. You clicked on some other line?";
						eventBus.fireEvent(new MessageEvent(msg));
						return;
					}
				}
				//FIXME: find the distance at which the xsection is to be added and draw a line of 
				// a certain width (say 1000ft) perpendicular to the flowline.
			}
		} else {
			Overlay overlay = event.getOverlay();
			if (overlay == null) {
				String msg = "You have selected adding a channel and did not click on a node marker";
				eventBus.fireEvent(new MessageEvent(msg));
				return;
			}
			NodeMarkerDataManager nodeManager = mapPanel.getNodeManager();
			Node node = nodeManager.getNodeForMarker(overlay);
			if (node == null) {
				String msg = "You have selected adding a channel and clicked on a marker that is not a node!";
				eventBus.fireEvent(new MessageEvent(msg));
				return;
			}
			if (previousNode == null) {
				String msg = "Adding a channel with upnode (" + node.getId()
						+ "). Now click on downnode.";
				eventBus.fireEvent(new MessageEvent(msg));
				if (channelLineHandler == null){
					channelLineHandler = new ChannelLineMouseMoveHandler();
				}
				channelLineHandler.startLine();
				previousNode = node;
			} else {
				String msg = "Adding channel with upnode ("
						+ previousNode.getId() + ") and downnode ("
						+ node.getId() + ")";
				eventBus.fireEvent(new MessageEvent(msg));
				ChannelLineDataManager channelManager = mapPanel
						.getChannelManager();
				Channel channel = new Channel();
				channel.setUpNodeId(previousNode.getId());
				channel.setDownNodeId(node.getId());
				channel.setId(channelManager.getNewChannelId());
				channelManager.addChannel(channel);
				channelLineHandler.clearLine();
				previousNode = null;
			}
		}
	}

	public void setType(int type) {
		this.type = type;
	}

}
