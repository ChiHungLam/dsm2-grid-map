package gov.ca.maps.bathymetry.tiles.server;

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
			int lat100 = (int) Math.floor(latitude * BathymetryDataFile.FACTOR);
			int lng100 = (int) Math
					.floor(longitude * BathymetryDataFile.FACTOR);
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
		List<BathymetryDataFile> list = new ArrayList<BathymetryDataFile>();
		// equation of line y=mx+b
		// 
		double m = (lng2 - lng1) / (lat2 - lat1);
		double b = lng2 - m * lat2;
		//
		double xg0 = Math.floor(Math.min(lat1 * BathymetryDataFile.FACTOR, lat2
				* BathymetryDataFile.FACTOR))
				/ BathymetryDataFile.FACTOR;
		double yg0 = Math.floor(Math.min(lng1 * BathymetryDataFile.FACTOR, lng2
				* BathymetryDataFile.FACTOR))
				/ BathymetryDataFile.FACTOR;
		double xgf = Math.ceil(Math.max(lat1 * BathymetryDataFile.FACTOR, lat2
				* BathymetryDataFile.FACTOR))
				/ BathymetryDataFile.FACTOR;
		double ygf = Math.ceil(Math.max(lng1 * BathymetryDataFile.FACTOR, lng2
				* BathymetryDataFile.FACTOR))
				/ BathymetryDataFile.FACTOR;
		double gridSize = 1 / (BathymetryDataFile.FACTOR * 1.0);

		double x = xg0;
		// loops below simply cover the smallest rectangle of grids that
		// cover the line completely
		while (x <= xgf) {
			double y = yg0;
			while (y <= ygf) {
				// distance to line ( mx - y + b ) is Math.abs( mx - y +
				// b)/Math.sqrt( m*m+1)
				double distance = Math.abs(m * x - y + b)
						/ Math.sqrt(m * m + 1);
				if (distance <= 2 * gridSize) {
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

}
