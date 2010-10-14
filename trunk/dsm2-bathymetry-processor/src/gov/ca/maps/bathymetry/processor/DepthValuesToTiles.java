package gov.ca.maps.bathymetry.processor;

import gov.ca.maps.tile.TileCreator;
import gov.ca.maps.tile.renderer.ShapeColoredAndAlphaedByValueRenderer;
import gov.ca.maps.tile.renderer.TileRenderer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Date;

import javax.measure.unit.SI;

import org.jscience.geography.coordinates.LatLong;
import org.jscience.geography.coordinates.UTM;
import org.jscience.geography.coordinates.crs.CoordinatesConverter;

public class DepthValuesToTiles {
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out
					.println("Usage: DepthValuesToTiles <input_directory> <output_directory>");
			System.exit(2);
		}
		File inputDirectory = new File(args[0]);
		if (!inputDirectory.isDirectory()) {
			System.err
					.println(args[0]
							+ " is not a directory! Please specify an input directory with .xyz files");
			System.exit(3);
		}
		File outputDirectory = new File(args[1]);
		if (!outputDirectory.exists()) {
			outputDirectory.mkdirs();
		}
		while (true) {
			String[] fileList = inputDirectory.list(new FilenameFilter() {

				public boolean accept(File dir, String name) {
					return name.endsWith("xyz");
				}
			});
			if ((fileList == null) || (fileList.length == 0)) {
				break;
			}
			for (String file : fileList) {
				System.out.println("Working on: " + file);
				String filePath = inputDirectory.getAbsolutePath()
						+ File.separator + file;
				DepthValuesToTiles tiler = new DepthValuesToTiles(filePath);
				for (int i = 17; i > 3; i--) {
					System.out.println("Date: " + new Date());
					System.out.println("Generating zoom level: " + i);
					try {
						tiler.generateForZoom(
								outputDirectory.getAbsolutePath(), i);
					} catch (IOException ex) {
						ex.printStackTrace();
						continue;
					}
				}
				new File(filePath).renameTo(new File(filePath + ".processed"));
			}
		}

	}

	private final String file;

	public DepthValuesToTiles(String file) {
		this.file = file;
	}

	public void generateForZoom(String directory, int zoomLevel)
			throws IOException {
		TileCreator tileCreator = new TileCreator(
				directory,
				zoomLevel,
				new TileRenderer[] { new ShapeColoredAndAlphaedByValueRenderer() });
		CoordinatesConverter<UTM, LatLong> utmToLatLong = UTM.CRS
				.getConverterTo(LatLong.CRS);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = reader.readLine();
		String[] values = new String[2];
		while ((line = reader.readLine()) != null) {
			if (line.startsWith(";")) {
				continue;
			}
			String[] fields = line.trim().split(",");
			if (fields.length < 4) {
				System.err
						.println("Line: "
								+ line
								+ " has does not have the 4 required fields: lon, lat, depth, year");
				continue;
			}
			UTM utm = UTM.valueOf(10, 'N', Double.parseDouble(fields[0]),
					Double.parseDouble(fields[1]), SI.METER);
			LatLong latlng = utmToLatLong.convert(utm);
			double[] coordinates = latlng.getCoordinates();
			double lat = coordinates[0];
			double lon = coordinates[1];
			values[0] = Double.parseDouble(fields[2]) + "";
			values[1] = fields[3];
			tileCreator.renderData(lat, lon, values);
		}
		reader.close();
		tileCreator.saveAll();
	}
}
