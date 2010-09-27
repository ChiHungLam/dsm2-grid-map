/**
 *    Copyright (C) 2009, 2010 
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
 *    GNU General Public License for more details. [http://www.gnu.org/licenses]
 *    
 *    @author Nicky Sandhu
 *    
 */
package gov.ca.bdo.modeling.dsm2.map.server;

import gov.ca.bdo.modeling.dsm2.map.server.data.DEMDataFile;
import gov.ca.bdo.modeling.dsm2.map.server.persistence.DEMDataFileDAO;
import gov.ca.bdo.modeling.dsm2.map.server.persistence.DEMDataFileDAOImpl;
import gov.ca.bdo.modeling.dsm2.map.server.utils.PMF;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

@SuppressWarnings("serial")
public class DEMDataFileUploadServlet extends HttpServlet {
	private static final int MAX_SIZE_BYTES = 1024 * 1024 * 10;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String xStr = req.getParameter("x");
		String yStr = req.getParameter("y");
		if (xStr == null || yStr == null) {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND,
					"No DEM Data Available for x=" + xStr + "&y=" + yStr);
			return;
		}
		double x = Double.parseDouble(xStr);
		double y = Double.parseDouble(yStr);
		PersistenceManager persistenceManager = PMF.get()
				.getPersistenceManager();
		try {
			DEMDataFileDAO dao = new DEMDataFileDAOImpl(persistenceManager);
			DEMDataFile DEMDataFile = dao.getFileForLocation(x, y);
			if (DEMDataFile != null) {
				resp.setContentType("text/csv");
				resp.setHeader("Content-Disposition",
						"attachment; filename=demdata_" + DEMDataFile.getX()
								+ "_" + DEMDataFile.getY() + ".csv");
				String contents = convertToString(DEMDataFile);
				resp.setContentLength(contents.length() * 2); // number of bytes
				// sent
				resp.getWriter().write(contents);
			} else {
				resp.sendError(HttpServletResponse.SC_NOT_FOUND,
						"No DEM Data Available for x=" + x + "&y=" + y);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			persistenceManager.close();
		}
	}

	private String convertToString(DEMDataFile dataFile) throws IOException {
		StringWriter strWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(strWriter);
		int[] blobData = dataFile.getBlobData();
		for (int i = 0; i < blobData.length; i++) {
			writer.print(blobData[i]);
			if (i % 10 == 9) {
				writer.println();
			}
		}
		return strWriter.toString();
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		PrintWriter out = resp.getWriter();
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
					}
				} else {
					if (item.getFieldName().equals("demFile")) {
						String name = item.getName();
						int x = extractXFromName(name);
						int y = extractYFromName(name);
						String parameter = req.getParameter("append");
						boolean append = false;
						if (parameter != null
								&& !parameter.equalsIgnoreCase("n")) {
							append = true;
						}
						saveData(persistenceManager, inputStream, append, x, y);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			persistenceManager.close();
		}
		// Return result
		String resultUrl = "upload_dem.html";
		resp.sendRedirect(resultUrl);
		out.close();
	}

	private int extractYFromName(String name) {
		int lastIndexOf = name.lastIndexOf("_");
		String str = name.substring(lastIndexOf + 1, name.lastIndexOf(".csv"));
		return Integer.parseInt(str);
	}

	private int extractXFromName(String name) {
		int lastIndexOf = name.lastIndexOf("_");
		String str = name.substring(0, lastIndexOf);
		str = str.substring(str.lastIndexOf("s") + 1);
		return Integer.parseInt(str);
	}

	public void saveData(PersistenceManager persistenceManager,
			InputStream fileAsStream, boolean append, int x, int y)
			throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				fileAsStream));
		String line = null;
		int[] values = new int[100];
		int i = 0;
		while ((line = reader.readLine()) != null) {
			String[] fields = line.split("\\s");
			for (int j = 0; j < fields.length; j++) {
				values[i] = Integer.parseInt(fields[j]);
				i++;
			}
		}
		DEMDataFileDAO dao = new DEMDataFileDAOImpl(persistenceManager);
		DEMDataFile dataFile = dao.getFileForLocation(x, y);
		if (dataFile == null) {
			dataFile = new DEMDataFile();
			dataFile.setX(x);
			dataFile.setY(y);
			dao.createObject(dataFile);
		}
		dataFile.setBlobData(values);
	}
}
