package gov.ca.maps.bathymetry.processor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * Splits the bathymetry grid into 100x100 for latitude/longitude ~ 1/2mi by 1/2
 * mi grid
 * 
 * @author nsandhu
 * 
 */
public class BathymetryDataSplitter {
	public static int SPLIT_FACTOR = 1000;

	private final String directory;
	private final HashMap<String, PrintWriter> fileMap;

	public BathymetryDataSplitter(String directory) {
		this.directory = directory;
		File dir = new File(directory);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		fileMap = new HashMap<String, PrintWriter>();
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			return;
		}
		String tmpDir = args[0];
		String lngLatFile = args[1];
		BathymetryDataSplitter splitter = new BathymetryDataSplitter(tmpDir);
		splitter.splitFile(lngLatFile);
	}

	public void splitFile(String file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = reader.readLine();
		while ((line = reader.readLine()) != null) {
			String[] fields = line.trim().split("\\s+");
			if (fields.length < 5) {
				System.err
						.println("Line: "
								+ line
								+ " has less than required 5 fields: lat, lon, depth, year, agency");
				continue;
			}
			double lon = Double.parseDouble(fields[0]);
			double lat = Double.parseDouble(fields[1]);
			appendToFile(directory + "/"
					+ getBathymetrySplitFileNameFor(lat, lon), fields);
		}
		reader.close();
		closeAll();
	}

	private void closeAll() {
		for (String filename : fileMap.keySet()) {
			PrintWriter writer = fileMap.get(filename);
			if (writer != null) {
				writer.close();
			}
		}
	}

	private void appendToFile(String file, String[] fields) throws IOException {
		PrintWriter writer = fileMap.get(file);
		if (writer == null) {
			boolean append = new File(file).exists();
			writer = new PrintWriter(new FileWriter(file, append));
			fileMap.put(file, writer);
		}
		writer.print(fields[1]);
		writer.print(",");
		writer.print(fields[0]);
		writer.print(",");
		writer.print(fields[2]);
		writer.print(",");
		writer.print(fields[3]);
		writer.print(",");
		writer.print(fields[4]);
		writer.print("\n");
		if (fileMap.size() > 1000) {
			closeAll();
			fileMap.clear();
		}
	}

	public static String getSplitFilePrefix() {
		return "split";
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

	private static HashMap<String, Boolean> fileExists = new HashMap<String, Boolean>();

	public static boolean checkVicinityForBathymetryData(
			String dataSplitterDir, double latitude, double longitude) {
		boolean exists = checkIfBathymetryDataExists(dataSplitterDir, latitude,
				longitude);
		if (exists) {
			return true;
		} else {
			for (int i = -1; i < 2; i += 2) {
				for (int j = -1; j < 2; j += 2) {
					exists = checkIfBathymetryDataExists(dataSplitterDir,
							latitude + j * 1.0 / SPLIT_FACTOR, longitude + i
									* 1.0 / SPLIT_FACTOR);
					if (exists) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean checkIfBathymetryDataExists(String dataSplitterDir,
			double latitude, double longitude) {
		String fileNameFor = getBathymetrySplitFileNameFor(latitude, longitude);
		synchronized (fileExists) {
			if (fileExists.get(fileNameFor) == null) {
				fileExists.put(fileNameFor, new File(dataSplitterDir + "/"
						+ fileNameFor).exists());
			}
			return fileExists.get(fileNameFor);
		}
	}

}
