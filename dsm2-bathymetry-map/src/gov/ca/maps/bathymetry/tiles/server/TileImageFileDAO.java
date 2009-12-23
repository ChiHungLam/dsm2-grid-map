package gov.ca.maps.bathymetry.tiles.server;


public interface TileImageFileDAO extends GenericDAO<TileImageFile> {
	public TileImageFile getFileNamed(String name) throws Exception;
}
