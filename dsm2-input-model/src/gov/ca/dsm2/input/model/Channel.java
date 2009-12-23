package gov.ca.dsm2.input.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Channel implements Serializable{
	private String id;
	private double length;
	private double mannings;
	private double dispersion;
	private String upNodeId;
	private String downNodeId;
	private ArrayList<XSection> xsections;
	private ArrayList<double[]> latLngPoints;
	
	public Channel(){
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
	
	public void setLatLngPoints(List<double[]> points){
		this.latLngPoints.clear();
		this.latLngPoints.addAll(points);
	}
	
	public List<double[]> getLatLngPoints(){
		return latLngPoints;
	}

}
