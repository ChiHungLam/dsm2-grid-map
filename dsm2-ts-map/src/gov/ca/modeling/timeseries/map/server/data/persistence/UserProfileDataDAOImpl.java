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
package gov.ca.modeling.timeseries.map.server.data.persistence;

import gov.ca.modeling.server.utils.GenericDAOImpl;
import gov.ca.modeling.timeseries.map.shared.data.UserProfileData;

import java.util.List;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

public class UserProfileDataDAOImpl extends GenericDAOImpl<UserProfileData>
		implements UserProfileDataDAO {

	static {
		ObjectifyService.register(UserProfileData.class);
	}

	public UserProfileData getUserForEmail(String email) {
		Objectify ofy = ObjectifyService.begin();
		UserProfileData userProfileData = ofy.query(UserProfileData.class)
				.filter("email", email).get();
		return userProfileData;
	}

	public List<UserProfileData> findAll() {
		Objectify ofy = ObjectifyService.begin();
		List<UserProfileData> result = ofy.query(UserProfileData.class).list();
		return result;
	}

}
