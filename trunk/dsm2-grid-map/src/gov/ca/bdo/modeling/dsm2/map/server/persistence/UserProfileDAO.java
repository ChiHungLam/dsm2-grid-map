package gov.ca.bdo.modeling.dsm2.map.server.persistence;

import gov.ca.bdo.modeling.dsm2.map.server.data.UserProfile;
import gov.ca.bdo.modeling.dsm2.map.server.utils.GenericDAO;

import java.util.List;

public interface UserProfileDAO extends GenericDAO<UserProfile> {
	/**
	 * Returns the user's profile for given email
	 * 
	 * @param email
	 * @return
	 */
	public UserProfile getUserForEmail(String email);

	/**
	 * Returns the list of all user profiles
	 * 
	 * @return
	 */
	public List<UserProfile> findAll();
}
