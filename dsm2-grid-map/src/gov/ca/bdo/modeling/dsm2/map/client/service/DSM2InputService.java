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
package gov.ca.bdo.modeling.dsm2.map.client.service;

import gov.ca.bdo.modeling.dsm2.map.client.model.StudyInfo;
import gov.ca.dsm2.input.model.DSM2Model;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("dsm2input")
public interface DSM2InputService extends RemoteService {
	public DSM2Model getInputModel(String studyPath);

	/**
	 * A way to support unlisted studies with a unique key that is impossible to
	 * guess
	 * 
	 * @param key
	 * @return
	 */
	public DSM2Model getInputModelForKey(String key);

	public String[] getStudyNames();

	public List<StudyInfo> getStudies();

	public DSM2Model getInputModelForStudy(StudyInfo info);

	public void saveModel(String studyName, DSM2Model model);

	public String showHydroInput(String studyName);

	public String showGISInput(String studyName);

	public void removeStudy(String studyName);

	/**
	 * Generates a unique key for sharing this study.
	 * 
	 * @param studyName
	 *            to be shared
	 * @return a key that is used in building an unlisted url
	 */
	public String generateSharingKey(String studyName);

	/**
	 * Returns the name of the study related to the sharing key
	 * 
	 * @param key
	 * @return
	 */
	public String getStudyNameForSharingKey(String key);

	public String showInputForKey(String studyKey, String inputName);

}
