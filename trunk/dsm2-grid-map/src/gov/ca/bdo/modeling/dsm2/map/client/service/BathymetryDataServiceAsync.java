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
package gov.ca.bdo.modeling.dsm2.map.client.service;

import gov.ca.bdo.modeling.dsm2.map.client.model.BathymetryDataPoint;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface BathymetryDataServiceAsync {

	void getBathymetryDataPoints(double latitude, double longitude,
			AsyncCallback<List<BathymetryDataPoint>> callback);

	void getBathymetryDataPoints(double x1, double y1, double x2, double y2,
			AsyncCallback<List<BathymetryDataPoint>> callback);
}
