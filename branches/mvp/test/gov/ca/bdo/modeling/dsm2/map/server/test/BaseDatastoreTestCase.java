package gov.ca.bdo.modeling.dsm2.map.server.test;

import gov.ca.bdo.modeling.dsm2.map.server.utils.PMF;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.ApiProxyLocalImpl;
import com.google.apphosting.api.ApiProxy;

public class BaseDatastoreTestCase extends BaseTestCase {
	protected PersistenceManager persistenceManager;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		ApiProxyLocalImpl proxy = (ApiProxyLocalImpl) ApiProxy.getDelegate();
		proxy.setProperty(LocalDatastoreService.NO_STORAGE_PROPERTY,
				Boolean.TRUE.toString());
		LocalDatastoreService datastoreService = (LocalDatastoreService) proxy
				.getService(LocalDatastoreService.PACKAGE);
		PersistenceManagerFactory pmfFactory = PMF.get();
		persistenceManager = pmfFactory.getPersistenceManager();
	}

	@Override
	public void tearDown() throws Exception {
		ApiProxyLocalImpl proxy = (ApiProxyLocalImpl) ApiProxy.getDelegate();
		LocalDatastoreService datastoreService = (LocalDatastoreService) proxy
				.getService(LocalDatastoreService.PACKAGE);
		datastoreService.clearProfiles();
		super.tearDown();
	}

}
