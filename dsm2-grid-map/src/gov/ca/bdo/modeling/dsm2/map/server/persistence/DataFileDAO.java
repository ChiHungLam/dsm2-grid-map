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

}
