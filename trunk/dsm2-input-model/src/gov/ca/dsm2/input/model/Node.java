package gov.ca.dsm2.input.model;

import java.io.Serializable;
@SuppressWarnings("serial")
public class Node implements Serializable{
	private String id;
	private double latitude;
	private double longitude;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLongitude() {
		return longitude;
	}

}
