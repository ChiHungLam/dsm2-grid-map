package gov.ca.maps.bathymetry.processor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * Splits a file containing points referenced by x and y coordinates in meters
 * into 100mx100m grid files where each grid may contain one more such
 * coordinates.
 * 
 * @author nsandhu
 * 
 */
public class BathymetryDataSplitter {

	private final String directory;
	private final HashMap<String, PrintWriter> fileMap;
	private File inFile;
	public static int SPLIT_FACTOR = 100; // SPLIT_FACTORxSPLIT_FACTOR for grid

	// size

	public BathymetryDataSplitter(String inFileName, String directoryName) {
		inFile = new File(inFileName);
		if (!inFile.exists() || !inFile.isFile()) {
			throw new IllegalArgumentException(inFile
					+ " either doesn't exist or is not a file");
		}
		directory = inFile.getParent() + "/" + directoryName;
		File dir = new File(directory);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		fileMap = new HashMap<String, PrintWriter>();
	}

	/**
	 * Expects an argument of an input file in the format where the first two
	 * columns are the coordinates in UTM of easting and northing respectively.
	 * 
	 * A temporary directory is created (if it doesn't exist) at the location of
	 * the input file that will contain files each of which represents a space
	 * of 10mx10m
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String tmpDirName = "bathymetry-split"; // 
		if (args.length != 1) {
			System.err
					.println("Usage BathymetryDataSplitter input_utm_xyz.file");
			System.exit(1);
		}
		String inFileArg = args[0];
		BathymetryDataSplitter splitter = new BathymetryDataSplitter(inFileArg,
				tmpDirName);
		splitter.split();
	}

	public void split() throws IOException {
		String toFilePrefix = "s";
		String fieldSplitter = ",";
		BufferedReader reader = new BufferedReader(new FileReader(inFile));
		String line = reader.readLine();
		int count = 1;
		while ((line = reader.readLine()) != null) {
			if (line.startsWith(";")) {
				System.out.println(line);
				continue;
			}
			String[] fields = line.split(fieldSplitter);
			if (fields.length <= 3) {
				System.err.println("Line: " + line
						+ " has less than required 3 fields: x, y and z");
				continue;
			}
			double x = Double.parseDouble(fields[0]);
			double y = Double.parseDouble(fields[1]);
			StringBuffer csvLine = new StringBuffer();
			for (String field : fields) {
				csvLine.append(field).append(",");
			}
			csvLine.deleteCharAt(csvLine.length() - 1);
			appendToFile(directory + "/"
					+ getBathymetrySplitFileNameFor(toFilePrefix, x, y),
					csvLine.toString());
			if (count % 100000 == 0) {
				System.out.println("Processed " + count + " lines!");
			}
			count++;
		}
		reader.close();
		closeAll();
	}

	public static String getBathymetrySplitFileNameFor(String prefix, double x,
			double y) {
		long x0 = (long) Math.floor(x / SPLIT_FACTOR) * SPLIT_FACTOR;
		long y0 = (long) Math.floor(y / SPLIT_FACTOR) * SPLIT_FACTOR;
		return prefix + "" + toValidString(x0) + "_" + toValidString(y0)
				+ ".csv";
	}

	public static String toValidString(long v) {
		return v < 0 ? "m" + v : "" + v;
	}

	private void closeAll() {
		for (String filename : fileMap.keySet()) {
			PrintWriter writer = fileMap.get(filename);
			if (writer != null) {
				writer.close();
			}
		}
	}

	private void appendToFile(String file, String line) throws IOException {
		PrintWriter writer = fileMap.get(file);
		if (writer == null) {
			boolean append = new File(file).exists();
			writer = new PrintWriter(new FileWriter(file, append));
			fileMap.put(file, writer);
		}
		writer.println(line);
		if (fileMap.size() > 1000) {
			closeAll();
			fileMap.clear();
		}
	}
}
