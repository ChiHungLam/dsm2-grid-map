package gov.ca.cdec.maps.client;

import gov.ca.cdec.maps.client.model.Sensor;
import gov.ca.cdec.maps.client.model.Station;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapUIOptions;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.OverviewMapControl;
import com.google.gwt.maps.client.event.MapZoomEndHandler;
import com.google.gwt.maps.client.event.MarkerClickHandler;
import com.google.gwt.maps.client.event.MapZoomEndHandler.MapZoomEndEvent;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.geom.Size;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.utility.client.labeledmarker.LabeledMarker;
import com.google.gwt.maps.utility.client.labeledmarker.LabeledMarkerOptions;
import com.google.gwt.maps.utility.client.markerclusterer.MarkerClusterer;
import com.google.gwt.maps.utility.client.markerclusterer.MarkerClustererOptions;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;

public class MapPanel extends Composite {
	private final class MarkerShowDataHandler implements MarkerClickHandler {
		private HashMap<String, Station> stationMap;

		private MarkerShowDataHandler(HashMap<String, Station> stationMap) {
			this.stationMap = stationMap;
		}

		public void onClick(MarkerClickEvent event) {
			Marker marker = event.getSender();
			FlowPanel panel = new FlowPanel();
			final Station station = stationMap.get(marker.getTitle());
			Anchor dataLink = new Anchor(station.getDisplayName());
			dataLink.addClickHandler(new ClickHandler() {

				public void onClick(ClickEvent event) {
					RequestBuilder requestBuilder = new RequestBuilder(
							RequestBuilder.GET, "/cgi-progs/queryF?s="
									+ station.getStationId());
					requestBuilder.setCallback(new RequestCallback() {

						public void onResponseReceived(Request request,
								Response response) {
							String responseText = response.getText();
							dataDisplayPanel.clear();
							dataDisplayPanel.add(new HTMLPanel(responseText));
							NodeList<Element> nodeList = dataDisplayPanel
									.getElement().getElementsByTagName("a");
							nodeList.getItem(0).scrollIntoView();
						}

						public void onError(Request request, Throwable exception) {
							dataDisplayPanel.clear();
							dataDisplayPanel
									.add(new HTMLPanel(
											"Could not fetch data for "
													+ station.getStationId()
													+ "<pre>"
													+ exception.getMessage()
													+ "</pre>"));
							dataDisplayPanel.getElement().scrollIntoView();
						}
					});
					try {
						requestBuilder.send();
					} catch (RequestException ex) {
						dataDisplayPanel.clear();
						dataDisplayPanel.add(new HTMLPanel(
								"Could not fetch data for "
										+ station.getStationId() + "<pre>"
										+ ex.getMessage() + "</pre>"));
					}

				}
			});
			panel.add(dataLink);
			map.getInfoWindow().open(marker, new InfoWindowContent(panel));
		}
	}

	private MapWidget map;
	private FlowPanel dataDisplayPanel;
	private HashMap<String, String> sensorDescriptions;
	private MarkerClusterer markerClusterer;
	private HashMap<String, Station> stationMap;
	private HashMap<String, Marker> stationMarkerMap;
	private MapControlPanel controlPanel;

	public MapPanel() {
		sensorDescriptions = new HashMap<String, String>();
		setMap(new MapWidget(LatLng.newInstance(38.15, -121.70), 9));
		setOptions();
		getMap().setSize("900px", "600px");
		initWidget(getMap());
		getMap().addMapZoomEndHandler(new MapZoomEndHandler() {
			
			@Override
			public void onZoomEnd(MapZoomEndEvent event) {
				if (markerClusterer != null){
					markerClusterer.resetViewport();
				}
			}
		});
	}

	void setDataDisplayPanel(FlowPanel p) {
		dataDisplayPanel = p;
	}

	void setControlPanel(MapControlPanel p) {
		controlPanel = p;
	}

