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
package gov.ca.bdo.modeling.dsm2.map.client.map;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Polygon;
import com.google.gwt.maps.client.overlay.Polyline;

public class PolylineEncoder extends JavaScriptObject {

	// Overlay types always have protected, zero-arg ctors
	protected PolylineEncoder() {
	}

	public final native static PolylineEncoder newInstance(int numLevels,
			int zoomFactor, double verySmall, boolean forceEndpoints)
	/*-{
		return new $wnd.PolylineEncoder(numLevels, zoomFactor, verySmall, forceEndpoints);
	}-*/;

	/**
	 * Returns an object with encodedPoints, encodedLevels and
	 * encodedPointsLiteral
	 * 
	 * @param points
	 * @return
	 */
	public final native JavaScriptObject dpEncode(LatLng[] points)
	/*-{
		return this.dpEncode(points);
	}-*/;

	public final Polyline dpEncodeToGPolyline(LatLng[] points, String color,
			int weight, double opacity) {
		return Polyline.createPeer(impl_dpEncodeToGPolyline(LatLng
				.toJsArray(points), color, weight, opacity));
	}

	private final native JavaScriptObject impl_dpEncodeToGPolyline(
			JsArray<LatLng> points, String color, int weight, double opacity)
	/*-{
		return this.dpEncodeToGPolyline(points, color, weight, opacity);
	}-*/;

	public final native Polygon dpEncodeToGPolygon(LatLng[] points,
			String boundaryColor, int boundaryWeight, double boundaryOpacity,
			String fillColor, double fillOpacity, boolean fill, boolean outline)
	/*-{
		return this.dpEncodeToGPolygon(points,boundaryColor, boundaryWeight, boundaryOpacity, fillColor, fillOpacity, fill, outline);
	}-*/;

	public static class latLng extends JavaScriptObject {
		protected latLng() {
		};

		public final native static latLng newInstance(double y, double x)/*-{
			return $wnd.PolylineEncoder.latLng
		}-*/;
		/*-{

		}-*/
	}
}
