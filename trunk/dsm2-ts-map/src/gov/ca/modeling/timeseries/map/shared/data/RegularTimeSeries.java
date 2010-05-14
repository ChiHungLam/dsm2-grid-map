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
package gov.ca.modeling.timeseries.map.shared.data;

import java.io.Serializable;
import java.util.Date;

/**
 * A regular time series consists of a named series which is unique to the owner
 * of the time series.
 * 
 * @author psandhu
 * 
 */
@SuppressWarnings("serial")
public class RegularTimeSeries implements Serializable {
	/**
	 * This is unique to this owner.
	 */
	private String name;
	private Date startTime;
	private String interval;
	private double[] data;
	private String type; // flow or stage
	private String units;
	private String ownerName;

	/**
	 * regular time series
	 */
	public RegularTimeSeries() {
	}

	public double[] getData() {
		return data;
	}

	public String getInterval() {
		return interval;
	}

	public String getName() {
		return name;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public Date getStartTime() {
		return startTime;
	}

	public String getType() {
		return type;
	}

	public String getUnits() {
		return units;
	}

	public void setData(double[] data) {
		this.data = data;
	}

	public void setInterval(String interval) {
		this.interval = interval;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setUnits(String units) {
		this.units = units;
	}
}
