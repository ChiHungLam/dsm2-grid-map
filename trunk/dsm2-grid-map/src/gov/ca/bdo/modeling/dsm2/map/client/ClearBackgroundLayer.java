package gov.ca.bdo.modeling.dsm2.map.client;

import com.google.gwt.maps.client.Copyright;
import com.google.gwt.maps.client.CopyrightCollection;
import com.google.gwt.maps.client.MapType;
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

	public ClearBackgroundLayer(MapWidget map) {
		CopyrightCollection myCopyright = new CopyrightCollection("");
		myCopyright.addCopyright(new Copyright(1, LatLngBounds.newInstance(
				LatLng.newInstance(34, -81), LatLng.newInstance(36, -79)), 10,
				""));
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

		MapType mapType = new MapType(new TileLayer[] { tileLayer },
				new MercatorProjection(20), "Schematic");
		map.addMapType(mapType);
		map.addControl(new MapTypeControl());

	}

}
