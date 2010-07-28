package gov.ca.maps.bathymetry.processor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import javax.measure.unit.SI;

import org.jscience.geography.coordinates.LatLong;
import org.jscience.geography.coordinates.UTM;
import org.jscience.geography.coordinates.crs.CoordinatesConverter;

public class UTMToLatLng {
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		CoordinatesConverter<UTM, LatLong> utmToLatLong = UTM.CRS
				.getConverterTo(LatLong.CRS);
		BufferedWriter writer = new BufferedWriter(new FileWriter(Processor
				.getBathymetryLatLngFile()));
		BufferedReader reader = new BufferedReader(new FileReader(Processor
				.getBathymetryUTMFile()));
		String line = null;
		int j = 0;
		writer.write("lat,long,depth,year,agency");
		writer.newLine();
		while ((line = reader.readLine()) != null) {
			if (j % 100000 == 0) {
				System.out.println("@ line " + j);
			}
			j++;
			if (line.startsWith(";")) {
				// writer.write(line);
				// writer.newLine();
				continue;
			}
			String[] fields = line.split(",");
			UTM utm = UTM.valueOf(10, 'N', Double.parseDouble(fields[0]),
					Double.parseDouble(fields[1]), SI.METER);
			LatLong latlng = utmToLatLong.convert(utm);
			double[] coordinates = latlng.getCoordinates();
			fields[0] = coordinates[0] + "";
			fields[1] = coordinates[1] + "";
			StringBuilder buf = new StringBuilder();
			buf.append(fields[0]).append(",").append(fields[1]);
			buf.append(",").append(fields[2]);
			buf.append(",").append(fields[3]);
			buf.append(",").append(fields[4]);
			writer.write(buf.toString());
			writer.newLine();
		}
		reader.close();
		writer.close();
	}

}
