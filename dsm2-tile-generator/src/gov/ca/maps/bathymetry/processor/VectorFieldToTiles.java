package gov.ca.maps.bathymetry.processor;

import gov.ca.maps.tile.TileCreator;
import gov.ca.maps.tile.renderer.TileRenderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.HashMap;

import javax.measure.unit.SI;

import org.jscience.geography.coordinates.LatLong;
import org.jscience.geography.coordinates.UTM;
import org.jscience.geography.coordinates.crs.CoordinatesConverter;

public class VectorFieldToTiles {
	public static void main(String[] args) throws Exception {
		// inputs needed
		String inputFileStr = "d:/dev/3dgate-sdip/HORBifurcation.txt";
		double depth = 2.8956;
		int startZoom = 16;
		int endZoom = 21;
		double originEasting = 647218;
		double originNorthing = 4185824;
		//
		File inputFile = new File(inputFileStr);
		String dir = inputFile.getParent();
		dir = dir == null ? "./tiles" : dir + "/tiles";
		//
		CoordinatesConverter<UTM, LatLong> utmToLatLong = UTM.CRS
				.getConverterTo(LatLong.CRS);
		TileCreator[] creators = new TileCreator[endZoom - startZoom + 1];
		for (int i = startZoom; i <= endZoom; i++) {
			creators[i - startZoom] = new TileCreator(dir, i,
					new TileRenderer[] { new VectorArrowMagnitudeColor() });
			creators[i - startZoom].setRenderingWidth(15);
		}
		LineNumberReader lnr = new LineNumberReader(new FileReader(inputFile));
		String line = lnr.readLine();
		String[] fields = line.split(",");
		HashMap<String, Integer> nameToIndexMap = new HashMap<String, Integer>();
		for (int i = 0; i < fields.length; i++) {
			String fieldName = fields[i].trim();
			if (nameToIndexMap.containsKey(fieldName)) {
				continue;
			}
			nameToIndexMap.put(fieldName, new Integer(i));
		}

		int xci = nameToIndexMap.get("x-coordinate");
		int yci = nameToIndexMap.get("y-coordinate");
		int zci = nameToIndexMap.get("z-coordinate");
		int vmi = nameToIndexMap.get("velocity-magnitude");
		int vxi = nameToIndexMap.get("x-velocity");
		int vyi = nameToIndexMap.get("y-velocity");
		int count = 0;
		while ((line = lnr.readLine()) != null) {
			fields = line.split(",");
			double z = Double.parseDouble(fields[zci].trim());
			if (Math.abs(depth - z) < 1e-6) {
				double x = Double.parseDouble(fields[xci].trim());
				double y = Double.parseDouble(fields[yci].trim());
				double vm = Double.parseDouble(fields[vmi].trim());
				double vy = Double.parseDouble(fields[vyi].trim());
				double vx = Double.parseDouble(fields[vxi].trim());
				UTM utm = UTM.valueOf(10, 'N', x + originEasting, y
						+ originNorthing, SI.METER);
				LatLong latlng = utmToLatLong.convert(utm);
				double[] coordinates = latlng.getCoordinates();
				double lat = coordinates[0];
				double lng = coordinates[1];
				String[] values = { vm + "", Math.atan2(vy, vx) + "" };
				for (TileCreator creator : creators) {
					creator.renderData(lat, lng, values);
				}
				count++;
				if (count % 10000 == 0) {
					System.out.println("Processing line # " + count);
				}
			}
		}
		for (TileCreator creator : creators) {
			creator.saveAll();
		}
		lnr.close();
	}

	private static class VectorArrowMagnitudeColor implements TileRenderer {
		public static final double DEGREE_LAT_IN_FEET = 364160.86;
		public static final double DEGREE_LON_IN_FEET = 288163.56;
		public static final double LAT_WIDTH = 50.0 / DEGREE_LAT_IN_FEET;
		public static final double LON_WIDTH = 50.0 / DEGREE_LON_IN_FEET;
		private final HashMap<Object, Object> hints;
		// value and colors in ascending order
		private final double[] value1ColorBoundaries = new double[] { 0, 0.25,
				0.5, 0.75, 1.0 };
		private final Color[] value1Colors = new Color[] { Color.blue,
				Color.green, Color.yellow, Color.orange, Color.red };

