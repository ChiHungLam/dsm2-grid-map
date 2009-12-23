package gov.ca.bdo.modeling.dsm2.map.client.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author psandhu
 * 
 */
@SuppressWarnings("serial")
public class RegularTimeSeries implements Serializable {
	private String name;
	private Date startTime;
	private String interval;
	private double[] data;
	private String type; // flow or stage
	private String units;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public String getInterval() {
		return interval;
	}

	public void setInterval(String interval) {
		this.interval = interval;
	}

	public double[] getData() {
		return data;
	}

	public void setData(double[] data) {
		this.data = data;
	}

	/**
	 * regular time series
	 */
	public RegularTimeSeries() {
	}
}
