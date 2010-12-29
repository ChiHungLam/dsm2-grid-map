package gov.ca.bdo.modeling.dsm2.map.client.display;

import com.google.gwt.core.client.GWT;
import com.google.gwt.maps.utility.client.GoogleMapsUtilityPackage;

/**
 * Represents the local Google Maps utility packages.
 */
public enum MapsUtilityLocalPackage implements GoogleMapsUtilityPackage {

	CONTEXT_MENU_CONTROL("1.0", "contextmenucontrol_packed.js",
			"ContextMenuControl"), LABELED_MARKER("1.4",
			"labeledmarker_packed.js", "LabeledMarker"), MAP_ICON_MAKER("1.1",
			"mapiconmaker_packed.js", "MapIconMaker"), MARKER_CLUSTERER("1.0",
			"markerclusterer_packed.js", "MarkerClusterer"), MARKER_MANAGER(
			"1.1", "markermanager_packed.js", "MarkerManager"), MARKER_TRACKER(
			"1.0", "markertracker_packed.js", "MarkerTracker"), POPUP_MARKER(
			"1.1", "popupmarker_packed.js", "PopupMarker"), PROGRESS_BAR_CONTROL(
			"1.0", "progressbarcontrol_packed.js", "ProgressbarControl"), SNAPSHOT_CONTROL(
			"1.0", "snapshotcontrol_packed.js", "SnapShotControl"), SNAP_TO_ROUTE(
			"1.0", "snaptoroute_packed.js", "SnapToRoute");

	private String version, source, indicator;

	private MapsUtilityLocalPackage(String version, String source,
			String indicator) {
		this.version = version;
		this.source = source;
		this.indicator = indicator;
	}

	/**
	 * Retrieves the top-level object that indicates the availability of the
	 * package.
	 * 
	 * @return The package indicator.
	 */
	public String getIndicator() {
		return indicator;
	}

	/**
	 * Retrieves the script source url for the package.
	 * 
	 * @return The package's script source.
	 */
	public String getSource() {
		return GWT.getModuleBaseURL() + source;
	}

	/**
	 * Retrieves the version of the package.
	 * 
	 * @return The package's version.
	 */
	public String getVersion() {
		return version;
	}

}
