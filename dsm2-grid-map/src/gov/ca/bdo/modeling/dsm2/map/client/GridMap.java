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

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.maps.utility.client.DefaultPackage;
import com.google.gwt.maps.utility.client.GoogleMapsUtility;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GridMap implements EntryPoint {

	private MapPanel mapPanel;
	private UserProfilePanel userProfilePanel;
	private String currentStudy = null;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		History.addValueChangeHandler(new ValueChangeHandler<String>() {

			public void onValueChange(ValueChangeEvent<String> event) {
				String historyToken = event.getValue();
				if (historyToken.startsWith("user")) {
					showUserProfilePanel();
				} else {
					if (historyToken.startsWith("study")) {
						String[] fields = historyToken.split("=");
						if (fields.length == 2) {
							currentStudy = fields[1];
						}
					}
					showMapPanel();
				}
			}

		});
		History.fireCurrentHistoryState();
	}

	protected void showUserProfilePanel() {
		if (userProfilePanel == null) {
			userProfilePanel = new UserProfilePanel();
		}
		replaceWidget(userProfilePanel);
	}

	protected void replaceWidget(Widget newWidget) {
		RootPanel rootPanel = RootPanel.get("mapcontainer");
		if (rootPanel.getWidgetCount() > 0) {
			Widget widget = rootPanel.getWidget(0);
			if (widget != null) {
				rootPanel.remove(widget);
			}
		}
		rootPanel.add(newWidget);
	}

	protected void showMapPanel() {
		if (!Maps.isLoaded()) {
			Window
					.alert("The Maps API is not installed."
							+ "  The <script> tag that loads the Maps API may be missing or your Maps key may be wrong.");
			return;
		}

		if (!Maps.isBrowserCompatible()) {
			Window.alert("The Maps API is not compatible with this browser.");
			return;
		}
		Runnable mapLoadCallback = new Runnable() {
			public void run() {
				if (mapPanel == null) {
					mapPanel = new MapPanel(null);
				}
				if (currentStudy != null) {
					mapPanel.setStudy(currentStudy);
				}
				replaceWidget(mapPanel);
			}
		};
		if (!GoogleMapsUtility.isLoaded(DefaultPackage.MARKER_CLUSTERER,
				DefaultPackage.LABELED_MARKER, DefaultPackage.MAP_ICON_MAKER)) {
			GoogleMapsUtility.loadUtilityApi(mapLoadCallback,
					DefaultPackage.MARKER_CLUSTERER,
					DefaultPackage.LABELED_MARKER,
					DefaultPackage.MAP_ICON_MAKER);
		} else {
			mapLoadCallback.run();
		}
	}
}
