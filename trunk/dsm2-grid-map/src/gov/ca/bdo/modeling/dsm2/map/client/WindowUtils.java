package gov.ca.bdo.modeling.dsm2.map.client;

public class WindowUtils {
	/**
	 * Changes the cursor to desired shape e.g. "hand", "pointer"...
	 * 
	 * @param cursor
	 */
	public static native void changeCursor(String cursor)/*-{
		Window.cursor=cursor;
	}-*/;

}
