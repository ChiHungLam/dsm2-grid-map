package gov.ca.bdo.modeling.dsm2.map.client.map;

import gov.ca.bdo.modeling.dsm2.map.client.presenter.ColorSchemePresenter.Display;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.maps.client.overlay.PolyStyleOptions;
import com.google.gwt.maps.client.overlay.Polyline;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class ColorSchemePanel extends Composite implements Display {
	public static String CHANNEL_COLOR_PLAIN = "Plain";
	public static String CHANNEL_COLOR_MANNINGS = "Mannings";
	public static String CHANNEL_COLOR_DISPERSION = "Dispersion";

	public static final String[] divergingColorsArray = new String[] {
			"#5e3c99", "#b2abd2", "#ff99ff", "#fdb863", "#e66101" };
	public static final String[] qualitativeColorsArray = new String[] {
			"#6600cc", "#0000ff", "#006633", "#ff6600", "#ff3399" };
	public static final String[] sequentialColorsArray = new String[] {
			"#fee5d9", "#fcae91", "#fb6a4a", "#de2d26", "#a50f15" };
	private static NumberFormat formatter = NumberFormat.getFormat("0.000");

	private ChannelLineDataManager channelManager;
	private int scheme;
	private double max;
	private double min;

	public ColorSchemePanel(ChannelLineDataManager channelManager) {
		this.channelManager = channelManager;
	}

	private String getColorFor(double value) {
		String[] colorsArray = getColorArray();
		int ncolors = colorsArray.length;
		int colorIndex = 0;
		if (value < min) {
			colorIndex = 0;
		} else if (value > max) {
			colorIndex = ncolors - 1;
		} else {
			double colorSlope = (ncolors - 2) / (max - min + 1e-6);
			colorIndex = (int) Math.floor((value - min) * colorSlope) + 1;
		}
		return colorsArray[colorIndex];
	}

	public String[] getColorArray() {
		String[] colorsArray = sequentialColorsArray;
		if (scheme == Display.SCHEME_SEQUENTIAL) {
			colorsArray = sequentialColorsArray;
		} else if (scheme == Display.SCHEME_DIVERGING) {
			colorsArray = divergingColorsArray;
		} else if (scheme == Display.SCHEME_QUALITATIVE) {
			colorsArray = qualitativeColorsArray;
		}
		return colorsArray;
	}

	public Panel getColorArraySchemePanel() {
		String[] colorsArray = getColorArray();
		int ncolors = colorsArray.length;
		Grid panel = new Grid(ncolors, 2);
		double step = (max - min) / (ncolors - 2);
		String html = "<p style=\"background-color: "
				+ colorsArray[0]
				+ ";\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p>";
		panel.setHTML(0, 0, " < " + min);
		panel.setHTML(0, 1, html);
		for (int i = 1; i < ncolors - 1; i++) {
			double value = i * step + min;
			panel.setHTML(i, 0, formatter.format(value - step) + " - "
					+ formatter.format(value));
			html = "<p style=\"background-color: " + colorsArray[i]
					+ ";\">&nbsp;&nbsp;</p>";
			panel.setHTML(i, 1, html);
		}
		html = "<p style=\"background-color: " + colorsArray[ncolors - 1]
				+ ";\">&nbsp;&nbsp;</p>";
		panel.setHTML(ncolors - 1, 0, " > " + max);
		panel.setHTML(ncolors - 1, 1, html);
		return panel;
	}

	public Widget asWidget() {
		return this;
	}

	public void setChannelValue(String id, double value) {
		Polyline line = channelManager.getPolyline(id);
		String lineColor = getColorFor(value);
		PolyStyleOptions style = PolyStyleOptions.newInstance(lineColor);
		style.setOpacity(0.75);
		style.setWeight(3);
		line.setStrokeStyle(style);
	}

	public void setColorScheme(int scheme) {
		this.scheme = scheme;
	}

	public void setValueRange(double max, double min) {
		this.max = max;
		this.min = min;
	}

}
