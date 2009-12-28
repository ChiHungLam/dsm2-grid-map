package gov.ca.bdo.modeling.dsm2.map.server.test;

import gov.ca.bdo.modeling.dsm2.map.server.data.UserProfile;
import gov.ca.bdo.modeling.dsm2.map.server.persistence.UserProfileDAO;
import gov.ca.bdo.modeling.dsm2.map.server.persistence.UserProfileDAOImpl;

import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Query;

public class UserProfileDAOTestCase extends BaseDatastoreTestCase {

	public UserProfile addUser(UserProfileDAO dao, int userNumber) {
		UserProfile profile1 = new UserProfile();
		profile1.setEmail("test" + userNumber + "@example.com");
		profile1.setAccessLevel("default");
		return dao.createObject(profile1);
	}

	protected void checkNumberOfUsers(int nusers) {
		Query query = new Query(UserProfile.class.getSimpleName());
		assertEquals(nusers, DatastoreServiceFactory.getDatastoreService()
				.prepare(query).countEntities());
	}

	public void testCreate() {
		UserProfileDAO dao = new UserProfileDAOImpl(persistenceManager);
		addUser(dao, 1);
		checkNumberOfUsers(1);
		addUser(dao, 2);
		checkNumberOfUsers(2);
	}

	public void testDelete() {
		UserProfileDAO dao = new UserProfileDAOImpl(persistenceManager);
		UserProfile user1 = addUser(dao, 1);
		checkNumberOfUsers(1);
		dao.deleteObject(user1);
		checkNumberOfUsers(0);
	}

	public void testRetrieveByEmail() {
		UserProfileDAO dao = new UserProfileDAOImpl(persistenceManager);
		UserProfile user1 = addUser(dao, 1);
		UserProfile user2 = dao.getUserForEmail(user1.getEmail());
		assertEquals(user1.getEmail(), user2.getEmail());
		UserProfile user3 = dao.getUserForEmail(user1.getEmail() + "xx");
		assertNull(user3);
	}

}
