package gov.ca.maps.bathymetry.processor;

import java.io.File;

public class Processor {

	/**
	 * Defines the location of the temporary directory for all processing tasks
	 * 
	 * @return
	 */
	public static String getTempDir() {
		String tmpDir = "";
		if (System.getProperty("os.name").contains("Window")) {
			tmpDir = "d:/temp/bathymetry-processing";
		} else {
			tmpDir = "/Users/nsandhu/tmp/bathymetry-processing";
		}
		ensureDirectory(tmpDir);
		return tmpDir;
	}

	/**
	 * Defines the location where tiles are generated
	 */
	public static String getTilesDir() {
		return getTempDir() + "/bathymetry-tiles";
	}

	/**
	 * Defines the location where data split by lat/lon is stored
	 */
	public static String getBathymetryDataSplitterDir() {
		return getTempDir() + "/bathymetry-data-split";
	}

	public static String getLidarDataFile() {
		return getTempDir() + "/lidar-data-near-bathymetry.csv";
	}

	public static String getBathymetryLatLngFile() {
		return getTempDir() + "/dsm2Nad83Navd88-latlng.csv";
	}

	public static String getBathymetryUTMFile() {
		return "resources/dsm2Nad83Navd88.prn";
	}

	public static boolean ensureDirectory(String pathname) {
		File directory = new File(pathname);
		if (!directory.exists()) {
			return directory.mkdirs();
		} else {
			return true;
		}
	}
}
