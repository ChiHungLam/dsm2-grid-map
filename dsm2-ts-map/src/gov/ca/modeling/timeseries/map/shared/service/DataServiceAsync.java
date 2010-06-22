package gov.ca.modeling.timeseries.map.shared.service;

import gov.ca.modeling.timeseries.map.shared.data.MapTextAnnotation;
import gov.ca.modeling.timeseries.map.shared.data.TimeSeriesData;
import gov.ca.modeling.timeseries.map.shared.data.TimeSeriesReferenceData;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.objectify.Key;

public interface DataServiceAsync {

	void addMapTextAnnotation(MapTextAnnotation annotation,
			AsyncCallback<MapTextAnnotation> callback);

	void getDataForReference(TimeSeriesReferenceData reference,
			AsyncCallback<TimeSeriesData> callback);

	void getDataForReferences(List<Key<TimeSeriesReferenceData>> references,
			AsyncCallback<List<TimeSeriesData>> callback);

	void getMapTextAnnotation(Key<MapTextAnnotation> key,
			AsyncCallback<MapTextAnnotation> callback);

	void getMapTextAnnotations(List<Key<MapTextAnnotation>> keyList,
			AsyncCallback<List<MapTextAnnotation>> callback);

	void getReferencesForSourceLocationType(String source, String location,
			String type, AsyncCallback<List<TimeSeriesReferenceData>> callback);

	void removeMapTextAnnotation(Key<MapTextAnnotation> key,
			AsyncCallback<Void> callback);

}
