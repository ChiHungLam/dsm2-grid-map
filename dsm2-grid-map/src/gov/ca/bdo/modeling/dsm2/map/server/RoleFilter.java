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
package gov.ca.bdo.modeling.dsm2.map.server;

import gov.ca.bdo.modeling.dsm2.map.server.data.UserProfile;
import gov.ca.bdo.modeling.dsm2.map.server.persistence.UserProfileDAO;
import gov.ca.bdo.modeling.dsm2.map.server.persistence.UserProfileDAOImpl;
import gov.ca.bdo.modeling.dsm2.map.server.utils.PMF;

import java.io.IOException;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * Checks for authorization to access urls
 * 
 * @author nsandhu
 * 
 */
public class RoleFilter implements Filter {
	private static final Logger log = Logger.getLogger(RoleFilter.class
			.getName());
	private UserService userService;
	private static final String[] noLoginNeededURLS = new String[] { "/logout",
			"/login", "welcome.jsp", "user_profile.jsp", "/public",
			"upload_bathymetry", "/request_access", "/task",
			"dsm2_grid_map_view.html", "/dsm2_grid_map", "/dsm2_grid_map/dem",
			"/dsm2_grid_map/bathymetry", "/dsm2_grid_map/dsm2input", ".gwt.rpc" };

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		String requestURI = httpServletRequest.getRequestURI();
		if (userService.isUserLoggedIn()) {
			if (noLoginNeeded(requestURI)) {
				if (requestURI.contains("/logout")) {
					httpServletRequest.getSession().invalidate();
				}
				chain.doFilter(request, response);
			} else {
				User currentUser = userService.getCurrentUser();
				if (isAllowed(currentUser, requestURI)) {
					chain.doFilter(request, response);
				} else {
					log.info("user disallowed: calling redirect to "
							+ userService.createLoginURL(requestURI));
					httpServletResponse.sendRedirect(userService
							.createLoginURL(requestURI));
				}
			}
		} else {
			if (noLoginNeeded(requestURI)) {
				if (requestURI.contains("/logout")) {
					httpServletRequest.getSession().invalidate();
				}
				chain.doFilter(request, response);
			} else {
				log.info("Login needed to access: " + requestURI);
				httpServletResponse.sendRedirect("/welcome.jsp");
			}
		}
	}

	private boolean noLoginNeeded(String requestURI) {
		boolean noLoginNeeded = false;
		for (String element : noLoginNeededURLS) {
			noLoginNeeded = requestURI.contains(element);
			if (noLoginNeeded) {
				break;
			}
		}
		return noLoginNeeded;
	}

	/**
	 * Checks whether this user should be able to access this uri Currently
	 * admins (as defined by appengine rules) can access everything All other
	 * users are at the same authorization level
	 * 
	 * @param currentUser
	 * @param requestURI
	 * @return
	 */
	private boolean isAllowed(User currentUser, String requestURI) {
		if (currentUser == null) {
			return false;
		}
		if (userService.isUserAdmin()) {
			return true;
		}
		if (isAdminOnly(requestURI)) {
			return false;
		}
		PersistenceManager persistenceManager = PMF.get()
				.getPersistenceManager();
		try {
			UserProfileDAO dao = new UserProfileDAOImpl(persistenceManager);
			UserProfile userForEmail = dao.getUserForEmail(currentUser
					.getEmail());
			if (userForEmail == null) {
				return false;
			} else {
				return true;
			}
		} finally {
			persistenceManager.close();
		}
	}

	private boolean isAdminOnly(String requestURI) {
		if ((requestURI != null) && requestURI.endsWith("userProfile")) {
			return true;
		}
		return false;
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		userService = UserServiceFactory.getUserService();
	}

	public void destroy() {
	}

}
