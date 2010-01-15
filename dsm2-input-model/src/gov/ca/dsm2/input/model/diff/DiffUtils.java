package gov.ca.dsm2.input.model.diff;

public class DiffUtils {
	public static final double epsilon = 1e-8;

	/**
	 * 
	 * @param value1
	 * @param value2
	 * @return
	 */
	public static boolean isApproxEqual(double value1, double value2) {
		return Math.abs(value1 - value2) < epsilon;
	}
}
