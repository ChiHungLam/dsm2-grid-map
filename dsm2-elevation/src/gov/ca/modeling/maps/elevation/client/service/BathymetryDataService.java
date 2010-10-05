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
package gov.ca.modeling.maps.elevation.client.service;

import gov.ca.modeling.maps.elevation.client.model.BathymetryDataPoint;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.SerializationException;

@RemoteServiceRelativePath("bathymetry")
public interface BathymetryDataService extends RemoteService {
	public List<BathymetryDataPoint> getBathymetryDataPoints(double latitude,
			double longitude) throws SerializationException;

	/**
	 * Retrieve bathymetry data points along the line between (x1,y1) and
	 * (x2,y2) where x variables are latitude and y variables are longitude
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 * @throws Exception
	 */
	public List<BathymetryDataPoint> getBathymetryDataPointsAlongLine(double x1,
			double y1, double x2, double y2) throws SerializationException;

	/**
	 * Returns the average depth for the given set of coordinates supplied as a
	 * list of double arrays where each array is of two points representing
	 * latitude and longitude.
	 * 
	 * @return
	 * @throws SerializationException
	 */
	public double getAverageDepthInPolygon(List<double[]> points)
			throws SerializationException;
	
	
	public List<BathymetryDataPoint> getBathymetryDataPoints(double northLat, double westLong, double southLat, double eastLong) throws SerializationException;
}
