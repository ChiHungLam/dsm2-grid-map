package gov.ca.bdo.modeling.dsm2.map.client.display;

public class ColorRangeMapper {
	public static final String[] DIVERGING_COLORS = new String[] { "#5e3c99",
			"#b2abd2", "#ff99ff", "#fdb863", "#e66101" };

	public static final String[] QUALITATIVE_COLORS = new String[] { "#6600cc",
			"#0000ff", "#006633", "#ff6600", "#ff3399" };

	public static final String[] SEQUENTIAL_COLORS = new String[] { "#fee5d9",
			"#fcae91", "#fb6a4a", "#de2d26", "#a50f15" };

	private String[] colors;

	public ColorRangeMapper(String[] colors) {
		this.colors = colors;
	}

	public String convertValueToColor(double value, double min, double max) {
		int index = 0;
		if (value <= min) {
			index = 0;
		} else if (value >= max) {
			index = colors.length - 1;
		} else {
			index = (int) Math.floor((value - min) / (max - min)
					* colors.length);
		}
		return colors[index];
	}

}
