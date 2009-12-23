package gov.ca.maps.bathymetry.tiles.server;

import java.io.Serializable;

public interface GenericDAO<T> {

	public T createObject(T object);

	public void deleteObject(T object);

	public T findObjectById(Serializable id);

	public void updateObject(T object);

	public void flush();

}
