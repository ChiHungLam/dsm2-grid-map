package gov.ca.maps.bathymetry.tiles.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * Control for retrieving point, profile along a line
 * 
 * @author nsandhu
 * 
 */
public class ControlPanel extends Composite {

	private final FlowPanel infoPanel;
	private final ToggleButton drawLineButton;

	public ControlPanel(final MapPanel mapPanel) {
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
					mapPanel.addLine(ControlPanel.this);
				}
			}
		});
		//
		FlowPanel buttonPanel = new FlowPanel();
		buttonPanel.add(showData);
		buttonPanel.add(drawLineButton);
		infoPanel = new FlowPanel();
		infoPanel.setStyleName("infoPanel");
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.add(buttonPanel);
		mainPanel.add(infoPanel);
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
