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

import gov.ca.bdo.modeling.dsm2.map.server.data.DSM2ModelFile;
import gov.ca.bdo.modeling.dsm2.map.server.persistence.DSM2ModelFileDAOImpl;
import gov.ca.bdo.modeling.dsm2.map.server.utils.PMF;
import gov.ca.bdo.modeling.dsm2.map.server.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

import com.google.appengine.api.datastore.Text;

public class FileUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 8367618333138027430L;
	private static final int MAX_SIZE_BYTES = 1024 * 1024;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		PrintWriter out = resp.getWriter();
		out.print("<p>Error: The request method <code>" + req.getMethod()
				+ "</code> is inappropriate for the URL <code>"
				+ req.getRequestURI() + "</code></p>");
		out.close();
	}

	@SuppressWarnings( { "deprecation" })
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		PrintWriter out = resp.getWriter();
		String studyName = "base";
		String hydro_echo = "'", gis = "";
		ServletFileUpload upload = new ServletFileUpload();
		upload.setSizeMax(MAX_SIZE_BYTES);
		upload.setHeaderEncoding("UTF-8");
		PersistenceManager persistenceManager = PMF.get()
				.getPersistenceManager();
		try {
			// Parse the request
			FileItemIterator iterator = upload.getItemIterator(req);

			while (iterator.hasNext()) {
				FileItemStream item = iterator.next();
				InputStream inputStream = item.openStream();
				if (item.isFormField()) {
					if (item.getFieldName().equals("studyName")) {
						studyName = Streams.asString(inputStream);
					}
				} else {
					if (item.getFieldName().equals("hydro_echo_inp")) {
						hydro_echo = Streams.asString(inputStream);
					} else if (item.getFieldName().equals("gis_inp")) {
						gis = Streams.asString(inputStream);
					}
				}
			}
			DSM2ModelFileDAOImpl dao = new DSM2ModelFileDAOImpl(
					persistenceManager);
			String email = Utils.getCurrentUserEmail();
			List<DSM2ModelFile> filesForStudy = dao.getFilesForStudy(studyName,
					email);
			DSM2ModelFile hydro_echo_file = null;
			DSM2ModelFile gis_info_file = null;

			if (filesForStudy.size() == 0) {
				hydro_echo_file = new DSM2ModelFile();
				gis_info_file = new DSM2ModelFile();
			} else {
				for (DSM2ModelFile file : filesForStudy) {
					if (file.getName().equals("hydro_echo_inp")) {
						hydro_echo_file = file;
					}
					if (file.getName().equals("gis_inp")) {
						gis_info_file = file;
					}
				}
			}
			hydro_echo_file.setName("hydro_echo_inp");
			gis_info_file.setName("gis_inp");
			hydro_echo_file.setStudyName(studyName);
			gis_info_file.setStudyName(studyName);
			hydro_echo_file.setOwner(Utils.getCurrentUserEmail());
			gis_info_file.setOwner(Utils.getCurrentUserEmail());
			hydro_echo_file.setContents(hydro_echo);
			gis_info_file.setContents(gis);
			if (filesForStudy.size() == 0) {
				dao.createObject(hydro_echo_file);
				dao.createObject(gis_info_file);
			} else {
				dao.updateObject(hydro_echo_file);
				dao.updateObject(gis_info_file);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			persistenceManager.close();
		}
		// Return result
		String resultUrl = "/dsm2_grid_map.html";
		resp.sendRedirect(resultUrl + "#study=" + URLEncoder.encode(studyName));
		out.close();
	}
}
