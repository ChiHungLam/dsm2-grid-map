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
import gov.ca.modeling.timeseries.map.shared.data.LoginInfo;
import gov.ca.modeling.timeseries.map.shared.data.UserProfileData;
import gov.ca.modeling.timeseries.map.shared.service.LoginService;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class LoginServiceImpl extends RemoteServiceServlet implements
		LoginService {

	public LoginInfo login(String requestUri) {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		LoginInfo loginInfo = new LoginInfo();

		if (user != null) {
			loginInfo.setLoggedIn(true);
			loginInfo.setEmailAddress(user.getEmail());
			loginInfo.setNickname(user.getNickname());
			loginInfo.setLogoutUrl(userService.createLogoutURL(requestUri));
			lookupOrCreateProfile(user);
			loginInfo.setAdmin(userService.isUserAdmin());
		} else {
			loginInfo.setLoggedIn(false);
			loginInfo.setLoginUrl(userService.createLoginURL(requestUri));
		}
		//
		return loginInfo;

	}

	private void lookupOrCreateProfile(User user) {

		// lookup profile or create one if one not available
		try {
			UserProfileDataDAO dao = new UserProfileDataDAOImpl();
			UserProfileData userForEmail = dao.getUserForEmail(user.getEmail());
			if (userForEmail == null) {
				userForEmail = new UserProfileData();
				userForEmail.setEmail(user.getEmail());
				userForEmail.setGroup("default");
				userForEmail.setAccessLevel("default");
				dao.createObject(userForEmail);
			}
			boolean isUserAdmin = UserServiceFactory.getUserService()
					.isUserAdmin();
			if (isUserAdmin) {
				if (!userForEmail.getAccessLevel().equals("admin")) {
					userForEmail.setAccessLevel("admin");
				}
			} else {
				if (!userForEmail.getAccessLevel().equals("default")) {
					userForEmail.setAccessLevel("default");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}

}
