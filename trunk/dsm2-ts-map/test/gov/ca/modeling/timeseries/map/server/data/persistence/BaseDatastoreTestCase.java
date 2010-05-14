package gov.ca.modeling.timeseries.map.server.data.persistence;

import junit.framework.TestCase;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class BaseDatastoreTestCase extends TestCase {
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig());

	@Override
	public void setUp() throws Exception {
		helper.setUp();
	}

	@Override
	public void tearDown() throws Exception {
		helper.tearDown();
	}

}
