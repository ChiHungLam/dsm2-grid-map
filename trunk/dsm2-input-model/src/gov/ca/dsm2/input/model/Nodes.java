package gov.ca.dsm2.input.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("serial")
public class Nodes implements Serializable {
	private ArrayList<Node> nodes;
	private HashMap<String, Node> nodeMap;

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
