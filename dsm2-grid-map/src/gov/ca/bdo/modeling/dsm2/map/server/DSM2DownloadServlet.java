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

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class DSM2DownloadServlet extends HttpServlet {
	/**
	 * The request should contain two parameters : studyName and the inpuName
	 * (gis_inp or hydro_echo_inp)
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String studyName = req.getParameter("studyName");
		String inputName = req.getParameter("inputName");
		String studyKey = req.getParameter("studyKey");
		resp.setContentType("text/plain");
		// inputName = hydro_echo_inp | gis_inp
		String filename = inputName.replace("_inp", ".inp");
		resp.setHeader("Content-Disposition", "attachment; filename="
				+ filename);
		if ((studyKey == null) || studyKey.equals("")) {
			resp.getWriter().write(
					new DSM2InputServiceImpl().showInput(studyName, inputName));
		} else {
			resp.getWriter().write(
					new DSM2InputServiceImpl().showInputForKey(studyKey,
							inputName));
		}
	}

}
