package gov.ca.bdo.modeling.dsm2.map.client.display;

import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

public class ColorSchemeDisplay extends Composite{
	
	public ColorSchemeDisplay(ContainerDisplay display){
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.add(new MapWidget());
		initWidget(mainPanel);
	}

}
