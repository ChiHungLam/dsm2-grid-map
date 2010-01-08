package gov.ca.dsm2.input.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a reservoir in the model {@link DSM2Model} and contained by
 * {@link Reservoirs}. Its identified by a name {@link #getName()} and defined
 * by an area {@link #getArea()}, a bottom elevation
 * {@link #getBottomElevation()} and its connections
 * {@link #getReservoirConnections()}
 * <p>
 * GIS information is represented by the general location {@link #getLatitude()}
 * and {@link #getLongitude()} and its outline by {@link #getLatLngPoints()}
 * 
 * @author nsandhu
 * 
 */
@SuppressWarnings("serial")
public class Reservoir implements Serializable {
	private String name;
	private double area;
	private double bottomElevation;
	private final ArrayList<ReservoirConnection> reservoirConnections;
	private final ArrayList<double[]> latLngPoints;
	private double latitude;
	private double longitude;

	public Reservoir() {
		reservoirConnections = new ArrayList<ReservoirConnection>();
		latLngPoints = new ArrayList<double[]>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getArea() {
		return area;
	}

	public void setArea(double area) {
		this.area = area;
	}

	public double getBottomElevation() {
		return bottomElevation;
	}

	public void setBottomElevation(double bottomElevation) {
		this.bottomElevation = bottomElevation;
	}

	public void addReservoirConnection(ReservoirConnection connection) {
		reservoirConnections.add(connection);
	}

	public List<ReservoirConnection> getReservoirConnections() {
		return reservoirConnections;
	}

	public void setLatLngPoints(List<double[]> points) {
		this.latLngPoints.clear();
		this.latLngPoints.addAll(points);
	}

	public List<double[]> getLatLngPoints() {
		return this.latLngPoints;
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
