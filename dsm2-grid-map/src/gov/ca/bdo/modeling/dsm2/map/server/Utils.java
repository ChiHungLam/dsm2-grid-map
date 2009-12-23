package gov.ca.bdo.modeling.dsm2.map.server;

import com.google.appengine.api.users.UserServiceFactory;

public class Utils {
	public static String getCurrentUserEmail() {
		return UserServiceFactory.getUserService().getCurrentUser().getEmail();
	}
}
