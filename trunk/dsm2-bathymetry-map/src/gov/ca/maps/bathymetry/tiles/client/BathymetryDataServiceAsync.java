package gov.ca.maps.bathymetry.tiles.client;

import gov.ca.maps.bathymetry.tiles.client.model.BathymetryDataPoint;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface BathymetryDataServiceAsync {

	void getBathymetryDataPoints(double latitude, double longitude,
			AsyncCallback<List<BathymetryDataPoint>> callback);

	void getBathymetryDataPoints(double x1, double y1, double x2, double y2,
			AsyncCallback<List<BathymetryDataPoint>> callback);

	void getAverageDepthInPolygon(List<double[]> points,
			AsyncCallback<Double> callback);
}
