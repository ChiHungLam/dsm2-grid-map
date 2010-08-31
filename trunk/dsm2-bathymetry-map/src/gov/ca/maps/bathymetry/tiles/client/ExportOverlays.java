package gov.ca.maps.bathymetry.tiles.client;

import java.util.ArrayList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.maps.client.Copyright;
import com.google.gwt.maps.client.CopyrightCollection;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.maps.client.TileLayer;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.LatLngBounds;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;

public class ExportOverlays implements EntryPoint {

	public static final class BathymetryTileLayer extends TileLayer {
		private final String prefix;
		private double opacity = 0.6;

		public BathymetryTileLayer(CopyrightCollection copyrights,
				int minResolution, int maxResolution, String prefix) {
			super(copyrights, minResolution, maxResolution);
			this.prefix = prefix;
		}

		public double getOpacity() {
			return opacity;
		}

		public void setOpacity(double opacity) {
			this.opacity = opacity;
		}

		public String getTileURL(Point tile, int zoomLevel) {
			String uniqueValue = tile.getX() + "" + tile.getY() + ""
					+ zoomLevel;
			int hashCode = uniqueValue.hashCode();
			if (GWT.getHostPageBaseURL().contains("localhost")) {
				return "/tiles/" + hashCode + "_" + prefix + "tile"
						+ tile.getX() + "_" + tile.getY() + "_" + zoomLevel
						+ ".png";
			} else {
				int version = (tile.getX() + tile.getY()) % 4 + 1;
				return "http://" + version
						+ ".latest.dsm2bathymetry.appspot.com/tiles/"
						+ hashCode + "_" + prefix + "tile" + tile.getX() + "_"
						+ tile.getY() + "_" + zoomLevel + ".png";
			}
		}

		public boolean isPng() {
			return true;
		}
	}

	public void onModuleLoad() {
		if (!Maps.isLoaded()) {
			Window
					.alert("The Maps API is not installed."
							+ "  The <script> tag that loads the Maps API may be missing or your Maps key may be wrong.");
			return;
		}

		if (!Maps.isBrowserCompatible()) {
			Window.alert("The Maps API is not compatible with this browser.");
			return;
		}
		Runnable mapLoadCallback = new Runnable() {

			public void run() {
				exportMethods();
			}
		};
		mapLoadCallback.run();
	}

	public static native void exportMethods()/*-{
		$wnd.getBathymetryTileLayer = function(prefix){
		return @gov.ca.maps.bathymetry.tiles.client.ExportOverlays::getBathymetryTileLayer(Ljava/lang/String;);
		}
		$wnd.getNOAATileLayer = function(){
		return @gov.ca.maps.bathymetry.tiles.client.ExportOverlays::getNOAATileLayer();
		}
		$wnd.getLegendPanel = function() {
		return @gov.ca.maps.bathymetry.tiles.client.ExportOverlays::getLegendPanelElement();
		}
	}-*/;

	public static BathymetryTileLayer getBathymetryTileLayer(final String prefix) {
		CopyrightCollection myCopyright = new CopyrightCollection(
				"@ California DWR 2010");
		LatLng southWest = LatLng.newInstance(36.5, -123.0);
		LatLng northEast = LatLng.newInstance(39.5, -120.5);
		myCopyright.addCopyright(new Copyright(1, LatLngBounds.newInstance(
				southWest, northEast), 10, "@ Copyright California DWR"));
		BathymetryTileLayer tileLayer = new BathymetryTileLayer(myCopyright,
				10, 17, prefix);
		return tileLayer;
	}

