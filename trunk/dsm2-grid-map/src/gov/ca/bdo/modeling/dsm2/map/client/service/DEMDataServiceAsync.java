package gov.ca.bdo.modeling.dsm2.map.client.service;

import gov.ca.bdo.modeling.dsm2.map.client.model.DEMGridSquare;
import gov.ca.bdo.modeling.dsm2.map.client.model.DataPoint;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DEMDataServiceAsync {

	void getElevationAlong(double x1, double y1, double x2, double y2,
			AsyncCallback<List<DataPoint>> callback);

	void getElevationAt(double latitude, double longitude,
			AsyncCallback<Double> callback);

	void getGridAt(double latitude, double longitude,
			AsyncCallback<DEMGridSquare> callback);

	void getGridWithin(double x1, double y1, double x2, double y2,
			AsyncCallback<List<DEMGridSquare>> callback);

}
