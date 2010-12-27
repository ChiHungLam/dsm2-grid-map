package gov.ca.modeling.dsm2.widgets.client;

import gov.ca.modeling.dsm2.widgets.client.events.MessageEvent;
import gov.ca.modeling.dsm2.widgets.client.events.MessageEventHandler;

import java.util.Iterator;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

/**
 * A nice container with good styling.
 * 
 * [ Header Bar] [ Main Links Bar] [ Sub links bar ] <Main Container> [Footer
 * Bar]
 * 
 * 
 * @author nsandhu
 * 
 */
public class ContainerWithHeaderFooter extends ResizeComposite implements
		HasWidgets {
	private static final String DEFAULT_MESSAGE = "Loading...";

	private SimpleHeaderPanel headerPanel;
	private LinkBarPanel mainLinkBar;
	private FlowPanel footerPanel;
	private LayoutPanel containerPanel;
	private FlowPanel loadingPanel;

	private LinkBarPanel subLinkBar;

	public ContainerWithHeaderFooter() {
		DockLayoutPanel mainPanel = new DockLayoutPanel(Unit.PX);
		FlowPanel northPanel = new FlowPanel();
		northPanel.add(headerPanel = new SimpleHeaderPanel());
		northPanel.add(mainLinkBar = new LinkBarPanel("main-bar"));
		northPanel.add(subLinkBar = new LinkBarPanel("sub-bar"));
		loadingPanel = new FlowPanel();
		loadingPanel.setStylePrimaryName("message-area");
		loadingPanel.getElement().setId("loading-area");
		northPanel.add(loadingPanel);
		footerPanel = new FlowPanel();
		footerPanel.setStyleName("footer");
		containerPanel = new LayoutPanel();
		containerPanel.setStyleName("container");
		showWarningFor(DEFAULT_MESSAGE, 5000);
		mainPanel.addNorth(northPanel, 63);
		mainPanel.addSouth(footerPanel, 20);
		mainPanel.add(containerPanel);
		this.addHandler(new MessageEventHandler() {

			public void onMessage(MessageEvent event) {
				showMessage(event.getMessage(), event.getType(), event
						.getDelayInMillis());
			}
		}, MessageEvent.TYPE);
		initWidget(mainPanel);
	}

	public void addLinkToMainBar(Anchor anchor) {
		mainLinkBar.add(anchor);
	}

	public void setActiveMainLink(String href) {
		mainLinkBar.setActiveLink(href);
	}

	public void addLinkToSubBar(Anchor anchor) {
		subLinkBar.add(anchor);
	}

	public void setActiveSubLink(String href) {
		subLinkBar.setActiveLink(href);
	}

	public void setContentWidget(Widget content) {
		containerPanel.clear();
		containerPanel.add(content);
	}

	public Widget getContentWidget() {
		if (containerPanel.getWidgetCount() > 0) {
			return containerPanel.getWidget(0);
		} else {
			return null;
		}
	}

	public void setFooterPanel(Widget footer) {
		footerPanel.add(footer);
	}

	public SimpleHeaderPanel getHeaderPanel() {
		return headerPanel;
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

	public void clearMessages() {
		showWarning(false, "");
	}

	public void showMessageFor(String message, String color,
			int delayInMillisecs) {
		showMessage(true, message, color);
		Timer timer = new Timer() {

			@Override
			public void run() {
				clearMessages();
			}
		};
		timer.schedule(delayInMillisecs);
	}

	public void showWarning(boolean show, String message) {
		showMessage(show, message, "#FFF1A8");
	}

	public void showError(boolean show, String message) {
		showMessage(show, message, "#FFDDDD");
	}

	public void showWarningFor(String message, int delayInMillis) {
		showMessageFor(message, "#FFF1A8", delayInMillis);
	}

	public void showErrorFor(String message, int delayInMillis) {
		showMessageFor(message, "#FFDDDD", delayInMillis);
	}

	public void showMessage(String message, int type, int delayInMillis) {
		if (type == MessageEvent.ERROR) {
			if (delayInMillis > 0) {
				showErrorFor(message, delayInMillis);
			} else {
				showError(true, message);
			}
		} else {
			if (delayInMillis > 0) {
				showWarningFor(message, delayInMillis);
			} else {
				showWarning(true, message);
			}
		}
	}

	@Override
	public void add(Widget w) {
		containerPanel.add(w);
	}

	@Override
	public void clear() {
		containerPanel.clear();
	}

	@Override
	public Iterator<Widget> iterator() {
		return containerPanel.iterator();
	}

	@Override
	public boolean remove(Widget w) {
		return containerPanel.remove(w);
	}

}