	public static TileLayer getNOAATileLayer() {
		CopyrightCollection myCopyright = new CopyrightCollection(
				"@ California DWR 2010");
		LatLng southWest = LatLng.newInstance(36.5, -123.0);
		LatLng northEast = LatLng.newInstance(39.5, -120.5);
		myCopyright.addCopyright(new Copyright(1, LatLngBounds.newInstance(
				southWest, northEast), 10, "@ Copyright California DWR"));
		TileLayer tileLayer = new TileLayer(myCopyright, 10, 17) {

			public double getOpacity() {
				return 0.6;
			}

			public String getTileURL(Point tile, int zoomLevel) {
				String genQrst = genQrst(tile.getX(), tile.getY(), zoomLevel);
				String path = genPathFromQrst(genQrst);
				return "http://tiles5.geogarage.com/noaa/" + path;
			}

			public boolean isPng() {
				return true;
			}
		};
		return tileLayer;
	}

	/**
	 * Generate a sequence of QRST that match the tile requested by the
	 * parameters. The parameters are in the google maps tile numbering style,
	 * that differs from the geogarage tile numbering.
	 * 
	 * @param {Number} x tile coordinate (unit is the tile)
	 * @param {Number} y tile coordinate (unit is the tile)
	 * @param {Number} zoom zoom level
	 * @returns The qrst that identifies that tile
	 * @type String
	 */

	public static String genQrst(int x, int y, int zoom) {
		int inc = 1 << zoom - 1;
		String qrstString = "t";
		for (int i = zoom; i > 0; i--) {
			boolean xbitIs1 = (x & inc) == inc;
			boolean ybitIs1 = (y & inc) == inc;
			if (xbitIs1 && ybitIs1) {
				qrstString += "s";
			} else if (xbitIs1 && !ybitIs1) {
				qrstString += "r";
			} else if (!xbitIs1 && !ybitIs1) {
				qrstString += "q";
			} else if (!xbitIs1 && ybitIs1) {
				qrstString += "t";
			}
			inc = inc >> 1;
		}
		return qrstString;
	}

	/**
	 * Generate a path where a file corresponding to the string should located,
	 * in GeoGarage quadtree storage structure.
	 * 
	 * @param {String} qrstString the qrst string corresponding to a file.
	 * @returns The corresponding directory path
	 * @type String
	 */
	public static String genPathFromQrst(String qrstString) {
		ArrayList<String> directoryElem = new ArrayList<String>();
		int strLength = qrstString.length();
		for (int i = 0; (i + 1) * 6 <= strLength; i++) {
			directoryElem.add(qrstString.substring(i * 6, i * 6 + 6));
		}
		String result = "";
		for (int i = 0; i < directoryElem.size(); i++) {
			result += directoryElem.get(i) + "/";
		}
		return result + qrstString + ".png";
	}

	public static Panel getLegendPanel() {
		Grid legend = new Grid(13, 2);
		legend.setStyleName("legend");
		/*
		 * These are SVG colors and so IE8 and below don't understand them :(
		 * String[] legendColors = new String[] { "black", "blue", "cyan",
		 * "green", "yellow", "orange", "red", "pink", "magenta", "darkviolet",
		 * "gray", "lightGray", "white" };
		 */
		String[] legendColors = new String[] { "#000000", "#0000ff", "#00ffff",
				"#008000", "#ffff00", "#ffa500", "#ff0000", "#ffc0cb",
				"#ff00ff", "#9400d3", "#808080", "#d3d3d3", "#ffffff" };
		String[] legendDepth = new String[] { "< -100", "-40", "-30", "-15",
				"-10", " -5", "  0", " +5", "+10", "+15", "+30", "+40",
				" > +100" };
		for (int i = 0; i < legendColors.length; i++) {
			int gridIndex = legendColors.length - i - 1;
			legend.setHTML(gridIndex, 0, "&nbsp;");
			legend.getCellFormatter().setWidth(gridIndex, 0, "15px");
			legend.getCellFormatter().getElement(gridIndex, 0).getStyle().setBackgroundColor(legendColors[i]);
			legend.setHTML(gridIndex, 1, legendDepth[i]);
		}
		FlowPanel legendContainerPanel = new FlowPanel();
		legendContainerPanel.add(new HTML("Depth<br/>[NGVD29]"));
		legendContainerPanel.add(legend);
		return legendContainerPanel;
	}

	public static Element getLegendPanelElement() {
		return getLegendPanel().getElement();
	}

}
