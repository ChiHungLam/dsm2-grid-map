package gov.ca.maps.bathymetry.tiles.client;

import gov.ca.maps.bathymetry.tiles.client.model.BathymetryDataPoint;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.SerializationException;

@RemoteServiceRelativePath("bathymetry")
public interface BathymetryDataService extends RemoteService {
	public List<BathymetryDataPoint> getBathymetryDataPoints(double latitude,
			double longitude) throws SerializationException;

	/**
	 * Retrieve bathymetry data points along the line between (x1,y1) and
	 * (x2,y2) where x variables are latitude and y variables are longitude
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 * @throws Exception
	 */
	public List<BathymetryDataPoint> getBathymetryDataPoints(double x1,
			double y1, double x2, double y2) throws SerializationException;
}
