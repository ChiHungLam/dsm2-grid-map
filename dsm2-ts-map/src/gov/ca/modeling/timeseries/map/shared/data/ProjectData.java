package gov.ca.modeling.timeseries.map.shared.data;

import javax.persistence.Id;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;

@Entity
public class ProjectData {
	@Id
	private Long id;
	private String name;
	private Key<MapTimeSeriesMarkerData>[] timeSeriesMarkers;
	private Key<MapTextAnnotation>[] textAnnotations;
	// map center lat lng & zoom level
	private double mapCenterLatitude;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Key<MapTimeSeriesMarkerData>[] getTimeSeriesMarkers() {
		return timeSeriesMarkers;
	}

	public void setTimeSeriesMarkers(
			Key<MapTimeSeriesMarkerData>[] timeSeriesMarkers) {
		this.timeSeriesMarkers = timeSeriesMarkers;
	}

	public Key<MapTextAnnotation>[] getTextAnnotations() {
		return textAnnotations;
	}

	public void setTextAnnotations(Key<MapTextAnnotation>[] textAnnotations) {
		this.textAnnotations = textAnnotations;
	}

	public double getMapCenterLatitude() {
		return mapCenterLatitude;
	}

	public void setMapCenterLatitude(double mapCenterLatitude) {
		this.mapCenterLatitude = mapCenterLatitude;
	}

	public double getMapCenterLongitude() {
		return mapCenterLongitude;
	}

	public void setMapCenterLongitude(double mapCenterLongitude) {
		this.mapCenterLongitude = mapCenterLongitude;
	}

	public int getMapZoomLevel() {
		return mapZoomLevel;
	}

	public void setMapZoomLevel(int mapZoomLevel) {
		this.mapZoomLevel = mapZoomLevel;
	}

	public Long getId() {
		return id;
	}

	private double mapCenterLongitude;
	private int mapZoomLevel;
}
