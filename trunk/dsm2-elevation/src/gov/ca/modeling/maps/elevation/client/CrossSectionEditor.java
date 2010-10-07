package gov.ca.modeling.maps.elevation.client;

import gov.ca.modeling.maps.elevation.client.model.DataPoint;
import gov.ca.modeling.maps.elevation.client.model.XSectionProfile;
import gov.ca.modeling.maps.elevation.client.model.XYZPoint;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.Composite;

public class CrossSectionEditor extends Composite {
	private XYZPoint[] xsectionPoints;

	public CrossSectionEditor(String divId, XSectionProfile xsProfile,
			List<DataPoint> profile, List<DataPoint> bathymetry) {
		xsectionPoints = convertDataToXYZPoints(xsProfile.points);
		XYZPoint[] profilePoints = convertDataToXYZPoints(profile);
		XYZPoint[] points = convertDataToXYZPoints(bathymetry);
		plot(divId, ArrayUtils.toJsArray(xsectionPoints), ArrayUtils
				.toJsArray(profilePoints), ArrayUtils.toJsArray(points));
	}

	private XYZPoint[] convertDataToXYZPoints(List<DataPoint> points) {
		XYZPoint[] xyzs = new XYZPoint[points.size()];
		int i = 0;
		for (DataPoint point : points) {
			XYZPoint xyz = XYZPoint.create(point.x, point.z, point.y);
			xyzs[i++] = xyz;
		}
		return xyzs;
	}

	public native void plot(String divId, JavaScriptObject xsectionPoints,
			JavaScriptObject profilePoints, JavaScriptObject points)/*-{
		$wnd.plots.xsection_editor(divId, xsectionPoints, profilePoints, points);
	}-*/;

	public List<DataPoint> getXSectionProfilePoints() {
		List<DataPoint> points = new ArrayList<DataPoint>();
		for (XYZPoint xsectionPoint : xsectionPoints) {
			DataPoint p = new DataPoint();
			p.x = xsectionPoint.getX();
			p.y = xsectionPoint.getZ();
			p.z = xsectionPoint.getY();
			points.add(p);
		}
		return points;
	}
}
