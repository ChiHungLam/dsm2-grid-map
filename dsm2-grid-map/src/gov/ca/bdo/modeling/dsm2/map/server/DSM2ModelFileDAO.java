package gov.ca.bdo.modeling.dsm2.map.server;

import java.util.Collection;
import java.util.List;

public interface DSM2ModelFileDAO extends GenericDAO<DSM2ModelFile> {
	/**
	 * get all the files for the current user
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<DSM2ModelFile> getFilesForCurrentUser() throws Exception;

	/**
	 * Get all study names for the current user
	 * 
	 * @return
	 * @throws Exception
	 */
	public Collection<String> getStudyNamesForCurrentUser() throws Exception;

	/**
	 * Get all files for the study named
	 * 
	 * @param studyName
	 * @return
	 * @throws Exception
	 */
	public List<DSM2ModelFile> getFilesForStudy(String studyName)
			throws Exception;

}
