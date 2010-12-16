/**
 *   Copyright (C) 2009, 2010 
 *    Nicky Sandhu
 *    State of California,
 *    Department of Water Resources.
 *    This file is part of DSM2 Grid Map
 *    The DSM2 Grid Map is free software: 
 *    you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *    DSM2 Grid Map is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.

 *    You should have received a copy of the GNU General Public License
 *    along with DSM2 Grid Map.  If not, see <http://www.gnu.org/licenses>.
 */
package gov.ca.bdo.modeling.dsm2.map.client.map;

import gov.ca.dsm2.input.model.Reservoir;
import gov.ca.dsm2.input.model.ReservoirConnection;
import gov.ca.modeling.maps.elevation.client.model.CalculationState;
import gov.ca.modeling.maps.elevation.client.model.DataPoint;
import gov.ca.modeling.maps.elevation.client.model.GeomUtils;
import gov.ca.modeling.maps.elevation.client.service.DEMDataService;
import gov.ca.modeling.maps.elevation.client.service.DEMDataServiceAsync;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Panel;

public class ReservoirInfoPanel extends Composite {

	private Button bottomElevationButton;
	private FlexTable table;

	public ReservoirInfoPanel(Reservoir reservoir) {
		Panel basicInfo = getBasicInfoPanel(reservoir);
		initWidget(basicInfo);
	}

	private Panel getBasicInfoPanel(final Reservoir reservoir) {
		FlowPanel panel = new FlowPanel();
		panel.add(new HTMLPanel("<h3>Reservoir " + reservoir.getName()
				+ "</h3>"));
		table = new FlexTable();
		table.setHTML(1, 0, "Area (Million Sq. Feet): " + reservoir.getArea());
		setBottomElevation("Bottom Elevation (Feet): "
				+ reservoir.getBottomElevation());
		bottomElevationButton = new Button("Recalculate Bottom Elevation");
		table.setWidget(2, 2, bottomElevationButton);
		bottomElevationButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				final DEMDataServiceAsync service = GWT
						.create(DEMDataService.class);
				List<double[]> latLngPoints = reservoir.getLatLngPoints();
				List<DataPoint> points = new ArrayList<DataPoint>();
				for (int i = 0; i < latLngPoints.size(); i++) {
					DataPoint p = new DataPoint();
					double[] ll = latLngPoints.get(i);
					double[] utm = GeomUtils.convertToUTM(ll[0], ll[1]);
					p.x = utm[0];
					p.y = utm[1];
					points.add(p);
				}
				service.startCalculationOfAverageElevationInArea(points,
						new AsyncCallback<CalculationState>() {
							private Timer timer;
							private CalculationState state;

							public void onSuccess(CalculationState result) {
								state = result;
								timer = new Timer() {

									@Override
									public void run() {
										if (state.numberOfTasks == state.numberOfCompletedTasks) {
											timer.cancel();
											reservoir.setBottomElevation(state.latestValue);
											setBottomElevation("Bottom Elevation (Feet): "+reservoir.getBottomElevation());
										} else {
											service
													.checkStatus(
															state,
															new AsyncCallback<CalculationState>() {

																public void onFailure(
																		Throwable caught) {
																	timer
																			.cancel();
																}

																public void onSuccess(
																		CalculationState result) {
																	state = result;
																	setBottomElevation("Calculation in progress "
																			+ (state.numberOfCompletedTasks
																					/ state.numberOfTasks * 100)
																			+ "% complete");
																	timer.schedule(2000);
																}
															});
										}

									}
								};
								timer.schedule(2000);
							}

							public void onFailure(Throwable caught) {
								timer = null;
							}
						});

			}
		});
		panel.add(table);
		FlexTable connectionTable = new FlexTable();
		connectionTable.setStyleName("bordered-title");
		connectionTable.setWidth("100%");
		connectionTable.setHTML(0, 0, "<b>NODE</b>");
		connectionTable.setHTML(0, 1, "<b>COEFF IN</b>");
		connectionTable.setHTML(0, 2, "<b>COEFF OUT</b>");
		connectionTable.getRowFormatter().setStyleName(0, "table-header");
		List<ReservoirConnection> reservoirConnections = reservoir
				.getReservoirConnections();
		int index = 1;
		for (ReservoirConnection reservoirConnection : reservoirConnections) {
			connectionTable.setHTML(index, 0, reservoirConnection.nodeId);
			connectionTable.setHTML(index, 1, ""
					+ reservoirConnection.coefficientIn);
			connectionTable.setHTML(index, 2, ""
					+ reservoirConnection.coefficientOut);
			index++;
		}
		panel.add(connectionTable);
		return panel;
	}

	public void setBottomElevation(String msg) {
		table.setHTML(2, 0, msg);
	}
}