		public VectorArrowMagnitudeColor() {
			hints = new HashMap<Object, Object>();
			hints.put(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			hints.put(RenderingHints.KEY_ALPHA_INTERPOLATION,
					RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
			hints.put(RenderingHints.KEY_COLOR_RENDERING,
					RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		}

		public void renderData(BufferedImage image, double[] latLonBounds,
				double lat, double lon, String[] valuesAtLatLon) {
			Graphics2D graphics = (Graphics2D) image.getGraphics();
			graphics.addRenderingHints(hints);
			double latOrigin = latLonBounds[0];
			double lonOrigin = latLonBounds[1];
			double latExtent = latLonBounds[2] - latLonBounds[0];
			double lonExtent = latLonBounds[3] - latLonBounds[1];
			double ly = 256 - (256 * (lat - latOrigin)) / latExtent;
			double lx = (256 * (lon - lonOrigin)) / lonExtent;
			double height = Math.max(10, LAT_WIDTH / latExtent);
			double width = Math.max(10, LON_WIDTH / lonExtent);
			Color colorForValue1 = getColorForValue1(Double
					.parseDouble(valuesAtLatLon[0]));
			Color color = new Color(colorForValue1.getRed(), colorForValue1
					.getGreen(), colorForValue1.getBlue(), 255);
			graphics.setColor(color);
			double angle = Double.parseDouble(valuesAtLatLon[1]);
			drawVector(graphics, lx, ly, angle, color, (height + width) / 4);
		}

		private void drawVector(Graphics2D g, double x, double y, double angle,
				Color c, double size) {
			AffineTransform tr = g.getTransform();
			AffineTransform localTransform = new AffineTransform();
			localTransform.translate(x, y);
			localTransform.rotate(angle);
			g.setTransform(localTransform);
			GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
			path.moveTo(0, 0);
			path.lineTo(-size, size);
			path.lineTo(-size, -size);
			path.lineTo(0, 0);
			path.closePath();
			g.setColor(c);
			g.fill(path);
			GeneralPath stickPath = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
			stickPath.moveTo(-size, 0);
			stickPath.lineTo(-3 * size, 0);
			g.draw(stickPath);
			g.setTransform(tr);
		}

		private Color getColorForValue1(double value) {
			Color color = value1Colors[0];
			int i = 0;
			for (i = 0; i < value1ColorBoundaries.length; i++) {
				if (value < value1ColorBoundaries[i]) {
					break;
				}
			}
			if (i == 0) {
				color = value1Colors[0];
			} else if (i == value1ColorBoundaries.length) {
				color = value1Colors[i - 1];
			} else {
				Color color0 = value1Colors[i - 1];
				double value0 = value1ColorBoundaries[i - 1];
				Color color1 = value1Colors[i];
				double value1 = value1ColorBoundaries[i];
				double red = color0.getRed()
						+ (color1.getRed() - color0.getRed())
						/ (value1 - value0) * (value - value0);
				double green = color0.getGreen()
						+ (color1.getGreen() - color0.getGreen())
						/ (value1 - value0) * (value - value0);
				double blue = color0.getBlue()
						+ (color1.getBlue() - color0.getBlue())
						/ (value1 - value0) * (value - value0);
				color = new Color(constrainToValidValues(red),
						constrainToValidValues(green),
						constrainToValidValues(blue));
			}
			return color;
		}

		private int constrainToValidValues(double d) {
			if (d < 0) {
				return 0;
			} else if (d > 255) {
				return 255;
			} else {
				return (int) Math.round(d);
			}
		}

	}
}
