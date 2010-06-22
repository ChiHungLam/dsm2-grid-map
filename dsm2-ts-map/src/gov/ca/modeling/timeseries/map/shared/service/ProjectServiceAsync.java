package gov.ca.modeling.timeseries.map.shared.service;

import gov.ca.modeling.timeseries.map.shared.data.ProjectData;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ProjectServiceAsync {

	void createProjectData(String name, AsyncCallback<ProjectData> callback);

	void deleteProjectData(String name, AsyncCallback<Void> callback);

	void getAllProjectsForCurrentUser(AsyncCallback<List<ProjectData>> callback);

	void getAllPublicProjects(AsyncCallback<List<ProjectData>> callback);

	void getProjectOwnedByUser(String name, String ownerEmail,
			AsyncCallback<ProjectData> callback);

	void saveProjectData(ProjectData data, AsyncCallback<Void> callback);

}
