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

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;

public class FileUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 8367618333138027430L;
	private Cache cache;
	private DatastoreService datastore;
	private BlobstoreService blobstoreService;
	private ImagesService imagesService;

	@Override
	public void init() throws ServletException {
		datastore = DatastoreServiceFactory.getDatastoreService();
		blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		imagesService = ImagesServiceFactory.getImagesService();
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
		if (data == null) {
			Entity imageFileEntity = null;
			Query query = new Query("TileImageFile");
			query.addFilter("name", Query.FilterOperator.EQUAL, name);
			PreparedQuery preparedQuery = datastore.prepare(query);
			imageFileEntity = preparedQuery.asSingleEntity();
			if (imageFileEntity != null) {
				BlobKey key = (BlobKey) imageFileEntity.getProperty("blobKey");
				data = key;
				// String servingUrl = imagesService.getServingUrl(key);
				// data = servingUrl;
			} else {
				data = "/transparent.png";
			}
			try {
				cache.put(name, data);
			} catch (Exception ex) {
				// GCache exception due to policy on put
			}
		}
		if (data instanceof BlobKey) {
			blobstoreService.serve((BlobKey) data, resp);
		} else {
			resp.sendRedirect(resp.encodeRedirectURL(data.toString()));
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		PersistenceManager persistenceManager = PMF.get()
				.getPersistenceManager();
		String name = req.getParameter("name");
		try {
			TileImageFileDAOImpl dao = new TileImageFileDAOImpl(
					persistenceManager);
			TileImageFile file = dao.getFileNamed(name);
			if (file != null) {
				blobstoreService.delete(file.getBlobKey());
				dao.deleteObject(file);
			}
			file = new TileImageFile();
			file.setName(name);
			Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
			BlobKey blobKey = blobs.get("file");
			file.setBlobKey(blobKey);
			dao.createObject(file);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			persistenceManager.close();
		}
		resp.sendRedirect("/upload.jsp");
	}
}
