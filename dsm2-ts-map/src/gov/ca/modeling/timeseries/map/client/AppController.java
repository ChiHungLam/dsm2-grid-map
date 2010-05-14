package gov.ca.modeling.timeseries.map.client;

import gov.ca.modeling.timeseries.map.client.display.HeaderDisplayImpl;
import gov.ca.modeling.timeseries.map.client.display.MapViewDisplayImpl;
import gov.ca.modeling.timeseries.map.client.display.UserProfileDisplayImpl;
import gov.ca.modeling.timeseries.map.client.event.HeaderMessageEvent;
import gov.ca.modeling.timeseries.map.client.event.HeaderMessageEventHandler;
import gov.ca.modeling.timeseries.map.client.presenter.HeaderPresenter;
import gov.ca.modeling.timeseries.map.client.presenter.MapViewPresenter;
import gov.ca.modeling.timeseries.map.client.presenter.UserProfilePresenter;
import gov.ca.modeling.timeseries.map.shared.service.DataServiceAsync;
import gov.ca.modeling.timeseries.map.shared.service.LoginServiceAsync;
import gov.ca.modeling.timeseries.map.shared.service.UserProfileServiceAsync;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;

public class AppController implements Presenter, ValueChangeHandler<String> {
	private final HandlerManager eventBus;
	private HasWidgets container;
	private LoginServiceAsync loginService;
	private DataServiceAsync dataService;
	private UserProfileServiceAsync userProfileService;
	private DockLayoutPanel pagePanel;
	private FlowPanel headerPanelContainer;
	private FlowPanel mainPanelContainer;
	private HeaderPresenter headerPresenter;

	public AppController(LoginServiceAsync loginService,
			DataServiceAsync dataService,
			UserProfileServiceAsync userProfileService, HandlerManager eventBus) {
		this.loginService = loginService;
		this.dataService = dataService;
		this.userProfileService = userProfileService;
		this.eventBus = eventBus;
		bind();
	}

	private void bind() {
		History.addValueChangeHandler(this);
		// eventBus handlers
		eventBus.addHandler(HeaderMessageEvent.TYPE,
				new HeaderMessageEventHandler() {
					public void onMessage(HeaderMessageEvent event) {
						if (headerPresenter != null) {
							headerPresenter.showMessage(event);
						}
					}
				});

	}

	public void go(HasWidgets container) {
		this.container = container;
		pagePanel = new DockLayoutPanel(Unit.EM);
		pagePanel.addNorth(headerPanelContainer = new FlowPanel(), 3);
		pagePanel.add(mainPanelContainer = new FlowPanel());
		this.container.add(pagePanel);
		if ("".equals(History.getToken())) {
			History.newItem("view");
		} else {
			History.fireCurrentHistoryState();
		}
	}

	public void onValueChange(ValueChangeEvent<String> event) {
		String token = event.getValue();
		if (headerPresenter == null) {
			headerPresenter = new HeaderPresenter(loginService, eventBus,
					new HeaderDisplayImpl());
			headerPresenter.go(headerPanelContainer);
		}
		if (token != null) {
			Presenter presenter = null;
			if (token.startsWith("view")) {
				presenter = new MapViewPresenter(dataService, eventBus,
						new MapViewDisplayImpl());
			} else if (token.startsWith("edit")) {
			} else if (token.startsWith("upload")) {
			} else if (token.startsWith("user")) {
				presenter = new UserProfilePresenter(userProfileService,
						eventBus, new UserProfileDisplayImpl());
			} else {

			}

			if (presenter != null) {
				presenter.go(mainPanelContainer);
			}
		}
	}
}
