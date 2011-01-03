package gov.ca.bdo.modeling.dsm2.map.server;

import gov.ca.bdo.modeling.dsm2.map.server.data.UserProfile;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class UpdateCreateUserProfile extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String nickname = request.getParameter("nickname");
		UserProfileServiceImpl profileService = new UserProfileServiceImpl();
		User currentUser = UserServiceFactory.getUserService().getCurrentUser();
		if (currentUser == null) {
			response.sendRedirect("welcome.jsp");
			return;
		}
		UserProfile profile = profileService.getUserProfile(currentUser
				.getEmail());
		if (profile == null) {
			profileService.createUserProfile(currentUser.getEmail());
			profile = profileService.getUserProfile(currentUser.getEmail());
			if (profile == null) {
				System.err.println("No profile found for user: "
						+ currentUser.getEmail());
				response.sendRedirect("welcome.jsp");
				return;
			}
			if ((nickname != null) && !nickname.trim().equals("")) {
				profile.setNickname(nickname);
			}
			profile.setAccessLevel("default");
			profile.setGroup("default");
			profileService.updateUserProfile(profile);
		}
		response.sendRedirect("dsm2_grid_map.html");
	}
}
