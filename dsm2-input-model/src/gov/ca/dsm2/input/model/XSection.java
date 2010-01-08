package gov.ca.dsm2.input.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Contains the xsection of a {@link Channel} in {@link XSectionLayer} layers.
 * Contains the distance {@link #getDistance()} at which this xsection is
 * present and the channel id {@link #getChannelId()} to which it belongs
 * 
 * @author nsandhu
 * 
 */
@SuppressWarnings("serial")
public class XSection implements Serializable {
	private String channelId;
	private double distance;
	private final ArrayList<XSectionLayer> layers;

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
