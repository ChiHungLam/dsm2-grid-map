package gov.ca.bdo.modeling.dsm2.map.server.test;

import junit.framework.TestCase;

public class BaseTestCase extends TestCase {

	@Override
	public void setUp() throws Exception {
		super.setUp();
	}

	@Override
	public void tearDown() throws Exception {
		// not strictly necessary to null these out but there's no harm either
		super.tearDown();
	}
}