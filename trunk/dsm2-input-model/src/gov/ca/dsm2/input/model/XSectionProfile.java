package gov.ca.dsm2.input.model;

import java.util.List;
/**
 * Contains the GIS information from which the XSection and XSectionLayer(s) are derived.
 * 
 * @author nsandhu
 *
 */
public class XSectionProfile {
	private int id;
	private int channelId;
	private double distance;
	private List<double[]> endPoints;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getChannelId() {
		return channelId;
	}
	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public List<double[]> getEndPoints() {
		return endPoints;
	}
	public void setEndPoints(List<double[]> endPoints) {
		this.endPoints = endPoints;
	}
	public List<double[]> getProfilePoints() {
		return profilePoints;
	}
	public void setProfilePoints(List<double[]> profilePoints) {
		this.profilePoints = profilePoints;
	}
	private List<double[]> profilePoints;
}
