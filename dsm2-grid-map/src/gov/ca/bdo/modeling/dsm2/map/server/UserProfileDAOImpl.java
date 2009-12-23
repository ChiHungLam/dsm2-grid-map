package gov.ca.bdo.modeling.dsm2.map.server;


import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

public class UserProfileDAOImpl extends GenericDAOImpl<UserProfile> implements
		UserProfileDAO {
	public UserProfileDAOImpl(PersistenceManager pm) {
		super(pm);
	}

	@SuppressWarnings("unchecked")
	public UserProfile getUserForEmail(String email) {
		UserProfile userProfile = null;
		try {
			// look for item first else insert a new one
			Query query = getPersistenceManager().newQuery(
					"select from " + UserProfile.class.getName());
			query.setFilter("email==emailParam");
			query.declareParameters("String emailParam");
			List<UserProfile> users = (List<UserProfile>) query.execute(email);
			if (users.size() == 1) {
				userProfile = users.get(0);
			}
		} catch (Exception e) {
		}
		return userProfile;
	}

	@SuppressWarnings("unchecked")
	public List<UserProfile> findAll() {
		try {
			// look for item first else insert a new one
			Query query = getPersistenceManager().newQuery(
					"select from " + UserProfile.class.getName());
			return (List<UserProfile>) query.execute();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
