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

@SuppressWarnings("serial")
public class Nodes implements Serializable {
	private  ArrayList<Node> nodes;
	private  HashMap<String, Node> nodeMap;

	public Nodes() {
		nodes = new ArrayList<Node>();
		nodeMap = new HashMap<String, Node>();
	}

	public void addNode(Node node) {
		nodes.add(node);
		nodeMap.put(node.getId(), node);
	}

	public Node getNode(String id) {
		return nodeMap.get(id);
	}

	public void removeNode(Node node) {
		nodes.remove(node);
		nodeMap.remove(node.getId());
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public String buildGISTable() {
		StringBuilder buf = new StringBuilder();
		buf.append("NODE_GIS\n");
		buf.append("ID\tLAT_LNG\n");
		for (Node node : nodes) {
			buf.append(node.getId()).append("\t").append(node.getLatitude())
					.append("\t").append(node.getLongitude()).append("\n");
		}
		buf.append("END\n");
		return buf.toString();
	}
}
