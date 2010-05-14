package gov.ca.modeling.timeseries.map.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class HeaderMessageEvent extends GwtEvent<HeaderMessageEventHandler> {
	public static Type<HeaderMessageEventHandler> TYPE = new Type<HeaderMessageEventHandler>();
	private String message;
	private boolean isError;
	private int duration;

	@Override
	public Type<HeaderMessageEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(HeaderMessageEventHandler handler) {
		handler.onMessage(this);
	}

	public HeaderMessageEvent(String message) {
		this.message = message;
		isError = false;
		duration = 0;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setError(boolean isError) {
		this.isError = isError;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public boolean isError() {
		return isError;
	}

	public String getMessage() {
		return message;
	}

	public int getDuration() {
		return duration;
	}
}
