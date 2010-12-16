package gov.ca.bdo.modeling.dsm2.map.client.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasMessageEvents extends HasHandlers{
	public HandlerRegistration addMessageEventHandler(MessageEventHandler handler);
}
