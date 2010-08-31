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
package gov.ca.maps.bathymetry.tiles.server;

import java.util.List;

public interface BathymetryDataFileDAO extends GenericDAO<BathymetryDataFile> {
	/**
	 * Get all bathymetry data files for the grid in which latitude, longitude
	 * falls This implies multiplying lat,lng by 100 and then taking the floor
	 * to calculate the grid filename
	 * 
	 * @param studyName
	 * @return
	 * @throws Exception
	 */
	public BathymetryDataFile getFileForLocation(double latitude,
			double longitude) throws Exception;

	/**
	 * Gets the bathymetry data files in the vicinity of the line between
	 * (lat1,lng1) and (lat2,lng2) with a width in increments of the lat,lng by
	 * 100 grid.
	 */
	public List<BathymetryDataFile> getFilesAlongLine(double lat1, double lng1,
			double lat2, double lng2, int width) throws Exception;
	
	/**
	 * Gets the bathymetry data within the north,west and south,east rectangular bounds
	 * @param northLat
	 * @param westLong
	 * @param southLat
	 * @param eastLong
	 * @return
	 * @throws Exception
	 */
	public List<BathymetryDataFile> getFilesWithin(double northLat, double westLong, double southLat, double eastLong) throws Exception;
}
