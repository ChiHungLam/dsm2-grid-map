package gov.ca.modeling.timeseries.map.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface HeaderMessageEventHandler extends EventHandler {
  void onMessage(HeaderMessageEvent event);
}
