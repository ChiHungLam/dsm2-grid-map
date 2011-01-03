/**
 *   Copyright (C) 2009, 2010 
 *    Nicky Sandhu
 *    State of California,
 *    Department of Water Resources.
 *    This file is part of DSM2 Grid Map
 *    The DSM2 Grid Map is free software: 
 *    you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *    DSM2 Grid Map is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.

 *    You should have received a copy of the GNU General Public License
 *    along with DSM2 Grid Map.  If not, see <http://www.gnu.org/licenses>.
 */
package gov.ca.bdo.modeling.dsm2.map.server;

import gov.ca.bdo.modeling.dsm2.map.client.service.UserProfileService;
import gov.ca.bdo.modeling.dsm2.map.server.data.UserProfile;
import gov.ca.bdo.modeling.dsm2.map.server.persistence.UserProfileDAO;
import gov.ca.bdo.modeling.dsm2.map.server.persistence.UserProfileDAOImpl;
import gov.ca.bdo.modeling.dsm2.map.server.utils.PMF;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class UserProfileServiceImpl extends RemoteServiceServlet implements
		UserProfileService {
	public UserProfile getUserProfile(String email) {
		PersistenceManager persistenceManager = PMF.get()
				.getPersistenceManager();
		try {
			UserProfileDAO dao = new UserProfileDAOImpl(persistenceManager);
			UserProfile userForEmail = dao.getUserForEmail(email);
			return userForEmail;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			persistenceManager.close();
		}

	}

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

	public void updateUserProfile(UserProfile profile) {
		if ((profile == null) || (profile.getEmail() == null)) {
			return;
		}
		PersistenceManager persistenceManager = PMF.get()
				.getPersistenceManager();
		try {
			UserProfileDAO dao = new UserProfileDAOImpl(persistenceManager);
			UserProfile userForEmail = dao.getUserForEmail(profile.getEmail());
			userForEmail.setAccessLevel(profile.getAccessLevel());
			userForEmail.setGroup(profile.getGroup());
			userForEmail.setNickname(profile.getNickname());
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
