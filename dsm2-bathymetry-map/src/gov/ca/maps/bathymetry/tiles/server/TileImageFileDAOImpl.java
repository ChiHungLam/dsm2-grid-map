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
				//Logger.getLogger("dao").warning("No files found for " + name);
				return null;
			}
		} catch (Exception e) {
			//Logger.getLogger("dao").warning(
			//		"Got error " + e.getMessage() + " finding " + name);
			return null;
		}
	}

}
