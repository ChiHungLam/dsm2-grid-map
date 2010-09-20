package gov.ca.maps.bathymetry.tiles.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class DataPanel extends Composite {
	private final FlowPanel infoPanel;
	private ToggleButton drawLineButton;

	public DataPanel(final MapPanel mapPanel){
		final ToggleButton showData = new ToggleButton("Click to show data");
		showData.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				if (showData.isDown()) {
					mapPanel.activateShowDataHandler(true);
				} else {
					mapPanel.activateShowDataHandler(false);
				}
			}
		});
		drawLineButton = new ToggleButton("Draw line");
		drawLineButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				if (drawLineButton.isDown()) {
					mapPanel.addLine(DataPanel.this);
				}
			}
		});
		
		showData
				.setTitle("Toggle button to display raw data around point clicked on map");

		infoPanel = new FlowPanel();
		infoPanel.setStyleName("infoPanel");
		

		VerticalPanel dataPanel = new VerticalPanel();
		dataPanel.add(showData);
		dataPanel.add(new HTML("<hr/>"));
		dataPanel.add(drawLineButton);
		dataPanel.add(new HTML("<hr/>"));
		dataPanel.add(infoPanel);

		FlowPanel mainPanel = new FlowPanel();
		mainPanel.add(dataPanel);
		mainPanel.setWidth("500px");
		
		initWidget(mainPanel);

		
	}
	
	public ToggleButton getDrawLineButton() {
		return drawLineButton;
	}

	public void showInInfoPanel(Widget widget) {
		infoPanel.clear();
		infoPanel.add(widget);
	}
}
