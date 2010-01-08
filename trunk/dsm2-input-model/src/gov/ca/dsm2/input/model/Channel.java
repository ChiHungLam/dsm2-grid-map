package gov.ca.dsm2.input.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * In DSM2 a channel is an entity representing a stream with a in stream length
 * {@link #getLength()}, roughness coefficient (mannings n)
 * {@link #getMannings()}, dispersion factor {@link #getDispersion()} for water
 * quality constituents
 * 
 * It is defined by an id {@link #getId()} and its two end nodes, up node
 * {@link #getUpNodeId()} and down node {@link #getDownNodeId()}. It also has a
 * number of xsections {@link #getXsections()} and contains the gis
 * {@link #getLatLngPoints()} information to demarcate the internal points
 * 
 * @author nsandhu
 * 
 */
@SuppressWarnings("serial")
public class Channel implements Serializable {
	private String id;
	private double length;
	private double mannings;
	private double dispersion;
	private String upNodeId;
	private String downNodeId;
	private final ArrayList<XSection> xsections;
	private final ArrayList<double[]> latLngPoints;

	public Channel() {
		this.xsections = new ArrayList<XSection>();
		latLngPoints = new ArrayList<double[]>();
	}

	public String getId() {
		return id;
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public double getMannings() {
		return mannings;
	}

	public void setMannings(double mannings) {
		this.mannings = mannings;
	}

	public double getDispersion() {
		return dispersion;
	}

	public void setDispersion(double dispersion) {
		this.dispersion = dispersion;
	}

	public String getUpNodeId() {
		return upNodeId;
	}

	public void setUpNodeId(String upNodeId) {
		this.upNodeId = upNodeId;
	}

	public String getDownNodeId() {
		return downNodeId;
	}

	public void setDownNodeId(String downNodeId) {
		this.downNodeId = downNodeId;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ArrayList<XSection> getXsections() {
		return xsections;
	}

	public void addXSection(XSection xsection) {
		this.xsections.add(xsection);
	}

	public void setLatLngPoints(List<double[]> points) {
		this.latLngPoints.clear();
		this.latLngPoints.addAll(points);
	}

	public List<double[]> getLatLngPoints() {
		return latLngPoints;
	}

}
