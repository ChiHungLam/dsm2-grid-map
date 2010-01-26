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
import java.io.PrintWriter;

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

	private UserService userService;

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		String requestURI = httpServletRequest.getRequestURI();
		if (userService.isUserLoggedIn()) {
			User currentUser = userService.getCurrentUser();
			if (isAllowed(currentUser, requestURI)) {
				chain.doFilter(request, response);
			} else {
				PrintWriter writer = response.getWriter();
				writer.println("<h3>Access denied</h3>");
				writer
						.println("<p>Please see admin for access to this app</p>");
			}
		} else {
			if (requestURI.contains("/login")) {
				chain.doFilter(request, response);
			} else {
				httpServletResponse.sendRedirect(userService
						.createLoginURL(requestURI));
			}
		}
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
