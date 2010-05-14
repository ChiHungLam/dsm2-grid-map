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
package gov.ca.modeling.timeseries.map.server;

import gov.ca.modeling.timeseries.map.server.data.persistence.UserProfileDataDAO;
import gov.ca.modeling.timeseries.map.server.data.persistence.UserProfileDataDAOImpl;
import gov.ca.modeling.timeseries.map.shared.data.UserProfileData;

import java.io.IOException;
import java.util.logging.Logger;

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

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		String requestURI = httpServletRequest.getRequestURI();
		if (userService.isUserLoggedIn()) {
			log.info("requestURI: " + requestURI);
			if (requestURI.contains("/login")
					|| requestURI.contains("/request_access")
					|| requestURI.contains("/logout")) {
				if (requestURI.contains("/logout")) {
					httpServletRequest.getSession().invalidate();
				}
				log.info("calling chain.doFilter " + requestURI);
				chain.doFilter(request, response);
			} else {
				User currentUser = userService.getCurrentUser();
				if (isAllowed(currentUser, requestURI)) {
					log.info("user is allowed: calling chain.doFilter "
							+ requestURI);
					chain.doFilter(request, response);
				} else {
					log.info("user disallowed: calling redirect to "
							+ userService.createLoginURL(requestURI));
					httpServletResponse.sendRedirect(userService
							.createLoginURL(requestURI));
				}
			}
		} else {
			log.info("no user logged in yet");
			if (requestURI.contains("/logout") || requestURI.contains("/login")) {
				log.info("detecting login/logout uri: " + requestURI);
				log.info("calling chain.doFilter");
				if (requestURI.contains("/logout")) {
					httpServletRequest.getSession().invalidate();
				}
				chain.doFilter(request, response);
			} else {
				log.info("calling send redirect to login url: "
						+ userService.createLoginURL(requestURI));
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
		try {
			UserProfileDataDAO dao = new UserProfileDataDAOImpl();
			UserProfileData userForEmail = dao.getUserForEmail(currentUser
					.getEmail());
			if (userForEmail == null) {
				return false;
			} else {
				return true;
			}
		} finally {
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
