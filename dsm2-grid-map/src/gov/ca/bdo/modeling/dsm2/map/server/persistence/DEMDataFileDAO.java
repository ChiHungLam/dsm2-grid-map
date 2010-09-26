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
package gov.ca.bdo.modeling.dsm2.map.server.persistence;

import gov.ca.bdo.modeling.dsm2.map.server.data.DEMDataFile;
import gov.ca.bdo.modeling.dsm2.map.server.utils.GenericDAO;

import java.util.List;

public interface DEMDataFileDAO extends GenericDAO<DEMDataFile> {
	/**
	 * Gets the DEMDataFile that contains the x,y position elevations
	 * 
	 * @param studyName
	 * @return
	 * @throws Exception
	 */
	public DEMDataFile getFileForLocation(double x, double y) throws Exception;

	/**
	 * Gets the DEMDataFile(s) along the line between (x1,y1) and (x2,y2) with a
	 * width in units of the grid 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param width
	 * @return
	 * @throws Exception
	 */
	public List<DEMDataFile> getFilesAlongLine(double x1, double y1, double x2, double y2, int width) throws Exception;

	/**
	 * Gets the DEMDataFile(s) within the rectangle formed by the diagonally opposite corners of
	 * (x1,y1) and (x2,y2)
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 * @throws Exception
	 */
	public List<DEMDataFile> getFilesWithin(double x1,
			double y1, double x2, double y2) throws Exception;
}
