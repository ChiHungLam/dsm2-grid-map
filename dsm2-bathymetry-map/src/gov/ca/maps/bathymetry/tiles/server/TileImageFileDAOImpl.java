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

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

public class TileImageFileDAOImpl extends GenericDAOImpl<TileImageFile>
		implements TileImageFileDAO {
	public TileImageFileDAOImpl(PersistenceManager pm) {
		super(pm);
	}

	@SuppressWarnings("unchecked")
	public TileImageFile getFileNamed(String name) {
		try {
			// look for item first else insert a new one
			Query query = getPersistenceManager().newQuery(
					"select from " + TileImageFile.class.getName());
			query.setFilter("name==nameParam");
			query.declareParameters("String nameParam");
			List<TileImageFile> files = (List<TileImageFile>) query
					.execute(name);
			if (files.size() == 1) {
				return files.get(0);
			} else {
				// Logger.getLogger("dao").warning("No files found for " +
				// name);
				return null;
			}
		} catch (Exception e) {
			// Logger.getLogger("dao").warning(
			// "Got error " + e.getMessage() + " finding " + name);
			return null;
		}
	}

}
