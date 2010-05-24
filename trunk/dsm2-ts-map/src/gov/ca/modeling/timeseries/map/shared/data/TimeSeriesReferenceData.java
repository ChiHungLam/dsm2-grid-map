package gov.ca.modeling.timeseries.map.shared.data;

import java.io.Serializable;

import javax.persistence.Id;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;

@SuppressWarnings("serial")
@Entity
public class TimeSeriesReferenceData implements Serializable {
	@Id
	private Long id;
	/**
	 * The source of the data. Could be the filename it originally came from
	 */
	private String source;

	/**
	 * Arbitrary user defined name
	 */
	private String name;

	/**
	 * User defined name that could be used to group references by this field
	 */
	private String group;

	/**
	 * Name of location for which this timeseries measures some variable
	 */
	private String location;

	/**
	 * Type of variable being measured, e.g flow, velocity
	 */
	private String type;

	/**
	 * Units of variable.
	 */
	private String units;

	/**
	 * Interval if any at which this data is regularly sampled. If it is
	 * irregularly sampled but probably has an average interval, use the
	 * designation IR- before the interval The interval is specified as 15MIN,
	 * 1HOUR etc.
	 */
	private String interval;

	/**
	 * The time window for this data set. Usually designated as a start time
	 * followed by a - and then a end time
	 */
	private String timeWindow;

	/**
	 * The actual container of the time series.
	 */
	private Key<TimeSeriesData> timeSeriesId;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public String getInterval() {
		return interval;
	}

	public void setInterval(String interval) {
		this.interval = interval;
	}

	public String getTimeWindow() {
		return timeWindow;
	}

	public void setTimeWindow(String timeWindow) {
		this.timeWindow = timeWindow;
	}

	public Key<TimeSeriesData> getTimeSeriesId() {
		return timeSeriesId;
	}

	public void setTimeSeriesId(Key<TimeSeriesData> timeSeriesId) {
		this.timeSeriesId = timeSeriesId;
	}

	public long getId() {
		return id;
	}
}
