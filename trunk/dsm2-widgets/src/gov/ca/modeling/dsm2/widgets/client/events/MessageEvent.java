package gov.ca.modeling.dsm2.widgets.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class MessageEvent extends GwtEvent<MessageEventHandler> {
	public static Type<MessageEventHandler> TYPE = new Type<MessageEventHandler>();

	public static int WARNING = 100;
	public static int ERROR = 200;

	private String message;

	private int type;

	private int delayInMillis;

	public MessageEvent(String message) {
		this(message, WARNING, 0);
	}

	public MessageEvent(String message, int type) {
		this(message, type, 0);
	}

	public MessageEvent(String message, int type, int delayInMillis) {
		this.message = message;
		this.type = type;
		this.delayInMillis = delayInMillis;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getDelayInMillis() {
		return delayInMillis;
	}

	public void setDelayInMillis(int delayInMillis) {
		this.delayInMillis = delayInMillis;
	}

	@Override
	protected void dispatch(MessageEventHandler handler) {
		handler.onMessage(this);
	}

	@Override
	public Type<MessageEventHandler> getAssociatedType() {
		return TYPE;
	}

}
