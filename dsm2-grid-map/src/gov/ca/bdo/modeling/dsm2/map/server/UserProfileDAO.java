package gov.ca.bdo.modeling.dsm2.map.server;


import java.util.List;

public interface UserProfileDAO extends GenericDAO<UserProfile> {
	public UserProfile getUserForEmail(String email);

	public List<UserProfile> findAll();
}
