package gov.ca.bdo.modeling.dsm2.map.server.utils;

import java.io.Serializable;

public interface GenericDAO<T> {

	public T createObject(T object);

	public void deleteObject(T object);

	public T findObjectById(Serializable id);

	public void updateObject(T object);

	public void flush();

}
