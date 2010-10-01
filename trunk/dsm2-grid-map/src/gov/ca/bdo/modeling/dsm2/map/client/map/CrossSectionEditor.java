package gov.ca.bdo.modeling.dsm2.map.client.map;

import gov.ca.bdo.modeling.dsm2.map.client.model.DataPoint;
import gov.ca.bdo.modeling.dsm2.map.client.model.XSectionProfile;
import gov.ca.bdo.modeling.dsm2.map.client.model.XYZPoint;

import java.util.List;

import com.google.gwt.user.client.ui.Composite;

public class CrossSectionEditor extends Composite {
	public CrossSectionEditor(String divId, XSectionProfile xsProfile,
			List<DataPoint> profile, List<DataPoint> bathymetry) {
		XYZPoint[] xsectionPoints = convertDataToXYZPoints(xsProfile.points);
		XYZPoint[] profilePoints = convertDataToXYZPoints(profile);
		XYZPoint[] points = convertDataToXYZPoints(bathymetry);
		plot(divId, xsectionPoints, profilePoints, points);
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

	public native void plot(String divId, XYZPoint[] xsectionPoints,
			XYZPoint[] profilePoints, XYZPoint[] points)/*-{
		$wnd.plots.xsection_editor(divId, xsection_points, profile, points);
	}-*/;

}
