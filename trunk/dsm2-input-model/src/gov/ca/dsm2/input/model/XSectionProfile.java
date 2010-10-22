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
	private static final int MAX_LAYERS = 10;
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
		// 3. calculate layer for each such elevation.
		return layers;
	}

	public double getMinimumElevation() {
		double depth = Double.MAX_VALUE;
		for (double[] point : profilePoints) {
			depth = Math.min(depth, point[2]);
		}
		return depth;
	}

	public double getMaximumElevation() {
		double depth = Double.MIN_VALUE;
		for (double[] point : profilePoints) {
			depth = Math.max(depth, point[2]);
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

	/**
	 * Calculate and return the elevations that are important in defining the
	 * cross section from a DSM2 prespective.
	 * <p>
	 * TODO: Improve this by calculating derivative of slope and finding out
	 * inflexion points where slope changes faster than a certain threshold.
	 * <ul>
	 * <li>calculate rate of change of slope (second difference).</li>
	 * <li>identify elevations where there are changes above a certain threshold
	 * in rate of change of slope</li>
	 * <li>for elevations spaced closer than 2 feet... combine into one such
	 * elevation</li>
	 * </ul>
	 * 
	 * @return
	 */
	public double[] calculateElevations() {
		double minElevation = getMinimumElevation();
		double maxElevation = getMaximumElevation();
		double stepSize = (maxElevation - minElevation) / MAX_LAYERS;
		if (stepSize < 2) {
			stepSize = 2;
		}
		int nlayers = (int) Math.ceil((maxElevation - minElevation) / stepSize);
		double[] elevations = new double[nlayers];
		for (int i = 0; i < nlayers; i++) {
			elevations[i] = Math.min(minElevation + i * stepSize, maxElevation);
		}
		return elevations;
	}
}
