package gov.ca.dsm2.input.model;

import java.util.List;

public class TableUtil {
	public static String buildLatLng(double latitude, double longitude) {
		StringBuilder builder = new StringBuilder();
		builder.append("(").append(latitude).append(",").append(longitude)
				.append(")");
		return builder.toString();
	}

	public static String buildInteriorLatLngPoints(List<double[]> latlngPoints) {
		StringBuilder builder = new StringBuilder();
		for (double[] ds : latlngPoints) {
			builder.append("(").append(ds[0]).append(",").append(ds[1]).append(
					")");
			builder.append("|");
		}
		if (builder.length() >= 1) {
			builder.deleteCharAt(builder.length() - 1);
		}
		return builder.toString();
	}
}
