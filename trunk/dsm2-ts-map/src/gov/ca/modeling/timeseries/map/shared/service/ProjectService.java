package gov.ca.modeling.timeseries.map.shared.service;

import gov.ca.modeling.timeseries.map.shared.data.ProjectData;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("service/project")
public interface ProjectService extends RemoteService {
	/**
	 * Get list of projects for current user
	 * 
	 * @return
	 */
	public List<ProjectData> getAllProjectsForCurrentUser()
			throws ApplicationException;

	/**
	 * Get list of all projects available publicly (no sign-in required)
	 * 
	 * @return
	 */
	public List<ProjectData> getAllPublicProjects();

	/**
	 * A project name is unique to each user. Public projects are always
	 * referred to by their unique string names ( generated from a hash )
	 * 
	 * @param name
	 * @param email
	 * @return
	 */
	public ProjectData getProjectOwnedByUser(String name, String ownerEmail)
			throws ApplicationException;

	/**
	 * Create a project named
	 * 
	 * @param name
	 * @return
	 */
	public ProjectData createProjectData(String name)
			throws ApplicationException;

	/**
	 * Saves project data
	 */
	public void saveProjectData(ProjectData data) throws ApplicationException;

	/**
	 * Deletes the project named
	 * 
	 * @param name
	 */
	public void deleteProjectData(String name) throws ApplicationException;

}
