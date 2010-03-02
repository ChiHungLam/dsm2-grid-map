package gov.ca.bdo.modeling.dsm2.map.server.persistence;

import gov.ca.bdo.modeling.dsm2.map.server.data.DSM2Study;
import gov.ca.bdo.modeling.dsm2.map.server.utils.GenericDAOImpl;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

public class DSM2StudyDAOImpl extends GenericDAOImpl<DSM2Study> implements
		DSM2StudyDAO {

	public DSM2StudyDAOImpl(PersistenceManager pm) {
		super(pm);
	}

	public DSM2Study getStudyForSharingKey(String key) throws Exception {
		// look for item first else insert a new one
		Query query = getPersistenceManager().newQuery(
				"select from " + DSM2Study.class.getName());
		query.setFilter("sharingKey==sharingKeyParam");
		query.declareParameters("String sharingKeyParam");
		List<DSM2Study> files = (List<DSM2Study>) query.execute(key);
		if ((files == null) || (files.size() != 1)) {
			return null;
		} else {
			return files.get(0);
		}
	}

	public DSM2Study getStudyForName(String studyName) throws Exception {
		// look for item first else insert a new one
		Query query = getPersistenceManager().newQuery(
				"select from " + DSM2Study.class.getName());
		query.setFilter("studyName==studyNameParam");
		query.declareParameters("String studyNameParam");
		List<DSM2Study> files = (List<DSM2Study>) query.execute(studyName);
		if ((files == null) || (files.size() != 1)) {
			return null;
		} else {
			return files.get(0);
		}
	}

}
