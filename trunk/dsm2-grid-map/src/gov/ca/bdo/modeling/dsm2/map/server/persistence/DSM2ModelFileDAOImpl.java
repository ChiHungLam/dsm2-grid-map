package gov.ca.bdo.modeling.dsm2.map.server.persistence;

import gov.ca.bdo.modeling.dsm2.map.server.data.DSM2ModelFile;
import gov.ca.bdo.modeling.dsm2.map.server.utils.GenericDAOImpl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class DSM2ModelFileDAOImpl extends GenericDAOImpl<DSM2ModelFile>
		implements DSM2ModelFileDAO {
	public DSM2ModelFileDAOImpl(PersistenceManager pm) {
		super(pm);
	}

	@SuppressWarnings("unchecked")
	public List<DSM2ModelFile> getFilesForCurrentUser() throws Exception {
		UserService userService = UserServiceFactory.getUserService();
		User currentUser = userService.getCurrentUser();
		String email = currentUser.getEmail();
		try {
			// look for item first else insert a new one
			Query query = super.getPersistenceManager().newQuery(
					"select from " + DSM2ModelFile.class.getName());
			query.setFilter("owner==emailParam");
			query.declareParameters("String emailParam");
			List<DSM2ModelFile> files = (List<DSM2ModelFile>) query
					.execute(email);
			return files;
		} catch (Exception e) {
			throw e;
		}
	}

	public Collection<String> getStudyNamesForCurrentUser() throws Exception {
		List<DSM2ModelFile> list = getFilesForCurrentUser();
		HashMap<String, String> studyNames = new HashMap<String, String>();
		for (DSM2ModelFile file : list) {
			studyNames.put(file.getStudyName(), file.getStudyName());
		}
		return studyNames.values();
	}

	@SuppressWarnings("unchecked")
	public List<DSM2ModelFile> getFilesForStudy(String studyName)
			throws Exception {
		UserService userService = UserServiceFactory.getUserService();
		User currentUser = userService.getCurrentUser();
		String email = currentUser.getEmail();
		try {
			// look for item first else insert a new one
			Query query = getPersistenceManager().newQuery(
					"select from " + DSM2ModelFile.class.getName());
			query.setFilter("owner==emailParam && studyName==studyNameParam");
			query.declareParameters("String emailParam, String studyNameParam");
			List<DSM2ModelFile> files = (List<DSM2ModelFile>) query.execute(
					email, studyName);
			return files;
		} catch (Exception e) {
			throw e;
		}
	}

	public String getContents(DSM2ModelFile file) throws Exception {
		return file.getContents().getValue();
	}

}
