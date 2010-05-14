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
package gov.ca.modeling.server.utils;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

public class GenericDAOImpl<T> implements GenericDAO<T> {

	public GenericDAOImpl() {
	}

	public T createObject(T object) {
		Objectify ofy = ObjectifyService.begin();
		ofy.put(object);
		return object;
	}

	public void deleteObject(T object) {
		Objectify ofy = ObjectifyService.begin();
		ofy.delete(object);
	}

	public T findObjectById(Class<T> actualClass, long id) {
		Objectify ofy = ObjectifyService.begin();
		T result = ofy.find(actualClass, id);
		return result;
	}

}
