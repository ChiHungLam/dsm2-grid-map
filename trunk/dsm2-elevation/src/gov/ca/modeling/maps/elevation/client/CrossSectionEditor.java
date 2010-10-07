package gov.ca.modeling.maps.elevation.client;

import gov.ca.modeling.maps.elevation.client.model.DataPoint;
import gov.ca.modeling.maps.elevation.client.model.XSectionProfile;
import gov.ca.modeling.maps.elevation.client.model.XYZPoint;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.Composite;

public class CrossSectionEditor extends Composite {
	private JavaScriptObject xpArray;
	private XYZPoint[] xsectionPoints;

	public CrossSectionEditor(String divId, XSectionProfile xsProfile,
			List<DataPoint> profile, List<DataPoint> bathymetry) {
		xsectionPoints = convertDataToXYZPoints(xsProfile.points);
		XYZPoint[] profilePoints = convertDataToXYZPoints(profile);
		XYZPoint[] points = convertDataToXYZPoints(bathymetry);
		plot(divId, xpArray = ArrayUtils.toJsArray(xsectionPoints), ArrayUtils
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
		int count = ArrayUtils.lengthArray(xpArray);
		for (int i = 0; i < count; i++) {
			DataPoint p = new DataPoint();
			XYZPoint xp = (XYZPoint) ArrayUtils.getElementAt(xpArray, i);
			p.x = xp.getX();
			p.z = xp.getY();
			points.add(p);
		}
		return points;
	}
}
