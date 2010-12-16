package gov.ca.bdo.modeling.dsm2.map.server.test;

import java.util.List;

import gov.ca.modeling.maps.elevation.client.model.CoordinateGeometryUtils;
import gov.ca.modeling.maps.elevation.client.model.DataPoint;
import junit.framework.TestCase;

public class TestCoordinateGeomtryUtils extends TestCase{

	public void testLineGridIntersection(){
		List<DataPoint> points = CoordinateGeometryUtils.getIntersectionOfLineAndGrid(0, 0, 0, 45, 10);
		assertNotNull(points);
		assertEquals(6, points.size());
		assertEquals(0, points.get(0).x);
		assertEquals(0, points.get(0).y);
		assertEquals(0, points.get(3).x);
		assertEquals(30, points.get(3).y);
		points = CoordinateGeometryUtils.getIntersectionOfLineAndGrid(0, 0, 33, 0, 10);
		assertNotNull(points);
	}
}
