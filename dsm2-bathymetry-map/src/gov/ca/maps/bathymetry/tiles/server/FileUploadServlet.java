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
package gov.ca.maps.bathymetry.tiles.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collections;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

import com.google.appengine.api.datastore.Blob;

public class FileUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 8367618333138027430L;
	private static final int MAX_SIZE_BYTES = 1024 * 1024;
	private Cache cache;

	@Override
	public void init() throws ServletException {
		try {
			cache = CacheManager.getInstance().getCacheFactory().createCache(
					Collections.emptyMap());
		} catch (CacheException e) {
			cache = null;
			e.printStackTrace();
		}
		super.init();
	}

	@SuppressWarnings("unchecked")
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String clearCache = req.getParameter("clear");
		if ((clearCache != null) && (clearCache.length() > 0)) {
			cache.clear();
			resp.setContentType("text/plain");
			resp.getWriter().println("Cleared cache");
			return;
		}
		String pathInfo = req.getPathInfo();
		if (pathInfo.lastIndexOf("/") >= 0) {
			pathInfo = pathInfo.substring(pathInfo.lastIndexOf("/") + 1);
		}
		String name = pathInfo.substring(pathInfo.indexOf("_") + 1);

		Object data = cache.get(name);
		// Object data = null;
		if ((data == null) || !(data instanceof byte[])) {
			PersistenceManager persistenceManager = PMF.get()
					.getPersistenceManager();
			TileImageFileDAOImpl dao = new TileImageFileDAOImpl(
					persistenceManager);
			TileImageFile file = dao.getFileNamed(name);
			if (file != null) {
				data = file.getContents().getBytes();
				cache.put(name, data);
			} else {
				data = "/transparent.png";
				cache.put(name, data);
			}
		}
		if (data instanceof byte[]) {
			byte[] dataAsBytes = (byte[]) data;
			resp.setContentType("image/png");
			resp.setContentLength(dataAsBytes.length);
			resp.setHeader("Cache-Control", "public, max-age=222222222");
			resp.getOutputStream().write(dataAsBytes);
		} else {
			resp.sendRedirect(resp.encodeRedirectURL(data.toString()));
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		PrintWriter out = resp.getWriter();
		String name = "blank.png";
		ServletFileUpload upload = new ServletFileUpload();
		upload.setSizeMax(MAX_SIZE_BYTES);
		PersistenceManager persistenceManager = PMF.get()
				.getPersistenceManager();
		try {
			// Parse the request
			ByteArrayOutputStream baos = new ByteArrayOutputStream(5000);
			FileItemIterator iterator = upload.getItemIterator(req);

			while (iterator.hasNext()) {
				FileItemStream item = iterator.next();
				InputStream inputStream = item.openStream();
				if (item.isFormField()) {
					if (item.getFieldName().equals("name")) {
						name = Streams.asString(inputStream);
					}
				} else if (item.getFieldName().equals("file")) {
					Streams.copy(inputStream, baos, true);
				}
			}
			TileImageFileDAOImpl dao = new TileImageFileDAOImpl(
					persistenceManager);
			TileImageFile file = dao.getFileNamed(name);
			if (file != null) {
				dao.deleteObject(file);
			}
			file = new TileImageFile();
			file.setName(name);
			file.setContents(new Blob(baos.toByteArray()));
			dao.createObject(file);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			persistenceManager.close();
		}
		// Return result
		String resultUrl = "/upload.html";
		resp.sendRedirect(resultUrl);
		out.close();
	}
}
