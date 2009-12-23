package gov.ca.dsm2.input.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InputTable {
	private String name;
	private ArrayList<String> headers;
	private HashMap<String, Integer> headerIndexMap;
	private ArrayList<ArrayList<String>> values;

	public InputTable() {
		this.name = "";
		this.headers = new ArrayList<String>();
		this.headerIndexMap = new HashMap<String, Integer>();
		this.values = new ArrayList<ArrayList<String>>();
	}

	// --- add on get/sets
	public String getValue(int index, String headerName) {
		Integer headerIndex = headerIndexMap.get(headerName);
		if (headerIndex == null) {
			throw new IllegalArgumentException("No header with name: "
					+ headerName + " in table: " + name);
		}
		if (headerIndex.intValue() >= values.get(index).size()){
			return null;
		}
		return values.get(index).get(headerIndex.intValue());
	}

	// --- raw get/sets
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<String> getHeaders() {
		return headers;
	}

	public void setHeaders(List<String> list) {
		this.headers = new ArrayList<String>();
		this.headers.addAll(list);
		headerIndexMap.clear();
		int index = 0;
		for (String header : list) {
			headerIndexMap.put(header, new Integer(index));
			index++;
		}
	}

	public ArrayList<ArrayList<String>> getValues() {
		return values;
	}

	public void setValues(ArrayList<ArrayList<String>> values) {
		this.values = values;
	}
	
	public String toStringRepresentation(){
		StringBuilder builder = new StringBuilder();
		builder.append(getName()).append("\n");
		writeRow(builder, getHeaders());
		builder.append("\n");
		for(ArrayList<String> valueRow: getValues()){
			writeRow(builder, valueRow);
			builder.append("\n");
		}
		builder.append("END\n");
		return builder.toString();
	}

	private void writeRow(StringBuilder builder, ArrayList<String> valueRow) {
		for(String value: valueRow){
			builder.append(value).append("\t");
		}
		builder.deleteCharAt(builder.length()-1);
	}
}
