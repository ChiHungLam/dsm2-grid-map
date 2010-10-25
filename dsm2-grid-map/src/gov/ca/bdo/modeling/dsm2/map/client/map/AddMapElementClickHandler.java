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

import com.google.gwt.maps.client.event.MapClickHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Overlay;
import com.google.gwt.user.client.Window;

/**
 * @author nsandhu
 *
 */
public class AddMapElementClickHandler implements MapClickHandler {

	private int type;
	private MapPanel mapPanel;
	private Node previousNode;

	public AddMapElementClickHandler(MapPanel mapPanel, int type) {
		this.mapPanel = mapPanel;
		this.type = type;
	}

	public void onClick(MapClickEvent event) {
		if (type != ElementType.CHANNEL) {
			LatLng latLng = event.getLatLng();
			if (latLng == null) {
				String msg = "You clicked on a marker. If you'd like to add a node, click on the map instead. You can move it overlap later.";
				Window.alert(msg);
				return;
			}
			if (type == ElementType.NODE) {
				NodeMarkerDataManager nodeManager = mapPanel.getNodeManager();
				Node n = new Node();
				n.setId(nodeManager.getNewNodeId());
				n.setLatitude(latLng.getLatitude());
				n.setLongitude(latLng.getLongitude());
				mapPanel.getNodeManager().addNode(n);
			} else if (type==ElementType.GATE){
				GateOverlayManager gateManager = mapPanel.getGateManager();
				Gate g = new Gate();
				g.setName("GATE_"+(gateManager.getNumberOfGates()+1));
				gateManager.addGate(g);
			} else if (type == ElementType.RESERVOIR){
				ReservoirOverlayManager reservoirManager = mapPanel.getReservoirManager();
				Reservoir r = new Reservoir();
				r.setLatitude(latLng.getLatitude());
				r.setLongitude(latLng.getLongitude());
				r.setName("RESERVOIR_"+(reservoirManager.getNumberOfReservoirs()+1));
				reservoirManager.addReservoir(r);
			} else if (type == ElementType.OUTPUT){
				
			}
		} else {
			Overlay overlay = event.getOverlay();
			if (overlay == null) {
				String msg = "You have selected adding a channel and did not click on a node marker";
				Window.alert(msg);
				return;
			}
			NodeMarkerDataManager nodeManager = mapPanel.getNodeManager();
			Node node = nodeManager.getNodeForMarker(overlay);
			if (node == null) {
				String msg = "You have selected adding a channel and clicked on a marker that is not a node!";
				Window.alert(msg);
			}
			if (previousNode == null) {
				previousNode = node;
			} else {
				ChannelLineDataManager channelManager = mapPanel
						.getChannelManager();
				Channel channel = new Channel();
				channel.setUpNodeId(previousNode.getId());
				channel.setDownNodeId(node.getId());
				channel.setId(channelManager.getNewChannelId());
				channelManager.addChannel(channel);
				previousNode = null;
			}
		}
	}

	public void setType(int type) {
		this.type = type;
	}

}
