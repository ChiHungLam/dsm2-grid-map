package gov.ca.bdo.modeling.dsm2.map.client.map;

import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.geom.Size;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.maps.client.overlay.Overlay;
import com.google.gwt.maps.client.overlay.Polyline;

public class DirectionalPolyline extends Overlay {
	/**
	 * Base url to be appended with <degrees>.png where degrees is between 0 and
	 * 120
	 */
	public static final String DIR_PNG_BASE = "http://www.google.com/intl/en_ALL/mapfiles/dir_";
	private LatLng fromPoint;
	private LatLng toPoint;
	private MapWidget map;
	private Polyline polyline;
	private String name;
	private Marker arrowMarker;

	public DirectionalPolyline(String name, LatLng fromPoint, LatLng toPoint) {
		this.fromPoint = fromPoint;
		this.toPoint = toPoint;
		polyline = new Polyline(new LatLng[] { this.fromPoint, this.toPoint });
	}

	@Override
	protected Overlay copy() {
		return new DirectionalPolyline(name, fromPoint, toPoint);
	}

	@Override
	protected void initialize(MapWidget map) {
		this.map = map;
		map.addOverlay(polyline);
		midArrows(new LatLng[] { fromPoint, toPoint });
	}

	@Override
	protected void redraw(boolean force) {
		// Only set the rectangle's size if the map's size has changed
		if (!force) {
			return;
		}
	}

	@Override
	protected void remove() {
		map.removeOverlay(polyline);
		map.removeOverlay(arrowMarker);
	}

	// === Returns the bearing in degrees between two points. ===
	// North = 0, East = 90, South = 180, West = 270.
	public static final double DEGREES_PER_RADIAN = 180.0 / Math.PI;

	public double bearing(LatLng from, LatLng to) {
		// See T. Vincenty, Survey Review, 23, No 176, p 88-93,1975.
		// Convert to radians.
		double lat1 = from.getLatitudeRadians();
		double lon1 = from.getLongitudeRadians();
		double lat2 = to.getLatitudeRadians();
		double lon2 = to.getLongitudeRadians();

		// Compute the angle.
		double angle = -Math.atan2(Math.sin(lon1 - lon2) * Math.cos(lat2), Math
				.cos(lat1)
				* Math.sin(lat2)
				- Math.sin(lat1)
				* Math.cos(lat2)
				* Math.cos(lon1 - lon2));
		if (angle < 0.0) {
			angle += Math.PI * 2.0;
		}

		// And convert result to degrees.
		angle = angle * DEGREES_PER_RADIAN;
		return angle;
	}

	// === A function to put arrow heads at intermediate points
	protected void midArrows(LatLng[] points) {
		for (int i = 1; i < points.length; i++) {
			LatLng p1 = points[i - 1];
			LatLng p2 = points[i];
			LatLng midp = LatLng.newInstance((p1.getLatitude() + p2
					.getLatitude()) / 2,
					(p1.getLongitude() + p2.getLongitude()) / 2);
			double dir = bearing(p1, p2);
			// == round it to a multiple of 3 and cast out 120s
			dir = Math.round(dir / 3) * 3;
			while (dir >= 120) {
				dir -= 120;
			}
			// == use the corresponding triangle marker
			Icon arrowIcon = createArrowIcon((int) dir);
			MarkerOptions options = MarkerOptions.newInstance();
			options.setTitle(name);
			options.setIcon(arrowIcon);
			// -- edit mode options and only for the marker being
			// manipulated --
			options.setDragCrossMove(false);
			options.setDraggable(false);
			options.setClickable(false);
			options.setAutoPan(false);
			map.addOverlay(arrowMarker = new Marker(midp, options));
		}
	}

	protected Icon createArrowIcon(int dir) {
		// === The basis of the arrow icon information ===
		Icon arrowIcon = Icon.newInstance();
		arrowIcon.setIconSize(Size.newInstance(24, 24));
		arrowIcon.setShadowSize(Size.newInstance(1, 1));
		arrowIcon.setIconAnchor(Point.newInstance(12, 12));
		arrowIcon.setInfoWindowAnchor(Point.newInstance(0, 0));
		arrowIcon.setImageURL(DIR_PNG_BASE + dir + ".png");
		return arrowIcon;
	}

}
