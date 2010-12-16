package gov.ca.bdo.modeling.dsm2.map.server.test;

import gov.ca.bdo.modeling.dsm2.map.server.utils.PMF;

import javax.jdo.PersistenceManager;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class BaseDatastoreTestCase extends BaseTestCase {
	protected PersistenceManager persistenceManager;
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig());

	@Override
	public void setUp() throws Exception {
		helper.setUp();
		persistenceManager = PMF.get().getPersistenceManager();
	}

	@Override
	public void tearDown() throws Exception {
		persistenceManager.close();
		helper.tearDown();
	}

}
