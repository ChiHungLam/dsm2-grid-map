package gov.ca.maps.bathymetry.tiles.server.test;

import gov.ca.maps.bathymetry.tiles.server.GeomUtils;
import junit.framework.TestCase;

public class TestProjectOntoLine extends TestCase {

	public void testProject() {
		double[] xy = new double[2];
		xy[0] = 0.5;
		xy[1] = 3;
		GeomUtils.projectOntoLine(0, 0, 1, 0, xy);
		approxEquals(0.5, xy[0]);
	}

	public static void approxEquals(double expected, double actual) {
		assertTrue(Math.abs(expected - actual) < 0.0000001);
	}
}
