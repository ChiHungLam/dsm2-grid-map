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
package gov.ca.bdo.modeling.dsm2.map.client;

import com.google.gwt.maps.client.Copyright;
import com.google.gwt.maps.client.CopyrightCollection;
import com.google.gwt.maps.client.MapType;
import com.google.gwt.maps.client.MapTypeOptions;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.TileLayer;
import com.google.gwt.maps.client.control.MapTypeControl;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.LatLngBounds;
import com.google.gwt.maps.client.geom.MercatorProjection;
import com.google.gwt.maps.client.geom.Point;

/**
 * Adds a clear background to see schematic without the distraction of map tiles
 * 
 * @author nsandhu
 * 
 */
public class ClearBackgroundLayer {

	public ClearBackgroundLayer(MapWidget map, boolean hybrid) {
		CopyrightCollection myCopyright = new CopyrightCollection("");
		myCopyright.addCopyright(new Copyright(1, LatLngBounds.newInstance(
				LatLng.newInstance(34, -81), LatLng.newInstance(36, -79)), 10,
				""));
		TileLayer tileLayerDimmedMap = null;
		if (hybrid) {
			MapType normalMap = MapType.getNormalMap();
			final TileLayer normalMapLayer = normalMap.getTileLayers()[0];
			tileLayerDimmedMap = new TileLayer(myCopyright, 10, 18) {

				@Override
				public double getOpacity() {
					return 0.66;
				}

				@Override
				public String getTileURL(Point tile, int zoomLevel) {
					return normalMapLayer.getTileURL(tile, zoomLevel);
				}

				@Override
				public boolean isPng() {
					return normalMapLayer.isPng();
				}

			};
		}
		TileLayer tileLayer = new TileLayer(myCopyright, 10, 18) {
			@Override
			public double getOpacity() {
				return 1.0;
			}

			@Override
			public String getTileURL(Point tile, int zoomLevel) {
				return "images/transparent.png";
			}

			@Override
			public boolean isPng() {
				return true;
			}
		};
		TileLayer[] layers = null;
		MapTypeOptions options = new MapTypeOptions();
		if (hybrid) {
			layers = new TileLayer[] { tileLayerDimmedMap, tileLayer };
			options.setShortName("Sch Hyb");
		} else {
			layers = new TileLayer[] { tileLayer };
			options.setShortName("Sch");
		}
		String name = "Schematic";
		if (hybrid) {
			name = "Sch. Hybrid";
		}

		MapType mapType = new MapType(layers, new MercatorProjection(20), name);
		map.addMapType(mapType);
		map.addControl(new MapTypeControl());
	}
}
