package gov.ca.maps.bathymetry.processor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.measure.unit.SI;

import org.jscience.geography.coordinates.LatLong;
import org.jscience.geography.coordinates.UTM;
import org.jscience.geography.coordinates.crs.CoordinatesConverter;

public class ConvertLidarUTMToLidarLatLng {
	private static final boolean FIND_LAT_LNGS = false;

	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		final File inputFile;
		if (args.length >= 1) {
			inputFile = new File(args[0]);
		} else {
			System.err
					.println("Needs an argument of the input lidar file full path");
			return;
		}
		if (inputFile.isDirectory()) {
			String[] files = inputFile.list();
			ExecutorService pool = Executors.newFixedThreadPool(10);
			for (final String file : files) {
				Runnable task = new Runnable() {
					public void run() {
						try {
							processLidarFile(new File(inputFile
									.getAbsolutePath()
									+ "/" + file));
						} catch (IOException ioex) {
							ioex.printStackTrace();
						}
					}
				};

				pool.execute(task);
			}
			pool.awaitTermination(0, TimeUnit.SECONDS);
		} else {
			processLidarFile(inputFile);
		}
	}

	private static void processLidarFile(File inputFile)
			throws FileNotFoundException, IOException {
		CoordinatesConverter<UTM, LatLong> utmToLatLong = UTM.CRS
				.getConverterTo(LatLong.CRS);
		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		BufferedWriter writer = new BufferedWriter(new FileWriter(Processor
				.getLidarDataFile()
				+ "." + inputFile.getName(), true));
		String line = null;
		int j = 0;
		// System.out.println("Processing " + inputFile);
		while ((line = reader.readLine()) != null) {
			if (j % 100000 == 0) {
				System.out.println("@ line " + j);
			}
			j++;
			if (line.startsWith(";")) {
				continue;
			}
			String[] fields = line.split("\\s");
			UTM utm = UTM.valueOf(10, 'N', Double.parseDouble(fields[0]),
					Double.parseDouble(fields[1]), SI.METER);
			double elevation = Double.parseDouble(fields[2]);
			elevation = 3.2808399 * elevation;// convert to feet from meters
			LatLong latlng = utmToLatLong.convert(utm);
			double[] coordinates = latlng.getCoordinates();
			if (FIND_LAT_LNGS) {
				System.out.println("In file: " + inputFile + " Lat,Lng = "
						+ coordinates[0] + ", " + coordinates[1]);
				break;
			}
			if (BathymetryDataSplitter.checkIfBathymetryDataExists("",
					coordinates[0], coordinates[1])) {
				fields[0] = coordinates[0] + "";
				fields[1] = coordinates[1] + "";
				fields[2] = elevation + "";
				StringBuilder buf = new StringBuilder();
				buf.append(fields[0]).append(",").append(fields[1]);
				buf.append(",").append(fields[2]).append(",").append("2007")
						.append(",").append("DWR-LIDAR");
				writer.write(buf.toString());
				writer.newLine();
			}
		}
		reader.close();
		writer.close();
	}

}
