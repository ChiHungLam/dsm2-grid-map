package gov.ca.maps.bathymetry.tiles.server.test;

import junit.framework.TestCase;
import gov.ca.maps.bathymetry.tiles.server.GeomUtils;

public class TestGeomUtils extends TestCase {
	public void testAngle() {
		assertEquals(0, GeomUtils.angle(0, 0, 0, 0));
		assertEquals(0, GeomUtils.angle(0, 0, 5, 0));
		assertEquals(Math.PI / 2, GeomUtils.angle(0, 0, 0, 5));
		assertEquals(Math.PI / 4, GeomUtils.angle(0, 0, 1, 1));
	}

	public void testDistanceBetween() {
		assertEquals(1, GeomUtils.distanceBetween(0, 0, 1, 0));
		assertEquals(0, GeomUtils.distanceBetween(0, 0, 0, 0));
		assertEquals(1, GeomUtils.distanceBetween(0, 0, 0, 1));
	}

	public void testProjectionOfPointOntoLine() {
		double[] p = GeomUtils.projectionOfPointOntoLine(0, 0, 0, 0, 1, 0);
		assertEquals(0, p[0]);
		assertEquals(0, p[1]);
		p=GeomUtils.projectionOfPointOntoLine(0, 0.5, 0, 0, 1, 0);
		assertEquals(0,p[0]);
		assertEquals(0.5,p[1]);
		p=GeomUtils.projectionOfPointOntoLine(2, 0, 0, 0, 1, 0);
		assertEquals(2,p[0]);
		assertEquals(0,p[1]);
		p=GeomUtils.projectionOfPointOntoLine(1, 1, 0, 0, 1, 0);
		assertEquals(1,p[0]);
		assertEquals(1,p[1]);
	}

	protected void assertEquals(double expected, double value) {
		assertTrue(Math.abs(expected - value) < 1e-6);
	}

}
