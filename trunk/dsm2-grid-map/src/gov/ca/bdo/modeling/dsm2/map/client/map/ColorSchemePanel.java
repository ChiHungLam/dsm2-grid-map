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
		Grid legend = new Grid(colors.length, 2);
		legend.setStyleName("legend");
		int ncolors = colors.length;
		double step = (max - min) / (ncolors - 2);
		setGridAtIndex(legend, 0, colors[0], " < " + formatter.format(min));
		for (int i = 1; i < ncolors - 1; i++) {
			double value = i * step + min;
			setGridAtIndex(legend, i, colors[i], formatter.format(value - step)
					+ " - " + formatter.format(value));
		}
		setGridAtIndex(legend, ncolors - 1, colors[ncolors - 1], " > "
				+ formatter.format(max));
		return legend;
	}

	private void setGridAtIndex(Grid legend, int i, String color, String value) {
		legend.setHTML(i, 0, "&nbsp;");
		legend.getCellFormatter().setWidth(i, 0, "15px");
		legend.getCellFormatter().getElement(i, 0).getStyle()
				.setBackgroundColor(color);
		legend.setHTML(i, 1, value);

	}

}
