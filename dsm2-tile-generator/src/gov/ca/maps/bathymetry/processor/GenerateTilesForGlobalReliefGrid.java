package gov.ca.maps.bathymetry.processor;

import gov.ca.maps.tile.TileCreator;
import gov.ca.maps.tile.renderer.ShapeColoredByLogValueRenderer;
import gov.ca.maps.tile.renderer.TileRenderer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;

public class GenerateTilesForGlobalReliefGrid {
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out
					.println("Usage: GenerateTilesForGlobalReliefGrid <input_directory> <output_directory>");
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
				GenerateTilesForGlobalReliefGrid tiler = new GenerateTilesForGlobalReliefGrid(
						filePath);
				for (int i = 4; i < 15; i++) {
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

	public GenerateTilesForGlobalReliefGrid(String file) {
		this.file = file;
	}

	public void generateForZoom(String directory, int zoomLevel)
			throws IOException {
		TileCreator tileCreator = new TileCreator(directory, zoomLevel,
				new TileRenderer[] { new ShapeColoredByLogValueRenderer() });
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = reader.readLine();
		String[] values = new String[1];
		while ((line = reader.readLine()) != null) {
			String[] fields = line.split(" ");
			if (fields.length != 3) {
				System.err
						.println("Line: "
								+ line
								+ " has does not have the 3 required fields: lon, lat, depth");
				continue;
			}
			double lon = Double.parseDouble(fields[0]);
			double lat = Double.parseDouble(fields[1]);
			values[0] = Double.parseDouble(fields[2]) * 3.2808399 + "";
			tileCreator.renderData(lat, lon, values);
		}
		reader.close();
		tileCreator.saveAll();
	}
}
