package gov.ca.bdo.modeling.dsm2.map.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("admin/userProfile")
public interface UserProfileService extends RemoteService {
	public List<String> getUserProfiles();

	public void createUserProfile(String email);

	public void deleteUserProfile(String email);
}
