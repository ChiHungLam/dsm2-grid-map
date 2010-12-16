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

import gov.ca.bdo.modeling.dsm2.map.client.model.RegularTimeSeries;
import gov.ca.bdo.modeling.dsm2.map.server.data.DataFile;
import gov.ca.bdo.modeling.dsm2.map.server.persistence.DataFileDAO;
import gov.ca.bdo.modeling.dsm2.map.server.persistence.DataFileDAOImpl;
import gov.ca.bdo.modeling.dsm2.map.server.utils.PMF;
import gov.ca.bdo.modeling.dsm2.map.server.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

import com.google.appengine.api.datastore.Text;

public class DataFileUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 8367618333138027430L;
	private static final int MAX_SIZE_BYTES = 1024 * 1024 * 10;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String study = req.getParameter("study");
		String name = req.getParameter("name");
		String type = req.getParameter("type");
		resp.getWriter().println(
				"Got request for study: " + study + " name: " + name
						+ " type: " + type);
	}

	@SuppressWarnings( { "deprecation" })
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		PrintWriter out = resp.getWriter();
		String studyName = null;
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
					if (item.getFieldName().equals("dataFile")) {
						if ((studyName == null) || studyName.equals("")) {
							return;
						}
						saveData(persistenceManager, studyName, inputStream);
					}
				}
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

	public void saveData(PersistenceManager persistenceManager,
			String studyName, InputStream fileAsStream) throws Exception {
		DataFileDAO dao = new DataFileDAOImpl(persistenceManager);
		DSSOutParser parser = new DSSOutParser(fileAsStream);
		RegularTimeSeries rts = null;
		String email = Utils.getCurrentUserEmail();
		while ((rts = parser.nextSeries()) != null) {
			DataFile file = dao.getFileForStudyAndName(studyName,
					rts.getName(), rts.getType(), email);
			if (file == null) {
				file = new DataFile();
				file.setStudyName(studyName);
				file.setOwnerName(Utils.getCurrentUserEmail());
				file.setName(rts.getName().toLowerCase());
				file.setType(rts.getType().toLowerCase());
				dao.createObject(file);
			}
			StringBuilder contents = new StringBuilder();
			double[] data = rts.getData();
			contents.append("startTime=" + rts.getStartTime().getTime() + "\n");
			contents.append("count=" + data.length + "\n");
			for (double element : data) {
				contents.append(element).append("\n");
			}
			file.setContents(new Text(contents.toString()));
			dao.updateObject(file);
		}
	}
}
