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
package gov.ca.bdo.modeling.dsm2.map.client.map;

import gov.ca.bdo.modeling.dsm2.map.client.service.DataService;
import gov.ca.bdo.modeling.dsm2.map.client.service.DataServiceAsync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.event.MapClickHandler;
import com.google.gwt.maps.client.event.MapInfoWindowCloseHandler;
import com.google.gwt.maps.client.event.MarkerClickHandler;
import com.google.gwt.maps.client.event.MarkerDragEndHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.geom.Size;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.maps.client.overlay.Overlay;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TextAnnotationsManager implements MapClickHandler {
	private final MapWidget map;
	/**
	 * maps overlay type and name to text string
	 */
	private final HashMap<Marker, TextAnnotation> overlayTextMap;
	private boolean addingText;
	private final ArrayList<TextAnnotation> textAnnotations;
	private final MarkerClickHandler textClickHandler;
	private final DataServiceAsync dataServiceAsync;
	private final String studyName;

	public TextAnnotationsManager(MapPanel mapPanel) {
		dataServiceAsync = (DataServiceAsync) GWT.create(DataService.class);
		map = mapPanel.getMap();
		overlayTextMap = new HashMap<Marker, TextAnnotation>();
		textClickHandler = new TextClickHandler();
		addingText = false;
		textAnnotations = new ArrayList<TextAnnotation>();
		studyName = mapPanel.getCurrentStudy();
		dataServiceAsync.getNotes(studyName,
				new AsyncCallback<List<TextAnnotation>>() {

					public void onSuccess(List<TextAnnotation> result) {
						if (result == null) {
							return;
						}
						addAllMarkers(result);
					}

					public void onFailure(Throwable caught) {
						textAnnotations.clear();
					}
				});
	}

	public void addAllMarkers(List<TextAnnotation> textAnnotations) {
		for (TextAnnotation annotation : textAnnotations) {
			addMarker(annotation);
		}
	}

	public void saveTextAnnotations() {
		dataServiceAsync.saveNotes(studyName, textAnnotations,
				new AsyncCallback<Void>() {

					public void onFailure(Throwable caught) {
					}

					public void onSuccess(Void result) {
					}
				});
	}

	public Marker addMarker(final TextAnnotation annotation) {
		textAnnotations.add(annotation);
		Marker textMarker = createTextMarkerOverlay(LatLng.newInstance(
				annotation.getLatitude(), annotation.getLongitude()));
		textMarker.addMarkerDragEndHandler(new MarkerDragEndHandler() {

			public void onDragEnd(MarkerDragEndEvent event) {
				Marker sender = event.getSender();
				annotation.setLatitude(sender.getLatLng().getLatitude());
				annotation.setLongitude(sender.getLatLng().getLongitude());
				saveTextAnnotations();
			}
		});
		map.addOverlay(textMarker);
		overlayTextMap.put(textMarker, annotation);
		return textMarker;
	}

	public void startAddingText() {
		addingText = true;
		map.addMapClickHandler(this);
	}

	public void stopAddingText() {
		addingText = false;
		map.removeMapClickHandler(this);
	}

	public void onClick(MapClickEvent event) {
		Overlay overlay = event.getOverlay();
		if (overlay != null) {
			return;
		}
		TextAnnotation annotation = new TextAnnotation();
		LatLng latLng = event.getLatLng();
		annotation.setLatitude(latLng.getLatitude());
		annotation.setLongitude(latLng.getLongitude());
		annotation.setText("");
		Marker marker = addMarker(annotation);
		TextPanel textPanel = new TextPanel(annotation, marker);
		map.getInfoWindow().open(marker, new InfoWindowContent(textPanel));
		map.addInfoWindowCloseHandler(textPanel);
	}

	private Marker createTextMarkerOverlay(LatLng point) {
		Icon icon = Icon.newInstance("images/text.png");
		icon.setShadowURL("images/shadow.png");
		icon.setIconSize(Size.newInstance(32, 32));
		icon.setShadowSize(Size.newInstance(22, 20));
		icon.setIconAnchor(Point.newInstance(16, 20));
		icon.setInfoWindowAnchor(Point.newInstance(5, 1));
		MarkerOptions options = MarkerOptions.newInstance();
		options.setTitle("");
		options.setIcon(icon);
		// -- edit mode options and only for the marker being manipulated --
		options.setDragCrossMove(true);
		options.setDraggable(true);
		options.setClickable(true);
		options.setAutoPan(true);
		final Marker textMarker = new Marker(point, options);
		textMarker.addMarkerClickHandler(textClickHandler);
		return textMarker;
	}

	private class TextPanel extends Composite implements
			MapInfoWindowCloseHandler {
		private final TextAnnotation textAnnotation;
		private Marker marker;
		private TextArea textArea;

		TextPanel(TextAnnotation annotation, Marker m) {
			textAnnotation = annotation;
			marker = m;
			if (addingText) {
				textArea = new TextArea();
				textArea.setText(annotation.getText());
				Button removeAnnotation = new Button("Remove Annotation");
				removeAnnotation.addClickHandler(new ClickHandler() {

					public void onClick(ClickEvent event) {
						map.removeOverlay(marker);
						textAnnotations.remove(textAnnotation);
						saveTextAnnotations();
					}
				});
				VerticalPanel vPanel = new VerticalPanel();
				vPanel.add(textArea);
				vPanel.add(removeAnnotation);
				initWidget(vPanel);
			} else {
				HTML html = new HTML(annotation.getText());
				initWidget(html);
			}
		}

		public void onInfoWindowClose(MapInfoWindowCloseEvent event) {
			if (textArea != null) {
				String text = textArea.getText();
				textAnnotation.setText(text);
				// TODO: this happens too often
				saveTextAnnotations();
			}
		}
	}

	private class TextClickHandler implements MarkerClickHandler {

		public void onClick(MarkerClickEvent event) {
			Marker sender = event.getSender();
			TextAnnotation textAnnotation = overlayTextMap.get(sender);
			TextPanel textPanel = new TextPanel(textAnnotation, sender);
			map.getInfoWindow().open(sender, new InfoWindowContent(textPanel));
			map.addInfoWindowCloseHandler(textPanel);
		}
	}
}
