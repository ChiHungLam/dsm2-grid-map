package gov.ca.bdo.modeling.dsm2.map.server.utils;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

import javax.jdo.PersistenceManager;

public class GenericDAOImpl<T> implements GenericDAO<T> {
	private final Class<T> persistentClass;
	private final PersistenceManager persistenceManager;

	@SuppressWarnings("unchecked")
	public GenericDAOImpl(PersistenceManager pm) {
		persistenceManager = pm;
		persistentClass = (Class<T>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
	}

	protected PersistenceManager getPersistenceManager() {
		return persistenceManager;
	}

	public T createObject(T object) {
		PersistenceManager persistenceManager = getPersistenceManager();
		persistenceManager.makePersistent(object);
		return object;
	}

	public void deleteObject(T object) {
		PersistenceManager persistenceManager = getPersistenceManager();
		persistenceManager.deletePersistent(object);
	}

	public T findObjectById(Serializable id) {
		PersistenceManager persistenceManager = getPersistenceManager();
		T result = persistenceManager.getObjectById(persistentClass, id);
		return result;
	}

	public void flush() {
		PersistenceManager persistenceManager = getPersistenceManager();
		persistenceManager.flush();
	}

	public void updateObject(T object) {
		PersistenceManager persistenceManager = getPersistenceManager();
		persistenceManager.refresh(object);
	}
}
