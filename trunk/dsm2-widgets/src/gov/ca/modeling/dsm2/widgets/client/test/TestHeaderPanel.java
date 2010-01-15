package gov.ca.modeling.dsm2.widgets.client.test;

import gov.ca.modeling.dsm2.widgets.client.HeaderPanel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class TestHeaderPanel implements EntryPoint {
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		final HeaderPanel headerPanel = new HeaderPanel();
		headerPanel.addToLinkPanel(new Anchor("Grid", "/grid"));
		headerPanel.addToLinkPanel(new Anchor("Bathymetry", "/bathymetry"));
		headerPanel.addToLinkPanel(new Anchor("Text", "/text"));
		HTML nameDisplay = new HTML("<b>sandhudavis@gmail.com</b>");
		nameDisplay.setStyleName("name");
		headerPanel.addToRightSide(nameDisplay);
		headerPanel.addToRightSide(new Anchor("Settings", "/settings"));
		headerPanel.addToRightSide(new Anchor("Logout", "/logout"));

		//
		RootPanel.get().add(headerPanel);
		FlowPanel leftBar = new FlowPanel();
		leftBar.setStyleName("bar1");
		leftBar.getElement().getStyle().setLeft(0, Unit.PT);
		FlowPanel rightBar = new FlowPanel();
		rightBar.setStyleName("bar1");
		rightBar.getElement().getStyle().setRight(0, Unit.PT);
		RootPanel.get().add(new FlowPanel());
		//
		headerPanel.showWarning(true, "Will be hiding in 5 seconds...");
		//
		Timer timer = new Timer() {

			@Override
			public void run() {
				headerPanel.showWarning(false, null);
				headerPanel.showError(true, "Error message here...");
			}
		};
		timer.schedule(5000);
		Timer timer2 = new Timer() {

			@Override
			public void run() {
				headerPanel.showWarning(false, null);
			}
		};
		timer2.schedule(10000);
	}
}
