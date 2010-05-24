package gov.ca.modeling.timeseries.map.shared.data;

import java.io.Serializable;

import javax.persistence.Id;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;

@SuppressWarnings("serial")
@Entity
public class MapTimeSeriesMarkerData implements Serializable {
	@Id
	private Long id;
	private String name;
	// private Key<MapMarkerType> type;
	private double latitude;
	private double longitude;
	private Key<TimeSeriesReferenceData>[] references;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public Key<TimeSeriesReferenceData>[] getReferences() {
		return references;
	}

	public void setReferences(Key<TimeSeriesReferenceData>[] references) {
		this.references = references;
	}

	public Long getId() {
		return id;
	}
}
