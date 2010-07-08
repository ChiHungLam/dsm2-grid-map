package gov.ca.modeling.calsim.report.shared;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class ChartsData extends JavaScriptObject {

	protected ChartsData() {

	}

	public static final native JsArray<ChartData> get() /*-{
		return $wnd.data;
	}-*/;
}
