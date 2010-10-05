package gov.ca.modeling.maps.elevation.client;


import gov.ca.modeling.maps.elevation.client.service.DEMDataService;
import gov.ca.modeling.maps.elevation.client.service.DEMDataServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.event.MapClickHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * This class listens for clicks on a MapWidget and displays the elevation at
 * that location in a infowindow
 * 
 * @author nsandhu
 * 
 */
public class ElevationDisplayer {
	private MapWidget map;
	private DEMDataServiceAsync service;
	private MapClickHandler handler;

	public ElevationDisplayer(MapWidget map) {
		this.service = (DEMDataServiceAsync) GWT.create(DEMDataService.class);
		this.map = map;
	}

	public void start() {
	 handler = new MapClickHandler() {

			public void onClick(MapClickEvent event) {
				final LatLng point = event.getLatLng();
				service.getElevationAt(point.getLatitude(), point
						.getLongitude(), new AsyncCallback<Double>() {

					public void onSuccess(Double result) {
						if (result.floatValue() <= -9998.9)
							return;
						InfoWindowContent content = new InfoWindowContent(
								"Elevation: " + result.floatValue() + " feet");
						ElevationDisplayer.this.map.getInfoWindow().open(point,
								content);
					}

					public void onFailure(Throwable caught) {
						GWT.log(caught.getMessage());
					}
				});
			}
		};
		this.map.addMapClickHandler(handler);
	}
	
	public void stop(){
		this.map.removeMapClickHandler(handler);
	}

}
