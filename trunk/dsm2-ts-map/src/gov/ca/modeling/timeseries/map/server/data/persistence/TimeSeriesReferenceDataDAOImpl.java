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
package gov.ca.modeling.timeseries.map.server.data.persistence;

import gov.ca.modeling.server.utils.GenericDAOImpl;
import gov.ca.modeling.timeseries.map.shared.data.TimeSeriesReferenceData;

import java.util.List;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;

public class TimeSeriesReferenceDataDAOImpl extends
		GenericDAOImpl<TimeSeriesReferenceData> implements
		TimeSeriesReferenceDataDAO {

	static {
		ObjectifyService.register(TimeSeriesReferenceData.class);
	}

	@Override
	public List<TimeSeriesReferenceData> findBySourceAndLocationAndType(
			String source, String location, String type) {
		Query<TimeSeriesReferenceData> query = ObjectifyService.begin().query(
				TimeSeriesReferenceData.class);
		if (source != null) {
			query.filter("source =", source);
		}
		if (location != null) {
			query.filter("location = ", location);
		}
		if (type != null) {
			query.filter("type = ", type);
		}
		return query.list();
	}
}
