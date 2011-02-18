package gov.ca.bdo.modeling.dsm2.map.client.map;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Panel;

public class ColorSchemePanel extends Composite {
	private String[] colors;
	private double max;
	private double min;
	private static NumberFormat formatter = NumberFormat.getFormat("0.000");

	public ColorSchemePanel(String[] colors, double max, double min) {
		this.colors = colors;
		this.max = max;
		this.min = min;
		initWidget(getColorArraySchemePanel());
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

	private String[] getColorArray() {
		return colors;
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

}
