package gov.ca.cdec.maps.client;

import gov.ca.cdec.maps.client.model.Sensor;
import gov.ca.cdec.maps.client.model.Station;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapUIOptions;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.OverviewMapControl;
import com.google.gwt.maps.client.event.MarkerClickHandler;
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
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;

public class MapPanel extends Composite {
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
		getMap().setSize("1000", "600");
		setOptions();
		dataDisplayPanel = new FlowPanel();
		controlPanel = new MapControlPanel(this);
		// layout top level things here
		FlexTable table = new FlexTable();
		table.setWidget(0, 0, getMap());
		table.setWidget(1, 0, controlPanel);
		table.setWidget(2, 0, dataDisplayPanel);
		initWidget(table);
		requestMarkerData();
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

	private void requestMarkerData() {
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET,
				"/stations.json.txt");
		requestBuilder.setCallback(new RequestCallback() {

			public void onResponseReceived(Request request, Response response) {
				String text = response.getText();
				sensorDescriptions = new HashMap<String, String>();
				JSONValue value = JSONParser.parse(text);
				JSONArray array = value.isArray();
				ArrayList<Station> stations = new ArrayList<Station>();
				for (int i = 0; i < array.size(); i++) {
					JSONValue itemValue = array.get(i);
					JSONObject stationObject = itemValue.isObject();
					Station station = new Station();
					station.displayName = stationObject.get("displayName")
							.isString().stringValue();
					station.stationId = stationObject.get("stationId")
							.isString().stringValue();
					station.latitude = stationObject.get("latitude").isNumber()
							.doubleValue();
					station.longitude = stationObject.get("longitude")
							.isNumber().doubleValue();
					JSONArray sensorArray = stationObject.get("sensors")
							.isArray();
					ArrayList<Sensor> sensors = new ArrayList<Sensor>();
					for (int k = 0; k < sensorArray.size(); k++) {
						sensors.add(buildSensor(sensorArray.get(k).isObject()));
					}
					station.sensors = sensors;
					station.elevation = stationObject.get("elevation")
							.isString().stringValue();
					stations.add(station);
				}
				markStations(stations);
				controlPanel.refreshSensorDescriptions();
			}

			private Sensor buildSensor(JSONObject object) {
				Sensor sensor = new Sensor();
				sensor.dataAvailable = object.get("dataAvailable").isString()
						.stringValue();
				sensor.dataCollection = object.get("dataCollection").isString()
						.stringValue();
				sensor.description = object.get("description").isString()
						.stringValue();
				if (!sensorDescriptions.containsKey(sensor.description)) {
					sensorDescriptions.put(sensor.description,
							sensor.description);
				}
				sensor.duration = object.get("duration").isString()
						.stringValue();
				sensor.sensorNumber = object.get("sensorNumber").isString()
						.stringValue();
				return sensor;
			}

			public void onError(Request request, Throwable exception) {
				Window.alert("Nodes data file is missing: dsm2_nodes.json");
			}
		});
		try {
			requestBuilder.send();
		} catch (RequestException e) {
			e.printStackTrace();
		}
	}

	protected void markStations(ArrayList<Station> stations) {
		stationMap = new HashMap<String, Station>();
		stationMarkerMap = new HashMap<String, Marker>();
		Marker[] markers = new Marker[stations.size()];
		Icon icon = Icon.newInstance("images/greencirclemarker.png");
		icon.setIconSize(Size.newInstance(32, 32));
		icon.setIconAnchor(Point.newInstance(16, 16));
		icon.setInfoWindowAnchor(Point.newInstance(25, 7));
		int index = 0;
		for (final Station station : stations) {
			stationMap.put(station.stationId, station);
			LatLng latlng = LatLng.newInstance(station.latitude,
					station.longitude);
			LabeledMarkerOptions opts = LabeledMarkerOptions.newInstance();
			opts.setIcon(icon);
			opts.setClickable(true);
			opts.setLabelOffset(Size.newInstance(-10, -6));
			opts.setLabelText(station.stationId);
			opts.setLabelClass("hm-marker-label");
			opts.setClickable(true);
			opts.setTitle(station.stationId);
			final Marker marker = new LabeledMarker(latlng, opts);
			markers[index++] = marker;
			stationMarkerMap.put(station.stationId, marker);
			marker.addMarkerClickHandler(new MarkerClickHandler() {

				public void onClick(MarkerClickEvent event) {
					FlowPanel panel = new FlowPanel();
					Anchor dataLink = new Anchor(station.displayName);
					dataLink.addClickHandler(new ClickHandler() {

						public void onClick(ClickEvent event) {
							RequestBuilder requestBuilder = new RequestBuilder(
									RequestBuilder.GET, "/cgi-progs/queryF?s="
											+ station.stationId);
							requestBuilder.setCallback(new RequestCallback() {

								public void onResponseReceived(Request request,
										Response response) {
									String responseText = response.getText();
									dataDisplayPanel.clear();
									dataDisplayPanel.add(new HTMLPanel(
											responseText));
								}

								public void onError(Request request,
										Throwable exception) {
									dataDisplayPanel.clear();
									dataDisplayPanel.add(new HTMLPanel(
											"Could not fetch data for "
													+ station.stationId
													+ "<pre>"
													+ exception.getMessage()
													+ "</pre>"));
								}
							});
							try {
								requestBuilder.send();
							} catch (RequestException ex) {
								dataDisplayPanel.clear();
								dataDisplayPanel.add(new HTMLPanel(
										"Could not fetch data for "
												+ station.stationId + "<pre>"
												+ ex.getMessage() + "</pre>"));
							}

						}
					});
					panel.add(dataLink);
					map.getInfoWindow().open(marker,
							new InfoWindowContent(panel));
				}
			});
		}
		MarkerClustererOptions clusterOptions = MarkerClustererOptions
				.newInstance();
		clusterOptions.setGridSize(100);
		clusterOptions.setMaxZoom(10);
		markerClusterer = MarkerClusterer.newInstance(map, markers,
				clusterOptions);
	}

	public String[] getSensorDescriptions() {
		String[] array = new String[sensorDescriptions.size()];
		sensorDescriptions.keySet().toArray(array);
		return array;
	}

	public void setSensorDescription(String sensorSelected) {
		markerClusterer.clearMarkers();
		ArrayList<Marker> markers = new ArrayList<Marker>();
		for (String id : stationMap.keySet()) {
			Station station = stationMap.get(id);
			for (Sensor sensor : station.sensors) {
				if (sensor.description.equalsIgnoreCase(sensorSelected)) {
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
}
