package gov.ca.modeling.maps.elevation.client;

import gov.ca.modeling.maps.elevation.client.model.BathymetryDataPoint;
import gov.ca.modeling.maps.elevation.client.model.DataPoint;
import gov.ca.modeling.maps.elevation.client.model.Profile;
import gov.ca.modeling.maps.elevation.client.model.XYZPoint;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.Composite;

public class CrossSectionEditor extends Composite {
	private JavaScriptObject xpArray;
	private JavaScriptObject dsm2xpArray;
	private XYZPoint[] xsectionPoints;
	private XYZPoint[] dsm2xsPoints;
	private JavaScriptObject vis;
	private XYZPoint[] profilePoints;
	private XYZPoint[] points;
	private String divId;
	private int width;
	private int height;

	public CrossSectionEditor(String divId, Profile xsProfile, Profile dsm2xs,
			List<DataPoint> profile, List<BathymetryDataPoint> bathymetry,
			int width, int height) {
		this.divId = divId;
		xsectionPoints = convertDataToXYZPoints(xsProfile.points);
		if (dsm2xs != null) {
			dsm2xsPoints = convertDataToXYZPoints(dsm2xs.points);
			dsm2xpArray = ArrayUtils.toJsArray(dsm2xsPoints);
		}
		profilePoints = convertDataToXYZPoints(profile);
		points = convertDataToXYZPoints(bathymetry);
		this.width = width;
		this.height = height;
		vis = plot(divId, xpArray = ArrayUtils.toJsArray(xsectionPoints),
				dsm2xpArray, ArrayUtils.toJsArray(profilePoints), ArrayUtils
						.toJsArray(points));
	}

	private XYZPoint[] convertDataToXYZPoints(List<? extends DataPoint> points) {
		XYZPoint[] xyzs = new XYZPoint[points.size()];
		int i = 0;
		for (DataPoint point : points) {
			XYZPoint xyz = XYZPoint.create(point.x, point.z, point.y);
			if (point instanceof BathymetryDataPoint) {
				xyz.setYear(((BathymetryDataPoint) point).year);
			}
			xyzs[i++] = xyz;
		}
		return xyzs;
	}

	private native JavaScriptObject plot(String divId,
			JavaScriptObject xsectionPoints, JavaScriptObject dsm2xpPoints,
			JavaScriptObject profilePoints, JavaScriptObject points)/*-{
		var w = this.@gov.ca.modeling.maps.elevation.client.CrossSectionEditor::width;
		var h = this.@gov.ca.modeling.maps.elevation.client.CrossSectionEditor::height;
		return $wnd.plots.xsection_editor(divId, xsectionPoints, dsm2xpPoints, profilePoints, points, w, h);
	}-*/;

	public void setXSectionProfile(Profile profile) {
		xsectionPoints = convertDataToXYZPoints(profile.points);
		vis = plot(divId, xpArray = ArrayUtils.toJsArray(xsectionPoints),
				dsm2xpArray, ArrayUtils.toJsArray(profilePoints), ArrayUtils
						.toJsArray(points));
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

	private native void redraw()/*-{
		this.@gov.ca.modeling.maps.elevation.client.CrossSectionEditor::vis.render();
	}-*/;
}
