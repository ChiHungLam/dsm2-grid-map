package gov.ca.bdo.modeling.dsm2.map.server;

import gov.ca.bdo.modeling.dsm2.map.client.service.UserProfileService;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class UserProfileServiceImpl extends RemoteServiceServlet implements
		UserProfileService {
	public void createUserProfile(String email) {
		PersistenceManager persistenceManager = PMF.get()
				.getPersistenceManager();
		try {
			UserProfileDAO dao = new UserProfileDAOImpl(persistenceManager);
			UserProfile userProfile = new UserProfile();
			userProfile.setEmail(email);
			userProfile.setGroup("default");
			userProfile.setAccessLevel("default");
			dao.createObject(userProfile);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			persistenceManager.close();
		}
	}

	public void deleteUserProfile(String email) {
		PersistenceManager persistenceManager = PMF.get()
				.getPersistenceManager();
		try {
			UserProfileDAO dao = new UserProfileDAOImpl(persistenceManager);
			UserProfile userForEmail = dao.getUserForEmail(email);
			if (userForEmail != null) {
				dao.deleteObject(userForEmail);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			persistenceManager.close();
		}
	}

	public List<String> getUserProfiles() {
		ArrayList<String> profiles = new ArrayList<String>();
		PersistenceManager persistenceManager = PMF.get()
				.getPersistenceManager();
		try {
			UserProfileDAO dao = new UserProfileDAOImpl(persistenceManager);
			List<UserProfile> all = dao.findAll();
			for (UserProfile userProfile : all) {
				profiles.add(userProfile.getEmail());
			}
			return profiles;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			persistenceManager.close();
		}
	}

}
