package gov.ca.bdo.modeling.dsm2.map.client.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class TextAnnotation implements Serializable {
	private String text;
	private double lat;
	private double lng;

	public TextAnnotation() {

	}

	public void setLatitude(double lat) {
		this.lat = lat;
	}

	public void setLongitude(double lng) {
		this.lng = lng;
	}

	public double getLatitude() {
		return lat;
	}

	public double getLongitude() {
		return lng;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
