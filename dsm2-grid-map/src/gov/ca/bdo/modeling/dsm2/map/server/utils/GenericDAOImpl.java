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
