package gov.ca.bdo.modeling.dsm2.map.client.service;

import gov.ca.bdo.modeling.dsm2.map.client.model.RegularTimeSeries;
import gov.ca.bdo.modeling.dsm2.map.client.model.TextAnnotation;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("data")
public interface DataService extends RemoteService {
	public List<RegularTimeSeries> getRegularTimeSeries(String studyName,
			String name, String[] variables);

	public List<TextAnnotation> getNotes(String studyName);

	public void saveNotes(String studyName, List<TextAnnotation> annotations);
}
