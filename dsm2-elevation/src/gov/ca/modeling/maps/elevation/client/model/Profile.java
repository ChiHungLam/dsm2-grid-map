package gov.ca.modeling.maps.elevation.client.model;


import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class Profile implements Serializable {
	/**
	 * A unique id given to this profile
	 */
	public long id;
	/**
	 * The points starting from x1,y1 which x representing the distance along
	 * the cross section from (x1,y1) and y representing the elevation and z
	 * representing the perpendicular distance to the xsection (0 in all cases)
	 */
	public List<DataPoint> points;
	/**
	 * The line between x1,y1 and x2,y2 in UTM coordinates is the cross section
	 * line on the surface of the earth.
	 */
	public double x1, y1, x2, y2;
}
