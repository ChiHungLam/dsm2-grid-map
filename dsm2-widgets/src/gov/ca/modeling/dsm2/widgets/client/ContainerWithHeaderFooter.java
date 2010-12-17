package gov.ca.modeling.dsm2.widgets.client;

import java.util.Iterator;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
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
public class ContainerWithHeaderFooter extends Composite implements HasWidgets {
	private static final String DEFAULT_MESSAGE = "Loading...";

	private SimpleHeaderPanel headerPanel;
	private MainBarPanel mainLinkBar;
	private FlowPanel footerPanel;
	private FlowPanel containerPanel;
	private FlowPanel loadingPanel;

	public ContainerWithHeaderFooter() {
		DockLayoutPanel mainPanel = new DockLayoutPanel(Unit.PX);
		FlowPanel northPanel = new FlowPanel();
		northPanel.add(headerPanel = new SimpleHeaderPanel());
		northPanel.add(mainLinkBar = new MainBarPanel());
		loadingPanel = new FlowPanel();
		loadingPanel.setStylePrimaryName("message-area");
		loadingPanel.getElement().setId("loading-area");
		northPanel.add(loadingPanel);
		footerPanel = new FlowPanel();
		footerPanel.setStyleName("footer");
		containerPanel = new FlowPanel();
		containerPanel.setStyleName("container");
		showWarningFor(DEFAULT_MESSAGE, 5000);
		mainPanel.addNorth(northPanel, 65);
		mainPanel.addSouth(footerPanel, 25);
		mainPanel.add(containerPanel);
		initWidget(mainPanel);
	}

	public void addLinkToMainBar(Anchor anchor) {
		mainLinkBar.add(anchor);
	}

	public void setActiveMainLink(String href) {
		mainLinkBar.setActiveLink(href);
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
