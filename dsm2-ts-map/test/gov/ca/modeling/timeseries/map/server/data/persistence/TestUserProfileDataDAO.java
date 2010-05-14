package gov.ca.modeling.timeseries.map.server.data.persistence;

import gov.ca.modeling.timeseries.map.shared.data.UserProfileData;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

public class TestUserProfileDataDAO extends BaseDatastoreTestCase {

	public UserProfileData addUser(UserProfileDataDAO dao, int userNumber) {
		UserProfileData profile1 = new UserProfileData();
		profile1.setEmail("test" + userNumber + "@example.com");
		profile1.setAccessLevel("default");
		return dao.createObject(profile1);
	}

	protected void checkNumberOfUsers(int nusers) {
		Objectify ofy = ObjectifyService.begin();
		assertEquals(nusers, ofy.query(UserProfileData.class).countAll());
	}

	public void testCreate() {
		UserProfileDataDAO dao = new UserProfileDataDAOImpl();
		addUser(dao, 1);
		checkNumberOfUsers(1);
		addUser(dao, 2);
		checkNumberOfUsers(2);
	}

	public void testDelete() {
		UserProfileDataDAO dao = new UserProfileDataDAOImpl();
		UserProfileData user1 = addUser(dao, 1);
		checkNumberOfUsers(1);
		dao.deleteObject(user1);
		checkNumberOfUsers(0);
	}

	public void testRetrieveByEmail() {
		UserProfileDataDAO dao = new UserProfileDataDAOImpl();
		UserProfileData user1 = addUser(dao, 1);
		UserProfileData user2 = dao.getUserForEmail(user1.getEmail());
		assertEquals(user1.getEmail(), user2.getEmail());
		UserProfileData user3 = dao.getUserForEmail(user1.getEmail() + "xx");
		assertNull(user3);
	}

}
