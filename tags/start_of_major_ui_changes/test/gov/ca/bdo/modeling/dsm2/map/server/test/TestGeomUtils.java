package gov.ca.bdo.modeling.dsm2.map.server.test;

import gov.ca.modeling.maps.elevation.client.model.CoordinateGeometryUtils;
import junit.framework.TestCase;

public class TestGeomUtils extends TestCase {
	public void testAngle() {
		assertEquals(0, CoordinateGeometryUtils.angle(0, 0, 0, 0));
		assertEquals(0, CoordinateGeometryUtils.angle(0, 0, 5, 0));
		assertEquals(Math.PI / 2, CoordinateGeometryUtils.angle(0, 0, 0, 5));
		assertEquals(Math.PI / 4, CoordinateGeometryUtils.angle(0, 0, 1, 1));
	}

	public void testDistanceBetween() {
		assertEquals(1, CoordinateGeometryUtils.distanceBetween(0, 0, 1, 0));
		assertEquals(0, CoordinateGeometryUtils.distanceBetween(0, 0, 0, 0));
		assertEquals(1, CoordinateGeometryUtils.distanceBetween(0, 0, 0, 1));
	}

	public void testProjectionOfPointOntoLine() {
		double[] p = CoordinateGeometryUtils.projectionOfPointOntoLine(0, 0, 0, 0, 1, 0);
		assertEquals(0, p[0]);
		assertEquals(0, p[1]);
		p=CoordinateGeometryUtils.projectionOfPointOntoLine(0, 0.5, 0, 0, 1, 0);
		assertEquals(0,p[0]);
		assertEquals(0.5,p[1]);
		p=CoordinateGeometryUtils.projectionOfPointOntoLine(2, 0, 0, 0, 1, 0);
		assertEquals(2,p[0]);
		assertEquals(0,p[1]);
		p=CoordinateGeometryUtils.projectionOfPointOntoLine(1, 1, 0, 0, 1, 0);
		assertEquals(1,p[0]);
		assertEquals(1,p[1]);
	}

	protected void assertEquals(double expected, double value) {
		assertTrue(Math.abs(expected - value) < 1e-6);
	}

}
