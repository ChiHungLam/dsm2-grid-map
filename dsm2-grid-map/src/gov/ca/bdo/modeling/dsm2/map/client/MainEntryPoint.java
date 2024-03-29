/**
 *   Copyright (C) 2009, 2010 
 *    Nicky Sandhu
 *    State of California,
 *    Department of Water Resources.
 *    This file is part of DSM2 Grid Map
 *    The DSM2 Grid Map is free software: 
 *    you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *    DSM2 Grid Map is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.

 *    You should have received a copy of the GNU General Public License
 *    along with DSM2 Grid Map.  If not, see <http://www.gnu.org/licenses>.
 */
package gov.ca.bdo.modeling.dsm2.map.client;

import gov.ca.bdo.modeling.dsm2.map.client.service.DSM2InputService;
import gov.ca.bdo.modeling.dsm2.map.client.service.DSM2InputServiceAsync;
import gov.ca.bdo.modeling.dsm2.map.client.service.LoginService;
import gov.ca.bdo.modeling.dsm2.map.client.service.LoginServiceAsync;
import gov.ca.bdo.modeling.dsm2.map.client.service.UserProfileService;
import gov.ca.bdo.modeling.dsm2.map.client.service.UserProfileServiceAsync;
import gov.ca.modeling.maps.elevation.client.service.BathymetryDataService;
import gov.ca.modeling.maps.elevation.client.service.BathymetryDataServiceAsync;
import gov.ca.modeling.maps.elevation.client.service.DEMDataService;
import gov.ca.modeling.maps.elevation.client.service.DEMDataServiceAsync;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.logging.client.FirebugLogHandler;
import com.google.gwt.user.client.ui.RootLayoutPanel;

public class MainEntryPoint implements EntryPoint {

	public void onModuleLoad() {
		Logger logger = Logger.getLogger("");
		String version = "1.0";
		Handler[] handlers = logger.getHandlers();
		if (handlers != null) {
			for (Handler h : handlers) {
				logger.removeHandler(h);
			}
		}
		logger.addHandler(new FirebugLogHandler());
		logger.setLevel(Level.FINE);
		logger.fine("onModuleLoad");
		LoginServiceAsync loginService = GWT.create(LoginService.class);
		DSM2InputServiceAsync rpcService = GWT.create(DSM2InputService.class);
		UserProfileServiceAsync userProfileService = GWT
				.create(UserProfileService.class);
		BathymetryDataServiceAsync bathymetryService = GWT
				.create(BathymetryDataService.class);
		DEMDataServiceAsync demService = GWT.create(DEMDataService.class);
		SimpleEventBus eventBus = new SimpleEventBus();
		final AppController appViewer = new AppController(loginService,
				rpcService, userProfileService, bathymetryService, demService,
				eventBus);
		//
		appViewer.go(RootLayoutPanel.get());
		logger.fine("onModuleLoad end");
	}

}
