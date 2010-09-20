package gov.ca.maps.bathymetry.processor;

import gov.ca.maps.tile.LatLonWithValuesToTiles;

import java.io.IOException;

public class MapLatLonWithValuesToTiles {

	public static void main(String[] args) throws IOException {
		LatLonWithValuesToTiles tiler = new LatLonWithValuesToTiles(Processor
				.getBathymetryLatLngFile());
		for (int i = 4; i < 17; i++) {
			System.out.println("Generating zoom level: " + i);
			tiler.generateForZoom(Processor.getTilesDir(), i);
		}
	}
}
