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
package gov.ca.modeling.timeseries.map.server.service;

import gov.ca.modeling.timeseries.map.server.data.persistence.UserProfileDataDAO;
import gov.ca.modeling.timeseries.map.server.data.persistence.UserProfileDataDAOImpl;
import gov.ca.modeling.timeseries.map.shared.data.UserProfileData;
import gov.ca.modeling.timeseries.map.shared.service.ApplicationException;
import gov.ca.modeling.timeseries.map.shared.service.UserProfileService;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class UserProfileServiceImpl extends RemoteServiceServlet implements
		UserProfileService {
	public void createUserProfile(String email) throws ApplicationException {
		checkAdmin();
		try {
			UserProfileDataDAO dao = new UserProfileDataDAOImpl();
			UserProfileData userProfile = new UserProfileData();
			userProfile.setEmail(email);
			userProfile.setGroup("default");
			userProfile.setAccessLevel("default");
			dao.createObject(userProfile);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}

	private void checkAdmin() throws ApplicationException {
		boolean isUserAdmin = UserServiceFactory.getUserService().isUserAdmin();
		if (!isUserAdmin) {
			throw new ApplicationException(
					"You need to be an administrator user");
		}
	}

	public void deleteUserProfile(String email) throws ApplicationException {
		checkAdmin();
		try {
			UserProfileDataDAO dao = new UserProfileDataDAOImpl();
			UserProfileData userForEmail = dao.getUserForEmail(email);
			if (userForEmail != null) {
				dao.deleteObject(userForEmail);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}

	public List<String> getUserProfiles() throws ApplicationException {
		checkAdmin();
		ArrayList<String> profiles = new ArrayList<String>();
		try {
			UserProfileDataDAO dao = new UserProfileDataDAOImpl();
			List<UserProfileData> all = dao.findAll();
			for (UserProfileData userProfile : all) {
				profiles.add(userProfile.getEmail());
			}
			return profiles;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
		}
	}

}
