package gov.ca.maps.bathymetry.processor;

import gov.ca.maps.tile.TileCreator;
import gov.ca.maps.tile.renderer.ShapeColoredAndAlphaedByValueRenderer;
import gov.ca.maps.tile.renderer.TileRenderer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.measure.unit.SI;

import org.jscience.geography.coordinates.LatLong;
import org.jscience.geography.coordinates.UTM;
import org.jscience.geography.coordinates.crs.CoordinatesConverter;

public class ASCIIGridValuesInUTMToTiles {
	CoordinatesConverter<UTM, LatLong> utmToLatLong = UTM.CRS
			.getConverterTo(LatLong.CRS);

	public static void main(String[] args) {
		if (args.length != 2) {
			System.out
					.println("Usage: ASCIIGridValuesInUTMToTiles <input_directory> <output_directory>");
			System.exit(2);
		}
		File inputDirectory = new File(args[0]);
		if (!inputDirectory.isDirectory()) {
			System.err
					.println(args[0]
							+ " is not a directory! Please specify an input directory with .xyz files");
			System.exit(3);
		}
		final File outputDirectory = new File(args[1]);
		if (!outputDirectory.exists()) {
			outputDirectory.mkdirs();
		}
		String[] fileList = inputDirectory.list(new FilenameFilter() {

			public boolean accept(File dir, String name) {
				return name.endsWith("asc");
			}
		});
		if ((fileList == null) || (fileList.length == 0)) {
			return;
		}
		ThreadPoolExecutor executor = new ThreadPoolExecutor(6, 6, 10,
				TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(500));
		for (String file : fileList) {
			System.out.println("Working on: " + file);
			String filePath = inputDirectory.getAbsolutePath() + File.separator
					+ file;
			for (int i = 15; i > 6; i--) {
				executor.execute(new GenerateTileTask(i, filePath,
						outputDirectory));
			}
			try {
				while (executor.getActiveCount() != 0) {
					Thread.sleep(30000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
			// new File(filePath).renameTo(new File(filePath + ".processed"));
		}

	}

	private final String file;

	public ASCIIGridValuesInUTMToTiles(String file) {
		this.file = file;
	}

	public void generateForZoom(String directory, int zoomLevel)
			throws IOException {
		TileCreator tileCreator = new TileCreator(
				directory,
				zoomLevel,
				new TileRenderer[] { new ShapeColoredAndAlphaedByValueRenderer() });
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = reader.readLine();
		int count = 0;
		int ncols = 0;
		int nrows = 0;
		double xllcorner = 0;
		double yllcorner = 0;
		double cellsize = 0;
		double nodataValue = 0;
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
				nodataValue = Double.parseDouble(fields[1]);
			}
			line = reader.readLine();
			count++;
		}
		String[] values = new String[2];
		values[1] = "2010";
		int pct = nrows / 10;
		for (int i = 0; i < nrows; i++) {
			String[] fields = line.split("\\s");
			if (i % pct == 0) {
				System.out.println("Processed " + ((i * 100) / nrows)
						+ " % rows for " + zoomLevel + " from file " + file);
			}
			for (int j = 0; j < fields.length; j++) {
				double x = xllcorner + j * cellsize;
				double y = yllcorner + (nrows - i) * cellsize;
				double depth = Double.parseDouble(fields[j]);
				if (Math.abs(depth - nodataValue) <= 1e-6) {
					continue;
				}
				// depth value in feet => *100 for centimeters
				values[0] = depth * 3.2808399 + "";
				// if (depth >= 99) {
				// continue;
				// }
				// values[0] = depth / 10.0 + "";
				UTM utm = UTM.valueOf(10, 'N', x, y, SI.METER);
				LatLong latlng = utmToLatLong.convert(utm);
				double[] coordinates = latlng.getCoordinates();
				tileCreator.renderData(coordinates[0], coordinates[1], values);
			}
			line = reader.readLine();
		}
		reader.close();
		tileCreator.saveAll();
	}

	public static class GenerateTileTask implements Runnable {
		int zoomLevel;
		private String filePath;
		private File outputDirectory;

		public GenerateTileTask(int zoomLevel, String filePath,
				File outputDirectory) {
			this.zoomLevel = zoomLevel;
			this.filePath = filePath;
			this.outputDirectory = outputDirectory;
		}

		public void run() {
			int i = zoomLevel;
			System.out.println("Generating zoom level: " + i + " for file "
					+ filePath + " to directory " + outputDirectory);
			ASCIIGridValuesInUTMToTiles tiler = new ASCIIGridValuesInUTMToTiles(
					filePath);
			try {
				tiler.generateForZoom(outputDirectory.getAbsolutePath(), i);
			} catch (IOException ex) {
				ex.printStackTrace();
				return;
			}
		}
	}
}
