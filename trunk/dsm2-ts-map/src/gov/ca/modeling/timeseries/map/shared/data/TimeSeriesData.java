package gov.ca.modeling.timeseries.map.shared.data;

import java.io.Serializable;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Serialized;

@SuppressWarnings("serial")
@Entity
/**
 * If interval is defined, i.e. not null, then the timeValues array is ignored
 * The series is then assumed to be a regular time series where each value
 * starting with startTime is at this interval from the next point.
 */
public class TimeSeriesData implements Serializable {
	@Id
	private Long id;
	private String startTime;
	private String interval;
	// this strategy limits us to 125000 values due to the 1MB data store limit
	// on blobs
	@Serialized
	private long[] timeValues;
	@Serialized
	private double[] values;

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getInterval() {
		return interval;
	}

	public void setInterval(String interval) {
		this.interval = interval;
	}

	public long[] getTimeValues() {
		return timeValues;
	}

	public void setTimeValues(long[] timeValues) {
		this.timeValues = timeValues;
	}

	public double[] getValues() {
		return values;
	}

	public void setValues(double[] values) {
		this.values = values;
	}

	public long getId() {
		return id;
	}
}
