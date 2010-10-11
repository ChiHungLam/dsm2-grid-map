/**
 *    Copyright (C) 2009, 2010 
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
 *    GNU General Public License for more details. [http://www.gnu.org/licenses]
 *    
 *    @author Nicky Sandhu
 *    
 */
package gov.ca.maps.bathymetry.tiles.client;

import gov.ca.maps.bathymetry.tiles.client.ExportOverlays.BathymetryTileLayer;
import gov.ca.modeling.maps.widgets.client.ExpandContractMapControl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.maps.client.MapType;
import com.google.gwt.maps.client.MapUIOptions;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.TileLayer;
import com.google.gwt.maps.client.control.OverviewMapControl;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Overlay;
import com.google.gwt.maps.client.overlay.TileLayerOverlay;
import com.google.gwt.user.client.ui.Composite;

public class MapPanel extends Composite {
	private final MapWidget map;

	public MapWidget getMap() {
		return map;
	}

	private TileLayerOverlay noaaLayer;
	private Overlay currentOverlay;
	private double opacity = 0.6;
	private BathymetryTileLayer currentTileLayer;

	public MapPanel(double latCenter, double lngCenter, int zoom) {
		map = new MapWidget(LatLng.newInstance(latCenter, lngCenter), zoom);

		ExpandContractMapControl fullScreenControl = new ExpandContractMapControl();
		map.addControl(fullScreenControl);

		setOptions();
		addOverlay("");
		initWidget(map);
	}

	public void addNOAAOverlay() {
		if (noaaLayer == null) {
			TileLayer tileLayer = ExportOverlays.getNOAATileLayer();
			noaaLayer = new TileLayerOverlay(tileLayer);
		}
		map.addOverlay(noaaLayer);
	}

	public void removeNOAAOverlay() {
		if (noaaLayer != null) {
			map.removeOverlay(noaaLayer);
		}
	}

	public void removeOverlays() {
		if (currentOverlay != null) {
			map.removeOverlay(currentOverlay);
		}
	}

	public void addOverlay(String prefix) {
		BathymetryTileLayer tileLayer = ExportOverlays
				.getBathymetryTileLayer(prefix);
		currentTileLayer = tileLayer;
		currentTileLayer.setOpacity(getLayerOpacity());
		currentOverlay = new TileLayerOverlay(tileLayer);
		map.addOverlay(currentOverlay);
	}

	private void setOptions() {
		MapUIOptions options = MapUIOptions.newInstance(map.getSize());
		options.setDoubleClick(true);
		options.setKeyboard(true);
		options.setMapTypeControl(false);
		options.setMenuMapTypeControl(true);
		options.setScaleControl(true);
		// options.setScrollwheel(true);
		options.setLargeMapControl3d(true);
		map.setUI(options);
		//
		map.addMapType(getTopoMapType());
		map.addMapType(new MapType(new TileLayer[] { ExportOverlays
				.getNOAATileLayer() }, MapType.getNormalMap().getProjection(),
				"NOAA"));
		OverviewMapControl control = new OverviewMapControl();
		map.addControl(control);
	}

	private final native MapType getTopoMapType()/*-{
		var layer = new $wnd.USGSTopoTileLayer("http://orthoimage.er.usgs.gov/ogcmap.ashx?", "USGS Topo Maps", "Topo","DRG","EPSG:4326","1.1.1","","image/png",null,"0xFFFFFF");
		var o = new $wnd.GMapType([layer], $wnd.G_NORMAL_MAP.getProjection(), "Topo");
		return @com.google.gwt.maps.client.MapType::createPeer(Lcom/google/gwt/core/client/JavaScriptObject;)(o);
	}-*/;

	public void onResize() {
		map.checkResizeAndCenter();
	}

	public void setLayerOpacity(double opacity) {
		this.opacity = opacity;
		if (currentTileLayer != null) {
			currentTileLayer.setOpacity(opacity);
			if (currentOverlay != null) {
				map.removeOverlay(currentOverlay);
				map.addOverlay(currentOverlay);
			}
		}
	}

	public double getLayerOpacity() {
		return opacity;
	}

	public String generateLinkURL() {
		return GWT.getHostPageBaseURL() + "?" + "lat="
				+ map.getCenter().getLatitude() + "&lng="
				+ map.getCenter().getLongitude() + "&z=" + map.getZoomLevel();
	}

}
