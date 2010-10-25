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

import gov.ca.dsm2.input.model.DSM2Model;
import gov.ca.dsm2.input.model.Gate;
import gov.ca.dsm2.input.model.Gates;
import gov.ca.dsm2.input.model.Node;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.geom.Size;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;

public class GateOverlayManager {
	private Gates gates;
	private final HashMap<String, Marker> gateMarkerMap;
	private MapPanel mapPanel;

	public GateOverlayManager(MapPanel mapPanel) {
		this.mapPanel = mapPanel;
		gateMarkerMap = new HashMap<String, Marker>();

	}

	public void setGates(Gates gates) {
		this.gates = gates;
		gateMarkerMap.clear();
	}

	public void addGateMarker(String gateId, Marker overlay) {
		gateMarkerMap.put(gateId, overlay);
	}

	public Marker getGateMarker(String gateId) {
		return gateMarkerMap.get(gateId);
	}

	public void removeGateMarker(String gateId) {
		gateMarkerMap.remove(gateId);
	}

	public void hideMarkers(boolean hide) {
		for (Marker marker : gateMarkerMap.values()) {
			marker.setVisible(!hide);
		}
	}

	public void addGates() {
		for (Gate gate : gates.getGates()) {
			addMarkerForGate(gate);
		}
	}
	
	public void addGate(Gate gate){
		gates.addGate(gate);
		addMarkerForGate(gate);
	}

	public void addMarkerForGate(Gate gate) {
		// String id = gate.getFromIdentifier();
		String objectType = gate.getFromObject();
		String toNode = gate.getToNode();
		DSM2Model model = mapPanel.getModel();
		if (objectType.equalsIgnoreCase("channel")) {
			Node node = model.getNodes().getNode(toNode);
			if (node == null) {
				GWT.log("Node " + toNode + " is not available for gate "
						+ gate.getName(), null);
				node = new Node();
				node.setId(toNode);
				node.setLatitude(38.5);
				node.setLongitude(-121.5);
			}
			LatLng nodePoint = LatLng.newInstance(node.getLatitude(), node
					.getLongitude());
			if ((gate.getLatitude() == 0) || (gate.getLongitude() == 0)) {
				gate.setLatitude(nodePoint.getLatitude());
				gate.setLongitude(nodePoint.getLongitude());
			}
			LatLng gatePoint = LatLng.newInstance(gate.getLatitude(), gate
					.getLongitude());
			// Create our "tiny" marker icon
			Icon icon = Icon.newInstance("images/dam.png");
			// icon.setShadowURL("images/water.shadow.png");
			icon.setIconSize(Size.newInstance(32, 32));
			icon.setShadowSize(Size.newInstance(22, 20));
			icon.setIconAnchor(Point.newInstance(16, 20));
			icon.setInfoWindowAnchor(Point.newInstance(5, 1));
			MarkerOptions options = MarkerOptions.newInstance();
			options.setTitle(gate.getName());
			options.setIcon(icon);
			// -- edit mode options and only for the marker being
			// manipulated --
			options.setDragCrossMove(true);
			options.setDraggable(true);
			options.setClickable(true);
			options.setAutoPan(true);
			Marker gateOverMarker = new Marker(gatePoint, options);
			gateOverMarker.addMarkerClickHandler(new GateClickHandler(gate,
					mapPanel));
			gateOverMarker
					.addMarkerDragEndHandler(new GateDragHandler(gate));
			this.addGateMarker(gate.getName(), gateOverMarker);
			mapPanel.getMap().addOverlay(gateOverMarker);
		}
	}

	public int getNumberOfGates(){
		return gates.getGates().size();
	}
}
