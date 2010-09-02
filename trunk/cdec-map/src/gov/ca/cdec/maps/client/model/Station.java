package gov.ca.cdec.maps.client.model;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class Station extends JavaScriptObject {
	protected Station() {

	}

	public final native String getStationId()/*-{
		return this.stationId;
	}-*/;

	public final native String getDisplayName()/*-{
		return this.displayName;
	}-*/;

	public final native double getLatitude()/*-{
		return this.latitude;
	}-*/;

	public final native double getLongitude()/*-{
		return this.longitude;
	}-*/;

	public final native JsArray<Sensor> getSensors()/*-{
		return this.sensors;
	}-*/;
}
