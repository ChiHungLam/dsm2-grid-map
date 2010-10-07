package gov.ca.modeling.maps.elevation.client;

import com.google.gwt.core.client.JavaScriptObject;

public class ArrayUtils {

	public static JavaScriptObject toJsArray(int[] array) {
		JavaScriptObject result = createArray();
		for (int element : array) {
			pushArray(result, element);
		}
		return result;
	};

	public static JavaScriptObject toJsArray(double[] array) {
		JavaScriptObject result = createArray();
		for (double element : array) {
			pushArray(result, element);
		}
		return result;
	};

	public static JavaScriptObject toJsArray(Object[] array) {
		JavaScriptObject result = createArray();
		for (Object element : array) {
			pushArray(result, element);
		}
		return result;
	};

	public native static JavaScriptObject createArray() /*-{
		return new Array();
	}-*/;

	public native static void pushArray(JavaScriptObject array, int i) /*-{
		array.push(i);
	}-*/;

	public native static void pushArray(JavaScriptObject array, double d) /*-{
		array.push(d);
	}-*/;

	public native static void pushArray(JavaScriptObject array, Object o) /*-{
		array.push(o);
	}-*/;

	public native static int lengthArray(JavaScriptObject array)/*-{
		return array.length;
	}-*/;

	public native static JavaScriptObject getElementAt(JavaScriptObject array,
			int i)/*-{
		return array[i];
	}-*/;
}
