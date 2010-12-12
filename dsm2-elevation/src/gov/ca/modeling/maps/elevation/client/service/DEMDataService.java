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

import gov.ca.modeling.maps.elevation.client.model.CalculationState;
import gov.ca.modeling.maps.elevation.client.model.DEMGridSquare;
import gov.ca.modeling.maps.elevation.client.model.DataPoint;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.SerializationException;

@RemoteServiceRelativePath("dem")
public interface DEMDataService extends RemoteService {

	/**
	 * Gets elevation at that point
	 * 
	 * @param latitude
	 * @param longitude
	 * @return elevation in feet
	 * @throws SerializationException
	 */
	public double getElevationAt(double latitude, double longitude)
			throws SerializationException;

	/**
	 * Bilinear interpolated values for elevation
	 * 
	 * @param latitude
	 * @param longitude
	 * @return
	 * @throws SerializationException
	 */
	public double getBilinearInterpolatedElevationAt(double latitude,
			double longitude) throws SerializationException;

	/**
	 * Gets the square 10x10 grid for this location
	 * 
	 * @param latitude
	 * @param longitude
	 * @return
	 * @throws SerializationException
	 */
	public DEMGridSquare getGridAt(double latitude, double longitude)
			throws SerializationException;

	/**
	 * Retrieve elevation data points along the line between (x1,y1) and (x2,y2)
	 * where x variables are latitude and y variables are longitude
	 * 
	 * The data points refer to x,y along the mentioned line in meters and z is
	 * the elevation in feet
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 * @throws Exception
	 */
	public List<DataPoint> getElevationAlong(double x1, double y1, double x2,
			double y2) throws SerializationException;

	public List<DataPoint> getBilinearInterpolatedElevationAlong(double x1,
			double y1, double x2, double y2) throws SerializationException;

	/**
	 * Retrieves all DEM Grid squares that lie within the grid defined by these
	 * diagonally opposite coordinates (x1,y1) and (x2,y2)
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public List<DEMGridSquare> getGridWithin(double x1, double y1, double x2,
			double y2);
	/**
	 * Calculates the average elevation within this area based on the 10mx10m grid
	 * @param points
	 * @return
	 */
	public CalculationState startCalculationOfAverageElevationInArea(List<DataPoint> points);
	
	/**
	 * Calculates the volume beneath a line at elevation calculated based on the 10mx10m grid
	 * @param points
	 * @param elevation
	 * @return
	 */
	public double startCalculationOfVolumeInAreaForElevation(List<DataPoint> points, double elevation);

	/**
	 * Checks status of calculation
	 * @param state
	 * @return
	 */
	public CalculationState checkStatus(CalculationState state);
}
