package gov.ca.modeling.simple.elevation.chart.client;

import gov.ca.modeling.maps.elevation.client.CrossSectionEditor;
import gov.ca.modeling.maps.elevation.client.model.BathymetryDataPoint;
import gov.ca.modeling.maps.elevation.client.model.DataPoint;
import gov.ca.modeling.maps.elevation.client.model.Profile;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SimpleElevationChart implements EntryPoint {

	private CrossSectionEditor editor;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		final Profile xsProfile = new Profile();
		final List<DataPoint> profile = new ArrayList<DataPoint>();
		profile.add(createDataPoint(0.5, 1.03, 10));
		profile.add(createDataPoint(0.75, 2.1, -5.3));
		profile.add(createDataPoint(10.5, 3.1, -5.2));
		profile.add(createDataPoint(10.3, 4.1, 10.44));
		xsProfile.points = new ArrayList<DataPoint>(profile);
		xsProfile.id = 993884;
		xsProfile.x1 = 0.2;
		xsProfile.y1 = 0.5;
		xsProfile.x2 = 10.2;
		xsProfile.y2 = 10.3;
		final List<BathymetryDataPoint> bathymetry = new ArrayList<BathymetryDataPoint>();
		bathymetry.add(createBathymetryDataPoint(2.5, 53.4, 10.3, 1990));
		bathymetry.add(createBathymetryDataPoint(3.1, 152.3, -6.1, 2000));
		bathymetry.add(createBathymetryDataPoint(8.33, 250.4, 6.3, 2010));
		Button sampleButton = new Button("Sample Plot");
		sampleButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				editor = null;
				sample_plot();
			}
		});
		final HTML infoArea = new HTML();
		Button xsectionButton = new Button("XSection Plot");
		xsectionButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (editor == null) {
					editor = new CrossSectionEditor("xsect", xsProfile,
							profile, bathymetry, 600, 450);
				}
				List<DataPoint> profilePoints = editor
						.getXSectionProfilePoints();
				StringBuffer b = new StringBuffer();
				for (DataPoint p : profilePoints) {
					b.append("x:").append(p.x).append(" y:").append(p.y)
							.append(" z:").append(p.z);
					b.append("<br/>");
				}
				infoArea.setHTML(b.toString());

			}
		});
		FlowPanel controlPanel = new FlowPanel();
		controlPanel.add(xsectionButton);
		controlPanel.add(infoArea);
		RootPanel.get("control").add(controlPanel);
	}

	public native void sample_plot() /*-{
		var profile = [{x: -400, y: -4.5}, {x:-100, y: -8.0}, {x:-25, y: -9.7}, {x:155, y:-7.6}, {x: 300, y:-4.3}, {x: 355, y: -3.1}];
		var points = $wnd.pv.range(1,100).map(function(i) { return (
		{x: Math.random()*800-400, y:-10+Math.random()*7, z: Math.random()*400}
		);
		});
		var xsection_points = [{x: -400, y: -4.5}, {x:-100, y: -8.0}, {x:-25, y: -9.7}, {x:155, y:-7.6}, {x: 300, y:-4.3}, {x: 355, y: -3.1}];
		$wnd.plots.xsection_editor('sample_plot',xsection_points, profile, points);
	}-*/;

	private DataPoint createDataPoint(double x, double y, double z) {
		DataPoint p = new DataPoint();
		p.x = x;
		p.y = y;
		p.z = z;
		return p;
	}
	
	private BathymetryDataPoint createBathymetryDataPoint(double x, double y, double z, int year){
		BathymetryDataPoint p = new BathymetryDataPoint();
		p.x = x;
		p.y = y;
		p.z = z;
		p.year = year;
		return p;
	}
}
