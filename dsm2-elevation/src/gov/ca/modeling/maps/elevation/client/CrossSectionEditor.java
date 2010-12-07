package gov.ca.modeling.maps.elevation.client;

import gov.ca.modeling.maps.elevation.client.model.DataPoint;
import gov.ca.modeling.maps.elevation.client.model.Profile;
import gov.ca.modeling.maps.elevation.client.model.XYZPoint;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.Composite;

public class CrossSectionEditor extends Composite {
	private JavaScriptObject xpArray;
	private XYZPoint[] xsectionPoints;
	private JavaScriptObject vis;
	private XYZPoint[] profilePoints;
	private XYZPoint[] points;
	private String divId;

	public CrossSectionEditor(String divId, Profile xsProfile,
			List<DataPoint> profile, List<DataPoint> bathymetry) {
		this.divId = divId;
		xsectionPoints = convertDataToXYZPoints(xsProfile.points);
		profilePoints = convertDataToXYZPoints(profile);
		points = convertDataToXYZPoints(bathymetry);
		vis = plot(divId, xpArray = ArrayUtils.toJsArray(xsectionPoints), ArrayUtils
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

	public native JavaScriptObject plot(String divId, JavaScriptObject xsectionPoints,
			JavaScriptObject profilePoints, JavaScriptObject points)/*-{
		return $wnd.plots.xsection_editor(divId, xsectionPoints, profilePoints, points);
	}-*/;
	
	public void setXSectionProfile(Profile profile){
		xsectionPoints = convertDataToXYZPoints(profile.points);
		vis = plot(divId, xpArray = ArrayUtils.toJsArray(xsectionPoints), ArrayUtils
				.toJsArray(profilePoints), ArrayUtils.toJsArray(points));
	}

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
	
	public native void redraw()/*-{
		this.vis.render();
	}-*/;
}
