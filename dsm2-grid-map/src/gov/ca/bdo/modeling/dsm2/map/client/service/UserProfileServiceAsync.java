package gov.ca.bdo.modeling.dsm2.map.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UserProfileServiceAsync {

	void createUserProfile(String email, AsyncCallback<Void> callback);

	void deleteUserProfile(String email, AsyncCallback<Void> callback);

	void getUserProfiles(AsyncCallback<List<String>> callback);

}
