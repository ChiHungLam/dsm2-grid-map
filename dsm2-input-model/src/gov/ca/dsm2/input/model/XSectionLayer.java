package gov.ca.dsm2.input.model;

import java.io.Serializable;

/**
 * A layer in the {@link XSection} which defines the elevation
 * {@link #getElevation()}, the top width {@link #getTopWidth()}, the wetted
 * perimeter {@link #getWettedPerimeter()}, the area {@link #getArea()}
 * 
 * @author nsandhu
 * 
 */
@SuppressWarnings("serial")
public class XSectionLayer implements Serializable {
	private double elevation;
	private double area;
	private double topWidth;
	private double wettedPerimeter;

	public double getElevation() {
		return elevation;
	}

	public void setElevation(double elevation) {
		this.elevation = elevation;
	}

	public double getArea() {
		return area;
	}

	public void setArea(double area) {
		this.area = area;
	}

	public double getTopWidth() {
		return topWidth;
	}

	public void setTopWidth(double topWidth) {
		this.topWidth = topWidth;
	}

	public double getWettedPerimeter() {
		return wettedPerimeter;
	}

	public void setWettedPerimeter(double wettedPerimeter) {
		this.wettedPerimeter = wettedPerimeter;
	}
}
