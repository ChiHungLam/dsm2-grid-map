package gov.ca.modeling.maps.elevation.client.service;

import gov.ca.modeling.maps.elevation.client.model.CalculationState;
import gov.ca.modeling.maps.elevation.client.model.DEMGridSquare;
import gov.ca.modeling.maps.elevation.client.model.DataPoint;

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

	void getBilinearInterpolatedElevationAt(double latitude, double longitude,
			AsyncCallback<Double> callback);

	void getBilinearInterpolatedElevationAlong(double x1, double y1, double x2,
			double y2, AsyncCallback<List<DataPoint>> callback);

	void startCalculationOfAverageElevationInArea(List<DataPoint> points,
			AsyncCallback<CalculationState> callback);

	void startCalculationOfVolumeInAreaForElevation(List<DataPoint> points,
			double elevation, AsyncCallback<Double> callback);

	void checkStatus(CalculationState state,
			AsyncCallback<CalculationState> callback);

}
