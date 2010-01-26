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
package gov.ca.bdo.modeling.dsm2.map.server.persistence;

import gov.ca.bdo.modeling.dsm2.map.server.data.UserProfile;
import gov.ca.bdo.modeling.dsm2.map.server.utils.GenericDAOImpl;

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
