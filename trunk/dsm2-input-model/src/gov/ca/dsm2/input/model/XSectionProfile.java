package gov.ca.dsm2.input.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the GIS information from which the XSection and XSectionLayer(s) are
 * derived.
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

	// ---------- calculation methods ------------//
	public List<XSectionLayer> calculateLayers() {
		ArrayList<XSectionLayer> layers = new ArrayList<XSectionLayer>();
		XSectionLayer layer = new XSectionLayer();
		layer.setElevation(getMinimumElevation());
		layer.setArea(0);
		layer.setTopWidth(0);
		layer.setWettedPerimeter(0);
		layers.add(layer);
		//TODO: 
		//1.calculate rate of change of slope (second difference).
		//2.identify elevations where there are changes above a certain threshold
		//  in rate of change of slope
		//  a.) for elevations spaced closer than 2 feet... combine into one such elevation
		//3. calculate layer for each such elevation.
		return layers;
	}

	public double getMinimumElevation() {
		double depth = Double.MAX_VALUE;
		for (double[] point : profilePoints) {
			depth = Math.min(depth, point[2]);
		}
		return depth;
	}

	public double calculateArea(double elevation) {
		
		return 0;
	}

	public double calculateTopWidth(double elevation) {
		return 0;
	}

	public double calculateWettedPerimeter(double elevation) {
		return 0;
	}
}
