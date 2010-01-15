package gov.ca.modeling.dsm2.widgets.client;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class HeaderPanel extends FlowPanel {

	private NoBrPanel nobrPanel;
	private ParagraphPanel paragraphPanel;
	private FlowPanel loadingPanel;
	private static final String DEFAULT_MESSAGE = "Loading...";

	public HeaderPanel() {
		FlowPanel linkPanel = new FlowPanel();
		linkPanel.getElement().setId("topbar");
		nobrPanel = new NoBrPanel();
		linkPanel.add(nobrPanel);
		//
		paragraphPanel = new ParagraphPanel();
		paragraphPanel.getElement().setId("info");
		this.add(linkPanel);
		this.add(paragraphPanel);
		FlowPanel rightBarPanel = new FlowPanel();
		rightBarPanel.setStyleName("bar");
		rightBarPanel.getElement().getStyle().setRight(0, Unit.PT);
		FlowPanel leftBarPanel = new FlowPanel();
		leftBarPanel.setStyleName("bar");
		rightBarPanel.getElement().getStyle().setLeft(0, Unit.PT);
		this.add(rightBarPanel);
		this.add(leftBarPanel);
		loadingPanel = new FlowPanel();
		loadingPanel.setStylePrimaryName("message-area");
		loadingPanel.getElement().setId("loading-area");
		showWarning(true, DEFAULT_MESSAGE);
		this.add(loadingPanel);
	}

	public void addToLinkPanel(Widget w) {
		if (w instanceof Anchor) {
			w.setStyleName("linkbar");
		}
		nobrPanel.add(w);
	}

	public void addToRightSide(Widget w) {
		if (w instanceof Anchor) {
			w.setStyleName("linkbar");
		}
		paragraphPanel.add(w);
	}

	public void showMessage(boolean show, String message, String color) {
		if (show) {
			loadingPanel.getElement().getStyle().setBackgroundColor(color);
			loadingPanel.removeStyleName("hidden");
			if (message != null) {
				loadingPanel.getElement().setInnerText(message);
			} else {
				loadingPanel.getElement().setInnerText(DEFAULT_MESSAGE);
			}
		} else {
			loadingPanel.addStyleName("hidden");
		}
	}

	public void showWarning(boolean show, String message) {
		showMessage(show, message, "#FFF1A8");
	}

	public void showError(boolean show, String message) {
		showMessage(show, message, "#FFDDDD");
	}
}
