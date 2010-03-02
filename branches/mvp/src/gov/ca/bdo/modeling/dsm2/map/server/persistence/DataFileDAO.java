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

import gov.ca.bdo.modeling.dsm2.map.server.data.DataFile;
import gov.ca.bdo.modeling.dsm2.map.server.utils.GenericDAO;

import java.util.List;

public interface DataFileDAO extends GenericDAO<DataFile> {
	/**
	 * Get all files for the study named
	 * 
	 * @param studyName
	 * @return
	 * @throws Exception
	 */
	public List<DataFile> getFilesForStudy(String studyName) throws Exception;

	/**
	 * Gets the file for the study name and named data set
	 * 
	 * @param studyName
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public List<DataFile> getFilesForStudyAndName(String studyName, String name)
			throws Exception;

	/**
	 * Get the file for study name, name and type of data set
	 * 
	 * @param studyName
	 * @param name
	 * @param type
	 */
	public DataFile getFileForStudyAndName(String studyName, String name,
			String type);

}
