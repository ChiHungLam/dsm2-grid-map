package gov.ca.bdo.modeling.dsm2.map.client.model;

import com.google.gwt.core.client.JavaScriptObject;

public class XYZPoint extends JavaScriptObject {
	protected XYZPoint() {

	}

	public static native XYZPoint create(double x0, double y0, double z0)/*-{
		this.x = x0;
		this.y = y0;
		this.z = z0;
	}-*/;

	public final native double getX()/*-{
		return this.x;
	}-*/;

	public final native double getY()/*-{
		return this.y;
	}-*/;

	public final native double getZ()/*-{
		return this.z;
	}-*/;

	public final native void setX(double value)/*-{
		this.x = value;
	}-*/;

	public final native void setY(double value)/*-{
		this.y=value;
	}-*/;

	public final native void setZ(double value)/*-{
		this.z=value;
	}-*/;
}