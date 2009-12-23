package gov.ca.dsm2.input.model;

import java.io.Serializable;
import java.util.ArrayList;
@SuppressWarnings("serial")
public class XSection implements Serializable{
	private String channelId;
	private double distance;
	private ArrayList<XSectionLayer> layers;

	public XSection() {
		layers = new ArrayList<XSectionLayer>();
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public ArrayList<XSectionLayer> getLayers() {
		return layers;
	}

	public void addLayer(XSectionLayer layer) {
		layers.add(layer);
	}
}
