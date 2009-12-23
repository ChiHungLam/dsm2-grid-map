package gov.ca.bdo.modeling.dsm2.map.client.service;

import gov.ca.dsm2.input.model.DSM2Model;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("dsm2input")
public interface DSM2InputService extends RemoteService {
	public DSM2Model getInputModel(String studyPath);

	public String[] getStudyNames();

	public void saveModel(String studyName, DSM2Model model);

	public String showHydroInput(String studyName);

	public String showGISInput(String studyName);

	public void removeStudy(String studyName);
}
