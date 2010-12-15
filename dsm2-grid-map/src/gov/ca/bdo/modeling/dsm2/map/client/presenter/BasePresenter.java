package gov.ca.bdo.modeling.dsm2.map.client.presenter;

import gov.ca.bdo.modeling.dsm2.map.client.Presenter;
import gov.ca.bdo.modeling.dsm2.map.client.model.LoginInfo;
import gov.ca.bdo.modeling.dsm2.map.client.service.LoginServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

public class BasePresenter implements Presenter{
	public static interface Display {
		public boolean isLoggedIn();
		public void setLoginInfo(LoginInfo result);
	}

	private LoginServiceAsync loginService;
	private Display display;
	private SimpleEventBus eventBus;
	
	public BasePresenter(LoginServiceAsync loginService, SimpleEventBus eventBus, Display display){	
		this.loginService = loginService;
		this.display=display;
		this.eventBus = eventBus;
	}


	public void go(HasWidgets container) {
		bind();
		container.clear();
	}
	
	public void bind(){
		if (!display.isLoggedIn()){
			loginService.login(GWT.getHostPageBaseURL(), new AsyncCallback<LoginInfo>() {
				
				public void onSuccess(LoginInfo result) {
					display.setLoginInfo(result);
				}
				
				public void onFailure(Throwable caught) {
					Location.reload();
				}
			});
		}
	}
}