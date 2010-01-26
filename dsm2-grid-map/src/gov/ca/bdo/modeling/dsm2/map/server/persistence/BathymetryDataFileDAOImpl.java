/**
 *   Copyright (C) 2009, 2010 
 *    Nicky Sandhu
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
 *    GNU General Public License for more details.

 *    You should have received a copy of the GNU General Public License
 *    along with DSM2 Grid Map.  If not, see <http://www.gnu.org/licenses>.
 */
package gov.ca.bdo.modeling.dsm2.map.server.persistence;

import gov.ca.bdo.modeling.dsm2.map.server.data.BathymetryDataFile;
import gov.ca.bdo.modeling.dsm2.map.server.utils.GenericDAOImpl;

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
	public BathymetryDataFile getFileForLocation(double latitude,
			double longitude) throws Exception {
		try {
			// look for item first else insert a new one
			Query query = getPersistenceManager().newQuery(
					"select from " + BathymetryDataFile.class.getName());
			int lat100 = (int) Math.ceil(latitude * BathymetryDataFile.FACTOR);
			int lng100 = (int) Math.ceil(longitude * BathymetryDataFile.FACTOR);
			query
					.setFilter("latitude100==latitudeParam && longitude100==longitudeParam");
			query.declareParameters("int latitudeParam, int longitudeParam");
			List<BathymetryDataFile> files = (List<BathymetryDataFile>) query
					.execute(lat100, lng100);
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
	public List<BathymetryDataFile> getFilesAlongLine(double lat1, double lng1,
			double lat2, double lng2, int width) throws Exception {
		try {
			// 
			List<BathymetryDataFile> list = new ArrayList<BathymetryDataFile>();
			for (int i = 0; i < width; i++) {
				double inverseFactor = 1 / (BathymetryDataFile.FACTOR * 1.0);
				double x1 = lat1 + width * inverseFactor;
				double y1 = lng1 + width * inverseFactor;
				double x2 = lat2 + width * inverseFactor;
				double y2 = lng2 + width * inverseFactor;
				// FIXME: for divide by zero error
				double m = (y2 - y1) / (x2 - x1);
				double xn = x1;
				double yn = y1;
				while (xn < x2) {
					BathymetryDataFile bathymetryDataFile = getFileForLocation(
							xn, yn);
					list.add(bathymetryDataFile);
					xn += inverseFactor;
					yn += m * (xn - x1) + y1;
				}
			}
			return list;
		} catch (Exception e) {
			throw e;
		}
	}

}
