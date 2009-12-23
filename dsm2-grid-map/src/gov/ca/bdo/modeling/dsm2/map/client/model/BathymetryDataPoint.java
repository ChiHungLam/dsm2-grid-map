package gov.ca.bdo.modeling.dsm2.map.client.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class BathymetryDataPoint implements Serializable {
	public double latitude;
	public double longitude;
	public double elevation;
	public int year;
	public String agency;

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(latitude).append(",").append(longitude).append(",")
				.append(elevation);
		builder.append(year).append(",").append(agency);
		return builder.toString();
	}
}