	private void setOptions() {
		MapUIOptions options = MapUIOptions.newInstance(getMap().getSize());
		options.setDoubleClick(true);
		options.setKeyboard(true);
		options.setMapTypeControl(true);
		options.setMenuMapTypeControl(false);
		options.setNormalMapType(true);
		options.setPhysicalMapType(true);
		options.setSatelliteMapType(true);
		options.setScaleControl(true);
		options.setScrollwheel(true);
		options.setSmallZoomControl3d(true);
		getMap().setUI(options);
		OverviewMapControl control = new OverviewMapControl();
		getMap().addControl(control);
	}

	public void setMap(MapWidget map) {
		this.map = map;
	}

	public MapWidget getMap() {
		return map;
	}

	protected void markStations(JsArray<Station> stations) {
		stationMap = new HashMap<String, Station>();
		stationMarkerMap = new HashMap<String, Marker>();
		ArrayList<Marker> markers = new ArrayList<Marker>();
		Icon icon = Icon.newInstance("images/greencirclemarker.png");
		icon.setIconSize(Size.newInstance(32, 32));
		icon.setIconAnchor(Point.newInstance(16, 16));
		icon.setInfoWindowAnchor(Point.newInstance(25, 7));
		MarkerShowDataHandler clickHandler = new MarkerShowDataHandler(
				stationMap);
		int l = stations.length();
		for (int i = 0; i < l; i++) {
			Station station = stations.get(i);
			updateSensorDescriptionsMap(station);
			stationMap.put(station.getStationId(), station);
			LatLng latlng = LatLng.newInstance(station.getLatitude(), station
					.getLongitude());
			LabeledMarkerOptions opts = LabeledMarkerOptions.newInstance();
			opts.setIcon(icon);
			opts.setClickable(true);
			opts.setLabelOffset(Size.newInstance(-10, -6));
			opts.setLabelText(station.getStationId());
			opts.setLabelClass("hm-marker-label");
			opts.setClickable(true);
			opts.setTitle(station.getStationId());
			final Marker marker = new LabeledMarker(latlng, opts);
			markers.add(marker);
			stationMarkerMap.put(station.getStationId(), marker);
			marker.addMarkerClickHandler(clickHandler);
		}
		MarkerClustererOptions clusterOptions = MarkerClustererOptions
				.newInstance();
		clusterOptions.setGridSize(100);
		clusterOptions.setMaxZoom(10);
		markerClusterer = MarkerClusterer.newInstance(map, markers
				.toArray(new Marker[markers.size()]), clusterOptions);
	}

	private void updateSensorDescriptionsMap(Station station) {
		JsArray<Sensor> sensors = station.getSensors();
		int l=sensors.length();
		for (int i=0; i<l; i++) {
			Sensor sensor = sensors.get(i);
			String description = sensor.getDescription();
			sensorDescriptions.put(description, description);
		}		
	}

	public String[] getSensorDescriptions() {
		String[] array = new String[sensorDescriptions.size()];
		sensorDescriptions.keySet().toArray(array);
		return array;
	}

	public void setSensorDescription(String sensorSelected) {
		markerClusterer.resetViewport();
		markerClusterer.clearMarkers();
		ArrayList<Marker> markers = new ArrayList<Marker>();
		for (String id : stationMap.keySet()) {
			Station station = stationMap.get(id);
			JsArray<Sensor> sensors = station.getSensors();
			int l=sensors.length();
			for (int i=0; i<l; i++) {
				Sensor sensor = sensors.get(i);
				if (sensorSelected.equals("ALL")
						|| sensor.getDescription().equalsIgnoreCase(
								sensorSelected)) {
					markers.add(stationMarkerMap.get(id));
				}
			}
		}
		MarkerClustererOptions clusterOptions = MarkerClustererOptions
				.newInstance();
		clusterOptions.setGridSize(100);
		clusterOptions.setMaxZoom(10);
		markerClusterer = MarkerClusterer.newInstance(map, markers
				.toArray(new Marker[markers.size()]), clusterOptions);
	}

	public void onResize() {
		map.checkResizeAndCenter();
	}

	public void showMarkers() {
		markStations(getStations());
		controlPanel.refreshSensorDescriptions();
	}

	public final native JsArray<Station> getStations()/*-{
		return $wnd.stations;
	}-*/;

}
