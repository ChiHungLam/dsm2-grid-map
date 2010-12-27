package gov.ca.modeling.dsm2.widgets.client.events;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasMessageEvents extends HasHandlers{
	public HandlerRegistration addMessageEventHandler(MessageEventHandler handler);
}
