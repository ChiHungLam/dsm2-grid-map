package gov.ca.bdo.modeling.dsm2.map.client.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasDSM2StudyEvents extends HasHandlers{
	public HandlerRegistration addDSM2StudyEventHandler(DSM2StudyEventHandler handler);
}
