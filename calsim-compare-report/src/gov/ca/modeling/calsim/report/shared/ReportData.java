package gov.ca.modeling.calsim.report.shared;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayNumber;

public class ReportData extends JavaScriptObject {

	protected ReportData() {

	}

	public static final native ReportData buildReportData(String json)/*-{return eval('('+json+')');}-*/;

	public final native String getReportName()/*-{return this.reportName;}-*/;

	public final native String getBaseStudyName()/*-{return this.baseStudy}-*/;

	public final native String getAlternateStudyName()/*-{return this.altStudy}-*/;

	public final native JsArray<ChartData> getCharts()/*-{return this.charts}-*/;

	/*-{}-*/
	/**
	 * returns array of [width, height] in pixels to be used for the chart
	 */
	public final native JsArrayNumber getChartDimensions()/*-{return this.chartDimensions}-*/;
}
