package gov.ca.bdo.modeling.dsm2.map.client;

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
	/*-{return new $wnd.PolylineEncoder(numLevels, zoomFactor, verySmall, forceEndpoints);}-*/;

	/**
	 * Returns an object with encodedPoints, encodedLevels and
	 * encodedPointsLiteral
	 * 
	 * @param points
	 * @return
	 */
	public final native JavaScriptObject dpEncode(LatLng[] points)
	/*-{return this.dpEncode(points);}-*/;

	public final Polyline dpEncodeToGPolyline(LatLng[] points, String color,
			int weight, double opacity) {
		return Polyline.createPeer(impl_dpEncodeToGPolyline(LatLng
				.toJsArray(points), color, weight, opacity));
	}

	private final native JavaScriptObject impl_dpEncodeToGPolyline(
			JsArray<LatLng> points, String color, int weight, double opacity)
	/*-{return this.dpEncodeToGPolyline(points, color, weight, opacity);}-*/;

	public final native Polygon dpEncodeToGPolygon(LatLng[] points,
			String boundaryColor, int boundaryWeight, double boundaryOpacity,
			String fillColor, double fillOpacity, boolean fill, boolean outline)
	/*-{return this.dpEncodeToGPolygon(points,boundaryColor, boundaryWeight, boundaryOpacity, fillColor, fillOpacity, fill, outline);}-*/;

	public static class latLng extends JavaScriptObject {
		protected latLng() {
		};

		public final native static latLng newInstance(double y, double x)/*-{return $wnd.PolylineEncoder.latLng}-*/;
		/*-{}-*/
	}
}
