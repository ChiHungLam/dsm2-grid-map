package gov.ca.dsm2.input.model;

import java.io.Serializable;

/**
 * A gate in the model {@link DSM2Model} contained in {@link Gates}. Each gate
 * is identified by its name {@link #getName()} and is defined by the object to
 * which its connected {@link #fromObject} and that objects identifier
 * {@link #fromIdentifier}. It further refines that connection to the
 * node/junction {@link #toNode} near which it exists
 * <p>
 * GIS information is represented by its location {@link #getLatitude()} and
 * {@link #getLongitude()}
 * 
 * @author nsandhu
 * 
 */
@SuppressWarnings("serial")
public class Gate implements Serializable {
	private String name;
	private String fromObject;
	private String fromIdentifier;
	private String toNode;
	private double latitude;
	private double longitude;

	public Gate() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFromObject() {
		return fromObject;
	}

	public void setFromObject(String fromObject) {
		this.fromObject = fromObject;
	}

	public String getFromIdentifier() {
		return fromIdentifier;
	}

	public void setFromIdentifier(String fromIdentifier) {
		this.fromIdentifier = fromIdentifier;
	}

	public String getToNode() {
		return toNode;
	}

	public void setToNode(String toNode) {
		this.toNode = toNode;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

}
