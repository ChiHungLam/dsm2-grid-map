/**
 *    Copyright (C) 2009, 2010 
 *    State of California,
 *    Department of Water Resources.
 *    This file is part of DSM2 Grid Map
 *    The DSM2 Grid Map is free software: 
 *    you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *    DSM2 Grid Map is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details. [http://www.gnu.org/licenses]
 *    
 *    @author Nicky Sandhu
 *    
 */
package gov.ca.maps.bathymetry.tiles.client.model;

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
