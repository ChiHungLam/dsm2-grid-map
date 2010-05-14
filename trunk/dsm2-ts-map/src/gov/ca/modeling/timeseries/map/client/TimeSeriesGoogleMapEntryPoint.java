package gov.ca.modeling.timeseries.map.client;

import gov.ca.modeling.timeseries.map.shared.service.DataService;
import gov.ca.modeling.timeseries.map.shared.service.DataServiceAsync;
import gov.ca.modeling.timeseries.map.shared.service.LoginService;
import gov.ca.modeling.timeseries.map.shared.service.LoginServiceAsync;
import gov.ca.modeling.timeseries.map.shared.service.UserProfileService;
import gov.ca.modeling.timeseries.map.shared.service.UserProfileServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class TimeSeriesGoogleMapEntryPoint implements EntryPoint {
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		DataServiceAsync dataService = GWT.create(DataService.class);
		LoginServiceAsync loginService = GWT.create(LoginService.class);
		UserProfileServiceAsync userProfileService = GWT
				.create(UserProfileService.class);
		HandlerManager eventBus = new HandlerManager(null);
		AppController appViewer = new AppController(loginService, dataService,
				userProfileService, eventBus);
		appViewer.go(RootLayoutPanel.get());
	}
}
