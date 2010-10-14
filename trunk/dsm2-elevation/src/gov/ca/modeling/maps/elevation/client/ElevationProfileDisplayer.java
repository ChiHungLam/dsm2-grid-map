package gov.ca.modeling.maps.elevation.client;

import gov.ca.modeling.maps.elevation.client.model.CoordinateGeometryUtils;
import gov.ca.modeling.maps.elevation.client.model.DEMGridSquare;
import gov.ca.modeling.maps.elevation.client.model.DataPoint;
import gov.ca.modeling.maps.elevation.client.model.GeomUtils;
import gov.ca.modeling.maps.elevation.client.model.XSectionProfile;
import gov.ca.modeling.maps.elevation.client.service.DEMDataService;
import gov.ca.modeling.maps.elevation.client.service.DEMDataServiceAsync;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.event.PolylineLineUpdatedHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.PolyStyleOptions;
import com.google.gwt.maps.client.overlay.Polyline;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.LegendPosition;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.visualizations.ScatterChart;
import com.google.gwt.visualization.client.visualizations.ScatterChart.Options;

/**
 * Enables user to draw a line over a map. Displays a cross section of
 * elevations along that line in the provided widget
 * 
 * @author nsandhu
 * 
 */
public class ElevationProfileDisplayer {
	private MapWidget map;
	private FlowPanel panel;
	private Polyline line;
	private DEMDataServiceAsync service;
	private boolean editMode = false;
	private CrossSectionEditor editor;

	public ElevationProfileDisplayer(MapWidget map, FlowPanel panel) {
		service = (DEMDataServiceAsync) GWT.create(DEMDataService.class);
		this.map = map;
		this.panel = panel;
	}

	public void startDrawingLine(final ToggleButton drawLineButton) {
		PolyStyleOptions style = PolyStyleOptions.newInstance("blue", 3, 1);
		stopDrawingLine();
		line = new Polyline(new LatLng[0]);
		map.addOverlay(line);
		line.setDrawingEnabled();
		line.setEditingEnabled(true);
		line.setStrokeStyle(style);
		line.addPolylineLineUpdatedHandler(new PolylineLineUpdatedHandler() {

			public void onUpdate(PolylineLineUpdatedEvent event) {
				if (line.getVertexCount() == 2) {
					line.setEditingEnabled(false);
					drawLineButton.setDown(false);
					fetchProfile();
				}
			}
		});

	}

	public void stopDrawingLine() {
		if (line != null) {
			line.setEditingEnabled(false);
			map.removeOverlay(line);
		}
	}

	private void fetchProfile() {
		panel.clear();
		LatLng latLng1 = line.getVertex(0);
		LatLng latLng2 = line.getVertex(1);
		service.getElevationAlong(latLng1.getLatitude(),
				latLng1.getLongitude(), latLng2.getLatitude(), latLng2
						.getLongitude(), new AsyncCallback<List<DataPoint>>() {

					public void onSuccess(final List<DataPoint> result) {
						panel.clear();
						if (result == null) {
							Label label = new Label("NO DATA AVAILABLE!");
							label.setStyleName("warning");
							panel.add(label);
						} else {
							if (editMode) {
								XSectionProfile xsProfile = new XSectionProfile();
								xsProfile.points = new ArrayList<DataPoint>(
										result);
								List<DataPoint> bathymetry = new ArrayList<DataPoint>();
								editor = new CrossSectionEditor("xsect",
										xsProfile, result, bathymetry);

							} else {
								VisualizationUtils.loadVisualizationApi(
										new Runnable() {

											public void run() {
												panel.add(drawProfile(result));
											}
										}, ScatterChart.PACKAGE);
							}
						}
					}

					public void onFailure(Throwable caught) {
						GWT.log(caught.getMessage());
					}
				});
	}

	protected Widget drawProfile(List<DataPoint> result) {
		String title = " X-Section View";
		Options options = Options.create();
		options.setHeight(350);
		options.setTitle(title);
		options.setTitleX("Length (ft)");
		options.setTitleY("Elevation (ft)");
		options.setWidth(panel.getParent().getParent().getOffsetWidth());
		options.setLineSize(1);
		options.setLegend(LegendPosition.BOTTOM);
		options.setShowCategories(false);
		DataTable table = DataTable.create();
		table.insertRows(0, result.size());
		table.addColumn(ColumnType.NUMBER, "Length");
		table.addColumn(ColumnType.NUMBER, "Elevation");
		int row = 0;
		double x0 = 0;
		double y0 = 0;
		if (result.size() > 0) {
			x0 = result.get(0).x;
			y0 = result.get(0).y;
		}
		double nodata = DEMGridSquare.NODATA / 10.;
		for (DataPoint point : result) {
			table.setValue(row, 0, GeomUtils
					.getLengthInFeet(CoordinateGeometryUtils.distanceBetween(
							x0, y0, point.x, point.y)));
			if (Math.abs(point.z - nodata) < 1e-5) {

			} else {
				table.setValue(row, 1, point.z);
			}
			row++;
		}
		options.setColors("blue");
		ScatterChart chart = new ScatterChart(table, options);
		return chart;
	}

	public boolean isEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	public List<DataPoint> getXSectionProfilePoints() {
		return editor.getXSectionProfilePoints();
	}

}
