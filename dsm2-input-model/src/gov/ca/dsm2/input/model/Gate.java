package gov.ca.dsm2.input.model;

import java.io.Serializable;
@SuppressWarnings("serial")
public class Gate implements Serializable{
	private String name;
	private String fromObject;
	private String fromIdentifier;
	private String toNode;
	private double latitude;
	private double longitude;
	
	public Gate(){
		
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFromObject() {
		return fromObject;
	}
	public void setFromObject(String fromObject) {
		this.fromObject = fromObject;
	}
	public String getFromIdentifier() {
		return fromIdentifier;
	}
	public void setFromIdentifier(String fromIdentifier) {
		this.fromIdentifier = fromIdentifier;
	}
	public String getToNode() {
		return toNode;
	}
	public void setToNode(String toNode) {
		this.toNode = toNode;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

}
