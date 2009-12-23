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
}
