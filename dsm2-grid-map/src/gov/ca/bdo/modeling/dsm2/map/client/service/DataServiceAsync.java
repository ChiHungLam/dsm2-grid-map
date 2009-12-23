package gov.ca.bdo.modeling.dsm2.map.client.service;

import gov.ca.bdo.modeling.dsm2.map.client.model.RegularTimeSeries;
import gov.ca.bdo.modeling.dsm2.map.client.model.TextAnnotation;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DataServiceAsync {

	void getRegularTimeSeries(String studyName, String name,
			String[] variables, AsyncCallback<List<RegularTimeSeries>> callback);

	void getNotes(String studyName, AsyncCallback<List<TextAnnotation>> callback);

	void saveNotes(String studyName, List<TextAnnotation> annotations,
			AsyncCallback<Void> callback);

}
