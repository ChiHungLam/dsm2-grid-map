package gov.ca.maps.bathymetry.tiles.server;

public class GeomUtils {

	/**
	 * Draw line thru (x1,y1) and (x2,y2). Project point x,y onto the line =>
	 * drawing a line thru x,y that interects with line thru the points 1 and 2
	 * and is perpendicular to it.
	 * 
	 * If the line is through 1 and 2 is represented by y=mx+b
	 * 
	 * Solving those equations the new xp and yp as
	 * 
	 * xp = (my+x-mb)/(m*m+1), yp = (m*m*y+m*x+b)/(m*m+1)
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param points
	 */
	public static void projectOntoLine(double x1, double y1, double x2,
			double y2, double[] xy) {
		// y = mx+b
		// a1 = m, b1=-1, c1=c
		double x = xy[0];
		double y = xy[1];
		if ((x2 - x1) == 0) {
			xy[0] = x1;
			return;
		}
		double m = (y2 - y1) / (x2 - x1);
		double b = y2 - m * x2;
		double m2 = m * m;
		double m2plus1 = m2 + 1;
		double xp = (m * y + x - m * b) / m2plus1;
		double yp = (m * m * y + m * x + b) / m2plus1;
		xy[0] = xp;
		xy[1] = yp;
	}
}
