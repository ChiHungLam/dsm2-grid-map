package gov.ca.maps.bathymetry.processor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.HashMap;

public class ASCIIGridDEMSplitter {
	private static final int SPLIT_FACTOR = 100;
	private final HashMap<String, int[]> nameToValueMap;

	public static void main(String[] args) {
		if (args.length != 2) {
			System.out
					.println("Usage: ASCIIGridDEMSplitter <DEM file> <output_directory>");
			System.exit(2);
		}
		ASCIIGridDEMSplitter tiler = new ASCIIGridDEMSplitter(args[0], args[1]);
		try {
			tiler.split();
		} catch (IOException ex) {
			ex.printStackTrace();
			return;
		}
	}

	private File inFile;
	private String directory;

	public ASCIIGridDEMSplitter(String inFileName, String directoryName) {
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
		nameToValueMap = new HashMap<String, int[]>();
	}

	public void split() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(inFile));
		String line = reader.readLine();
		int count = 0;
		int ncols = 0;
		int nrows = 0;
		double xllcorner = 0;
		double yllcorner = 0;
		double cellsize = 0;
		int nodataValue = 0;
		while (count < 6) {
			String[] fields = line.split("\\s+");
			if (fields.length != 2) {
				continue;
			}
			if (fields[0].equalsIgnoreCase("ncols")) {
				ncols = Integer.parseInt(fields[1]);
			} else if (fields[0].equalsIgnoreCase("nrows")) {
				nrows = Integer.parseInt(fields[1]);
			} else if (fields[0].equalsIgnoreCase("xllcorner")) {
				xllcorner = Double.parseDouble(fields[1]);
			} else if (fields[0].equalsIgnoreCase("yllcorner")) {
				yllcorner = Double.parseDouble(fields[1]);
			} else if (fields[0].equalsIgnoreCase("cellsize")) {
				cellsize = Double.parseDouble(fields[1]);
			} else if (fields[0].equalsIgnoreCase("nodata_value")) {
				nodataValue = Integer.parseInt(fields[1]);
			}
			line = reader.readLine();
			count++;
		}
		int pct = nrows / 10;
		for (int i = 0; i < nrows; i++) {
			String[] fields = line.split("\\s");
			if (i % pct == 0) {
				System.out.println("Processed " + ((i * 100) / nrows)
						+ " % rows from file " + inFile);
			}
			for (int j = 0; j < fields.length; j++) {
				double x = xllcorner + j * cellsize;
				double y = yllcorner + (nrows - i) * cellsize;
				int depth = 0;
				int rawDepth = Integer.parseInt(fields[j]);
				if (Math.abs(rawDepth - nodataValue) <= 1e-5) {
					depth = -9999;
				} else {
					depth = rawDepth;
				}
				appendToFile(x, y, depth);
			}
			line = reader.readLine();
		}
		closeAll();
		nameToValueMap.clear();
		reader.close();
	}

	private void appendToFile(double x, double y, int depth) throws IOException {
		String file = getSplitFilename(x, y);
		int xo = offSet(x);
		int yo = offSet(y);
		int[] values = nameToValueMap.get(file);
		if (values == null) {
			boolean append = new File(file).exists();
			if (!append) {
				values = initializeNew();
			} else {
				values = readFromFile(file);
			}
			nameToValueMap.put(file, values);
		}
		values[10 * yo + xo] = depth;
		if (nameToValueMap.size() > 1000) {
			closeAll();
			nameToValueMap.clear();
		}
	}

	private int[] initializeNew() {
		int[] values;
		values = new int[100];
		for (int i = 0; i < 100; i++) {
			values[i] = -9999;
		}
		return values;
	}

	private int[] readFromFile(String file) {
		int[] values = null;
		LineNumberReader lnr = null;
		try {
			values = new int[100];
			lnr = new LineNumberReader(new FileReader(file));
			String line = null;
			int count = 0;
			while ((line = lnr.readLine()) != null) {
				String[] cols = line.split("\\s");
				for (String col : cols) {
					values[count++] = Integer.parseInt(col);
				}
			}
		} catch (Exception ex) {
			values = initializeNew();
		} finally {
			if (lnr != null) {
				try {
					lnr.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return values;
	}

	private void writeToFile(String filename, int[] values) {
		PrintWriter wr = null;
		if (allNoDataValues(values)) {
			return;
		}
		try {
			wr = new PrintWriter(new File(filename));
			for (int i = 0; i < 100; i++) {
				wr.print(values[i]);
				wr.print(" ");
				if (i % 10 == 9) {
					wr.println();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (wr != null) {
				wr.close();
			}
		}
	}

	private boolean allNoDataValues(int[] values) {
		for (int value : values) {
			if (value != -9999) {
				return false;
			}
		}
		return true;
	}

	private String getSplitFilename(double x, double y) {
		long x0 = roundDown(x);
		long y0 = roundDown(y);
		return directory + "/s" + x0 + "_" + y0 + ".csv";
	}

	private long roundDown(double val) {
		return (long) Math.floor(val / SPLIT_FACTOR) * SPLIT_FACTOR;
	}

	private int offSet(double val) {
		return (int) (val - roundDown(val) - 5) / 10;
	}

	private void closeAll() {
		for (String filename : nameToValueMap.keySet()) {
			writeToFile(filename, nameToValueMap.get(filename));
		}
	}

}
