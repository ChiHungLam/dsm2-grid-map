package gov.ca.bdo.modeling.dsm2.map.client.map;

import gov.ca.dsm2.input.model.Channel;
import gov.ca.dsm2.input.model.Node;

import java.util.List;

import com.google.gwt.maps.client.geom.LatLng;

public class ModelUtils {
	public static LatLng[] getPointsForChannel(Channel channel, Node upNode, Node downNode){
		LatLng[] points = null;
		LatLng upPoint = LatLng.newInstance(upNode.getLatitude(), upNode
				.getLongitude());
		LatLng downPoint = LatLng.newInstance(downNode.getLatitude(), downNode
				.getLongitude());
		List<double[]> latLngPoints = channel.getLatLngPoints();
		if ((latLngPoints != null) && (latLngPoints.size() > 0)) {
			int size = latLngPoints.size();
			points = new LatLng[size + 2];
			for (int i = 1; i < points.length - 1; i++) {
				double[] ds = latLngPoints.get(i - 1);
				points[i] = LatLng.newInstance(ds[0], ds[1]);
			}
		} else {
			points = new LatLng[2];
		}
		points[0] = upPoint;
		points[points.length - 1] = downPoint;
		return points;
	}
}
