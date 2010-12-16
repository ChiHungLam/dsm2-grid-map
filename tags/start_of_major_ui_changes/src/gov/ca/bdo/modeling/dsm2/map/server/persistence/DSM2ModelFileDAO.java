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
package gov.ca.bdo.modeling.dsm2.map.server.persistence;

import gov.ca.bdo.modeling.dsm2.map.server.data.DSM2ModelFile;
import gov.ca.bdo.modeling.dsm2.map.server.utils.GenericDAO;

import java.util.Collection;
import java.util.List;

public interface DSM2ModelFileDAO extends GenericDAO<DSM2ModelFile> {
	/**
	 * get all the files for the current user
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<DSM2ModelFile> getFilesForCurrentUser(String email)
			throws Exception;

	/**
	 * Get all study names for the current user
	 * 
	 * @return
	 * @throws Exception
	 */
	public Collection<String> getStudyNamesForCurrentUser(String email)
			throws Exception;

	/**
	 * Get all files for the study named
	 * 
	 * @param studyName
	 * @return
	 * @throws Exception
	 */
	public List<DSM2ModelFile> getFilesForStudy(String studyName, String email)
			throws Exception;

}
