package gov.ca.modeling.dsm2.widgets.client;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

public class MainBarPanel extends Composite{

	private FlowPanel mainPanel;

	public MainBarPanel(){
		mainPanel = new FlowPanel();
		mainPanel.setStyleName("main-bar");
		initWidget(mainPanel);
	}
	
	public void add(Anchor anchor){
		anchor.setStyleName("tab");
		mainPanel.add(anchor);
	}
	
	public void setActiveLink(String href){
		int widgetCount = mainPanel.getWidgetCount();
		for (int i = 0; i < widgetCount; i++) {
			Anchor anchor = (Anchor) mainPanel.getWidget(i);
			if (anchor.getHref().endsWith(href)) {
				anchor.addStyleName("active");
			} else {
				anchor.removeStyleName("active");
			}
		}
	}
}
