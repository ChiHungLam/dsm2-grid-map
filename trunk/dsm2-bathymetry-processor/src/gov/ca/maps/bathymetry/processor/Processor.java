package gov.ca.maps.bathymetry.processor;

import java.io.File;
import java.util.HashMap;

public class Processor {
	public static int SPLIT_FACTOR = 1000;

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
			tmpDir = "/Users/nsandhu/dev/bathymetry/bathymetry-processing";
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
		return getTempDir() + "/dsm2Nad83Ngvd29-latlng.csv";
	}

	public static String getBathymetryUTMFile() {
		return getTempDir() + "/dsm2Nad83Ngvd29.prn";
	}

	public static String getSplitFilePrefix() {
		return "dsm2h83v88";
	}

	public static String getBathymetrySplitFileNameFor(double latitude,
			double longitude) {
		long lat = (long) Math.floor(latitude * SPLIT_FACTOR);
		long lng = (long) Math.floor(longitude * SPLIT_FACTOR);
		return getSplitFilePrefix() + "" + toValidString(lat) + "_"
				+ toValidString(lng) + ".csv";
	}

	public static String toValidString(long value) {
		String svalue = value + "";
		if (svalue.startsWith("-")) {
			svalue = svalue.replace("-", "m");
		}
		return svalue;
	}

	// ------ utility methods
	public static boolean checkVicinityForBathymetryData(double latitude,
			double longitude) {
		boolean exists = checkIfBathymetryDataExists(latitude, longitude);
		if (exists) {
			return true;
		} else {
			for (int i = -1; i < 2; i += 2) {
				for (int j = -1; j < 2; j += 2) {
					exists = checkIfBathymetryDataExists(latitude + j * 1.0
							/ SPLIT_FACTOR, longitude + i * 1.0 / SPLIT_FACTOR);
					if (exists) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private static HashMap<String, Boolean> fileExists = new HashMap<String, Boolean>();

	public static boolean checkIfBathymetryDataExists(double latitude,
			double longitude) {
		String fileNameFor = getBathymetrySplitFileNameFor(latitude, longitude);
		synchronized (fileExists) {
			if (fileExists.get(fileNameFor) == null) {
				fileExists.put(fileNameFor, new File(
						getBathymetryDataSplitterDir() + "/" + fileNameFor)
						.exists());
			}
			return fileExists.get(fileNameFor);
		}
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
