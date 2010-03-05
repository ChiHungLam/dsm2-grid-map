package gov.ca.bdo.modeling.dsm2.map.server.persistence;

import gov.ca.bdo.modeling.dsm2.map.server.data.DSM2Study;
import gov.ca.bdo.modeling.dsm2.map.server.utils.GenericDAO;

public interface DSM2StudyDAO extends GenericDAO<DSM2Study> {

	public DSM2Study getStudyForSharingKey(String key) throws Exception;

	public DSM2Study getStudyForName(String studyName, String email)
			throws Exception;
}
