package gov.ca.bdo.modeling.dsm2.map.server;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class DataFileDAOImpl extends GenericDAOImpl<DataFile> implements
		DataFileDAO {
	public DataFileDAOImpl(PersistenceManager pm) {
		super(pm);
	}

	@SuppressWarnings("unchecked")
	public List<DataFile> getFilesForStudy(String studyName) throws Exception {
		UserService userService = UserServiceFactory.getUserService();
		User currentUser = userService.getCurrentUser();
		String email = currentUser.getEmail();
		try {
			// look for item first else insert a new one
			Query query = getPersistenceManager().newQuery(
					"select from " + DataFile.class.getName());
			query
					.setFilter("ownerName==emailParam && studyName==studyNameParam");
			query.declareParameters("String emailParam, String studyNameParam");
			List<DataFile> files = (List<DataFile>) query.execute(email,
					studyName);
			return files;
		} catch (Exception e) {
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public List<DataFile> getFilesForStudyAndName(String studyName, String name)
			throws Exception {
		UserService userService = UserServiceFactory.getUserService();
		User currentUser = userService.getCurrentUser();
		String email = currentUser.getEmail();
		try {
			// look for item first else insert a new one
			Query query = getPersistenceManager().newQuery(
					"select from " + DataFile.class.getName());
			query
					.setFilter("ownerName==emailParam && studyName==studyNameParam && name==nameParam");
			query
					.declareParameters("String emailParam, String studyNameParam, String nameParam");
			List<DataFile> file = (List<DataFile>) query.execute(email,
					studyName, name);
			return file;
		} catch (Exception e) {
			throw e;
		}
	}

	public String getContents(DataFile file) throws Exception {
		return file.getContents().getValue();
	}

}