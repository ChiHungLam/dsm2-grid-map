package gov.ca.modeling.timeseries.map.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface DataChangedEventHandler extends EventHandler {
	void onDataChange(DataChangedEvent event);
}
