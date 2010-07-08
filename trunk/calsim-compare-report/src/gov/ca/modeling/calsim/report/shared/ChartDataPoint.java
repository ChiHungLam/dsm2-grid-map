package gov.ca.modeling.calsim.report.shared;

import com.google.gwt.core.client.JavaScriptObject;

public class ChartDataPoint extends JavaScriptObject {
	protected ChartDataPoint() {

	}

	public final native double getX()/*-{
		return this.x;
	}-*/;

	public final native double getY1()/*-{
		return this.y1;
	}-*/;

	public final native double getY2()/*-{
		return this.y2;
	}-*/;
}
