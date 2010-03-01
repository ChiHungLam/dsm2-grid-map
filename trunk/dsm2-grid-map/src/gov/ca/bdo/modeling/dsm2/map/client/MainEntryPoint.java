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

import gov.ca.bdo.modeling.dsm2.map.client.map.MapUI;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;

public class MainEntryPoint implements EntryPoint, ValueChangeHandler<String> {

	private MapUI mapUI;
	private StudyUI studyUI;

	public void onModuleLoad() {
		History.addValueChangeHandler(this);
		History.fireCurrentHistoryState();
	}

	public void onValueChange(ValueChangeEvent<String> event) {
		String value = event.getValue();
		if ((value == null) || "".equals(value)) {
			gotoMap();
		} else if (value.equals("manage")) {
			gotoStudies();
		} else {
			gotoMap();
		}
	}

	private void gotoStudies() {
		if (studyUI == null) {
			studyUI = new StudyUI();
		}
		studyUI.bind();
	}

	private void gotoMap() {
		if (mapUI == null) {
			mapUI = new MapUI();
		}
		mapUI.bind();
	}
}
