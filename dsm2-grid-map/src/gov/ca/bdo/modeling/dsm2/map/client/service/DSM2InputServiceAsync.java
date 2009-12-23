package gov.ca.bdo.modeling.dsm2.map.client.service;

import gov.ca.dsm2.input.model.DSM2Model;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DSM2InputServiceAsync {

	void getInputModel(String studyPath, AsyncCallback<DSM2Model> callback);

	void getStudyNames(AsyncCallback<String[]> callback);

	void saveModel(String studyName, DSM2Model model,
			AsyncCallback<Void> callback);

	void showHydroInput(String studyName, AsyncCallback<String> callback);

	void showGISInput(String studyName, AsyncCallback<String> callback);

	void removeStudy(String studyName, AsyncCallback<Void> callback);
}
