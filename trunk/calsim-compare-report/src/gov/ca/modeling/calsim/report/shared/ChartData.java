package gov.ca.modeling.calsim.report.shared;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;

public class ChartData extends JavaScriptObject {

	public static final String TIME_SERIES = "timeseries";
	public static final String EXCEEDANCE = "exceedance";

	protected ChartData() {
	}

	public final native String getTitle() /*-{
		return this.title;
	}-*/;

	public final native String getPlotType() /*-{
		return this.plot_type;
	}-*/;

	public final native JsArrayString getSeriesNames() /*-{
		return this.series_names;
	}-*/;

	public final native JsArray<ChartDataPoint> getValues()/*-{
		return this.values;
	}-*/;

	public final native String getXAxisName() /*-{
		return this.xaxis_name;
	}-*/;

	public final native String getYAxisName() /*-{
		return this.yaxis_name;
	}-*/;
}
