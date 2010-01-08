package gov.ca.dsm2.input.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A container for all the {@link Gate} objects with ability to retrieve them
 * all {@link #getGates()}. Retrieval by name/id {@link #getGate(String)} is
 * also available.
 * 
 * @author nsandhu
 * 
 */
@SuppressWarnings("serial")
public class Gates implements Serializable {
	private final ArrayList<Gate> gates;
	private final HashMap<String, Gate> gatesMap;

	public Gates() {
		gates = new ArrayList<Gate>();
		gatesMap = new HashMap<String, Gate>();
	}

	public void addGate(Gate gate) {
		gates.add(gate);
		gatesMap.put(gate.getName(), gate);
	}

	public List<Gate> getGates() {
		return gates;
	}

	public Gate getGate(String name) {
		return gatesMap.get(name);
	}

	public String buildGISTable() {
		StringBuilder buf = new StringBuilder();
		buf.append("GATE_GIS\n");
		buf.append("ID\tLAT_LNG\n");
		for (Gate gate : gates) {
			buf.append(gate.getName()).append("\t");
			buf.append("(").append(gate.getLatitude());
			buf.append(",").append(gate.getLongitude()).append(")");
			buf.append("\n");
		}
		buf.append("END\n");
		return buf.toString();
	}
}
