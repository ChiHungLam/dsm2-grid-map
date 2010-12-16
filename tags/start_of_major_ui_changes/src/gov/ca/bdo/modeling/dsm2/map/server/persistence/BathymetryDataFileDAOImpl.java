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

import gov.ca.bdo.modeling.dsm2.map.server.data.BathymetryDataFile;
import gov.ca.bdo.modeling.dsm2.map.server.utils.GenericDAOImpl;
import gov.ca.modeling.maps.elevation.client.model.CoordinateGeometryUtils;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

public class BathymetryDataFileDAOImpl extends
		GenericDAOImpl<BathymetryDataFile> implements BathymetryDataFileDAO {
	public BathymetryDataFileDAOImpl(PersistenceManager pm) {
		super(pm);
	}

	@SuppressWarnings("unchecked")
	public BathymetryDataFile getFileForLocation(double x, double y)
			throws Exception {
		try {
			int x100 = BathymetryDataFile.roundOff(x);
			int y100 = BathymetryDataFile.roundOff(y);
			String id = x100 + "_" + y100;
			return findObjectById(id);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Gets the bathymetry data files in the vicinity of the line between
	 * (lat1,lng1) and (lat2,lng2) with a width in increments of the lat,lng by
	 * 100 grid.
	 */
	public List<BathymetryDataFile> getFilesAlongLine(double x1, double y1,
			double x2, double y2, int width) throws Exception {
		List<BathymetryDataFile> list = new ArrayList<BathymetryDataFile>();
		// equation of line y=mx+b
		// 
		double m = (y2 - y1) / (x2 - x1);
		double b = y2 - m * x2;
		//
		double xg0 = Math.floor(Math.min(BathymetryDataFile.roundOff(x1),
				BathymetryDataFile.roundOff(x2)));
		double yg0 = Math.floor(Math.min(BathymetryDataFile.roundOff(y1),
				BathymetryDataFile.roundOff(y2)));
		double xgf = Math.ceil(Math.max(BathymetryDataFile.roundOff(x1),
				BathymetryDataFile.roundOff(x2)));
		double ygf = Math.ceil(Math.max(BathymetryDataFile.roundOff(y1),
				BathymetryDataFile.roundOff(y2)));
		double gridSize = BathymetryDataFile.FACTOR;

		double x = xg0;
		// loops below simply cover the smallest rectangle of grids that
		// cover the line completely
		while (x <= xgf) {
			double y = yg0;
			while (y <= ygf) {
				// distance to line ( mx - y + b ) is Math.abs( mx - y +
				// b)/Math.sqrt( m*m+1)
				double[] projections = CoordinateGeometryUtils
						.projectionOfPointOntoLine(x, y, x1, y1, x2, y2);
				double distance = projections[1];
				if (distance <= width * gridSize) {
					BathymetryDataFile bathymetryDataFile = getFileForLocation(
							x, y);
					list.add(bathymetryDataFile);
				}
				//
				y += gridSize;
			}
			x += gridSize;
		}
		return list;
	}

	public List<BathymetryDataFile> getFilesWithin(double xtopleft,
			double ytopleft, double xbottomright, double ybottomright)
			throws Exception {
		try {
			// look for item first else insert a new one
			Query query = getPersistenceManager().newQuery(
					"select from " + BathymetryDataFile.class.getName());
			query.setFilter("x >= xbr && y <= xtl && y==longitudeParam");
			query
					.declareParameters("int northx100, int westLong100, int southx100, int eastLong100");
			// List<BathymetryDataFile> files = (List<BathymetryDataFile>) query
			// .execute(northx100, westLong100, southx100);
			return null;
		} catch (Exception e) {
			throw e;
		}
	}

}
