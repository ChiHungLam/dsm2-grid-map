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
import gov.ca.bdo.modeling.dsm2.map.server.utils.GenericDAOImpl;
import gov.ca.modeling.maps.elevation.client.model.CoordinateGeometryUtils;
import gov.ca.modeling.maps.elevation.client.model.DataPoint;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

public class DEMDataFileDAOImpl extends GenericDAOImpl<DEMDataFile> implements
		DEMDataFileDAO {
	public DEMDataFileDAOImpl(PersistenceManager pm) {
		super(pm);
	}

	@SuppressWarnings("unchecked")
	public DEMDataFile getFileForLocation(double x, double y) throws Exception {
		try {
			// look for item first else insert a new one
			Query query = getPersistenceManager().newQuery(
					"select from " + DEMDataFile.class.getName());
			int x100 = (int) DEMDataFile.roundOff(x);
			int y100 = (int) DEMDataFile.roundOff(y);
			query.setFilter("x==xParam && y==yParam");
			query.declareParameters("int xParam, int yParam");
			List<DEMDataFile> files = (List<DEMDataFile>) query.execute(x100,
					y100);
			if ((files == null) || (files.size() == 0)) {
				return null;
			} else {
				return files.get(0);
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Gets the bathymetry data files in the vicinity of the line between
	 * (lat1,lng1) and (lat2,lng2) with a width in increments of the lat,lng by
	 * 100 grid.
	 */
	public List<DEMDataFile> getFilesAlongLine(double x1, double y1, double x2,
			double y2, int width) throws Exception {
		List<DEMDataFile> list = new ArrayList<DEMDataFile>();
		List<DataPoint> points = CoordinateGeometryUtils.getIntersectionOfLineAndGrid(x1, y1, x2, y2, 100);
		for(DataPoint p: points){
			
		}
		// equation of line y=mx+b
		// 
		double m = (y2 - y1) / (x2 - x1);
		double b = y2 - m * x2;
		//
		double xg0 = Math.floor(Math.min(DEMDataFile.roundOff(x1), DEMDataFile
				.roundOff(x2)));
		double yg0 = Math.floor(Math.min(DEMDataFile.roundOff(y1), DEMDataFile
				.roundOff(y2)));
		double xgf = Math.ceil(Math.max(DEMDataFile.roundOff(x1), DEMDataFile
				.roundOff(x2)));
		double ygf = Math.ceil(Math.max(DEMDataFile.roundOff(y1), DEMDataFile
				.roundOff(y2)));
		double gridSize = DEMDataFile.FACTOR;

		double x = xg0;
		// loops below simply cover the smallest rectangle of grids that
		// cover the line completely
		while (x <= xgf) {
			double y = yg0;
			while (y <= ygf) {
				// distance to line ( mx - y + b ) is Math.abs( mx - y +
				// b)/Math.sqrt( m*m+1)
				double[] projections = CoordinateGeometryUtils.projectionOfPointOntoLine(x,
						y, x1, y1, x2, y2);
				double distance = projections[1];
				if (distance <= width * gridSize) {
					DEMDataFile DEMDataFile = getFileForLocation(x, y);
					list.add(DEMDataFile);
				}
				//
				y += gridSize;
			}
			x += gridSize;
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<DEMDataFile> getFilesWithin(double x1, double y1, double x2,
			double y2) throws Exception {
		List<DEMDataFile> files = new ArrayList<DEMDataFile>();
		try {
			int xl = Math.min(DEMDataFile.roundOff(x1), DEMDataFile
					.roundOff(x2));
			int yl = Math.min(DEMDataFile.roundOff(y1), DEMDataFile
					.roundOff(y2));
			int xr = Math.max(DEMDataFile.roundOff(x1), DEMDataFile
					.roundOff(x2));
			int yr = Math.max(DEMDataFile.roundOff(y1), DEMDataFile
					.roundOff(y2));
			// look for item first else insert a new one
			Query query = getPersistenceManager().newQuery(
					"select from " + DEMDataFile.class.getName());
			query.setFilter("(x >= xl && x < xr) && y==yn");
			query
					.declareParameters("int xl, int xr, int yn");
			for(int y = yl; y <= yr; y+=DEMDataFile.FACTOR) {
				files.addAll((List<DEMDataFile>) query.execute(
						xl,xr,y));
			}
			return files;
		} catch (Exception e) {
			e.printStackTrace();
			return files;
		}
	}
}
