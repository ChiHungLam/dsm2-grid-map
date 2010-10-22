package gov.ca.dsm2.input.model.test;

import gov.ca.dsm2.input.model.XSectionLayer;
import gov.ca.dsm2.input.model.XSectionProfile;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

public class XSectionProfileTest extends TestCase {

	public void testRectangularXSection() {
		XSectionProfile profile = new XSectionProfile();
		profile.setChannelId(1);
		profile.setDistance(0.25);
		profile.setEndPoints(Arrays.asList(new double[] { 5, 5 }, new double[] {
				10, 5 }));
		profile.setId(275);
		profile.setProfilePoints(Arrays.asList(new double[] { 0, 0 },
				new double[] { 0, -10 }, new double[] { 15, -10 },
				new double[] { 15, 0 }));
		assertApproxEquals(0, profile.calculateArea(-10));
		assertApproxEquals(75, profile.calculateArea(-5));
		assertApproxEquals(150, profile.calculateArea(0));
		//
		assertApproxEquals(15, profile.calculateTopWidth(-10));
		assertApproxEquals(15, profile.calculateTopWidth(-5));
		assertApproxEquals(15, profile.calculateTopWidth(0));
		//
		assertApproxEquals(15, profile.calculateWettedPerimeter(-10));
		assertApproxEquals(25, profile.calculateWettedPerimeter(-5));
		assertApproxEquals(35, profile.calculateWettedPerimeter(0));
		//
		double[] calculateElevations = profile.calculateElevations();
		assertApproxEquals(profile.getMinimumElevation(),
				calculateElevations[0]);
		assertApproxEquals(profile.getMaximumElevation(),
				calculateElevations[1]);
		//
		List<XSectionLayer> layers = profile.calculateLayers();
		assertNotNull(layers);
	}

	public static void assertApproxEquals(double expected, double actual) {
		assertTrue(Math.abs(expected - actual) < 1e-6);
	}
}
