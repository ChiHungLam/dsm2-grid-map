package gov.ca.cdec.maps.client.model;

import com.google.gwt.core.client.JavaScriptObject;

public class Sensor extends JavaScriptObject{
	protected Sensor(){
		
	}
	public final native String getDescription()/*-{
		return this.description;
	}-*/;
}
