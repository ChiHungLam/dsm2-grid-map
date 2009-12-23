package gov.ca.bdo.modeling.dsm2.map.client;

import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.event.PolylineCancelLineHandler;
import com.google.gwt.maps.client.event.PolylineClickHandler;
import com.google.gwt.maps.client.event.PolylineEndLineHandler;
import com.google.gwt.maps.client.event.PolylineLineUpdatedHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.PolyEditingOptions;
import com.google.gwt.maps.client.overlay.PolyStyleOptions;
import com.google.gwt.maps.client.overlay.Polyline;
import com.google.gwt.user.client.ui.Label;

/**
 * Creates a polyline and displays distance as polyline is drawn
 * 
 * @author psandhu
 * 
 */
public class MeasuringDistanceAlongLine {
	private MapWidget map;
	private Polyline line;
	private String color = "#FF0000";
	private int weight = 5;
	private double opacity = 0.75;
	private Label messageLabel;

	public MeasuringDistanceAlongLine(MapWidget map, Label messageLabel) {
		this.map = map;
		this.messageLabel = messageLabel;
	}

	public void addPolyline() {
		PolyStyleOptions style = PolyStyleOptions.newInstance(color, weight,
				opacity);
		line = new Polyline(new LatLng[0]);
		map.addOverlay(line);
		line.setDrawingEnabled();
		line.setStrokeStyle(style);
		messageLabel.setText("");
		line.addPolylineClickHandler(new PolylineClickHandler() {

			public void onClick(PolylineClickEvent event) {
				editPolyline();
			}
		});
		line.addPolylineLineUpdatedHandler(new PolylineLineUpdatedHandler() {

			public void onUpdate(PolylineLineUpdatedEvent event) {
				messageLabel.setText("Length : " + getLengthInFeet() + " ft");
			}
		});

		line.addPolylineCancelLineHandler(new PolylineCancelLineHandler() {

			public void onCancel(PolylineCancelLineEvent event) {
				messageLabel.setText("Length : " + getLengthInFeet() + " ft");
			}
		});

		line.addPolylineEndLineHandler(new PolylineEndLineHandler() {

			public void onEnd(PolylineEndLineEvent event) {
				messageLabel.setText("Length : " + getLengthInFeet() + " ft");
			}
		});
	}

	public double getLengthInFeet() {
		return Math.round(line.getLength() * 3.2808399 * 100) / 100;
	}

	public void editPolyline() {
		if (line == null) {
			return;
		}
		// allow up to 10 vertices to exist in the line.
		line.setEditingEnabled(PolyEditingOptions.newInstance(10));
	}

	public void clearOverlay() {
		if (line != null) {
			map.removeOverlay(line);
		}
	}
}
