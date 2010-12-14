package gov.ca.modeling.dsm2.widgets.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class SimpleHeaderPanel extends Composite {

	private FlowPanel rightSide;
	private FlowPanel leftSide;

	public SimpleHeaderPanel() {
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.setStyleName("simple-header");
		rightSide = new FlowPanel();
		mainPanel.add(rightSide);
		rightSide.setStyleName("simple-header-right");
		leftSide = new FlowPanel();
		mainPanel.add(leftSide);
		leftSide.setStyleName("simple-header-left");
		// decoration
		FlowPanel leftPanel = new FlowPanel();
		leftPanel.setStyleName("simple-header-bar");
		leftPanel.getElement().setAttribute("style", "left: 0pt;");
		mainPanel.add(leftPanel);
		FlowPanel pixelPanel = new FlowPanel();
		pixelPanel.setStyleName("");
		pixelPanel.getElement().setAttribute("style", "height: 1px");
		mainPanel.add(pixelPanel);
		//
		initWidget(mainPanel);
	}

	public void addWidgetToRight(Widget w) {
		rightSide.add(w);
	}

	public void insertWidgetToRight(Widget w, int index) {
		rightSide.insert(w, index);
	}

	public void addWidgetToLeft(Widget w) {
		leftSide.add(w);
	}

	public void insertWidgetToLeft(Widget w, int index) {
		leftSide.insert(w, index);
	}
}
