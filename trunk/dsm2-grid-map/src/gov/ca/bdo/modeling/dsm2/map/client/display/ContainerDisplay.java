package gov.ca.bdo.modeling.dsm2.map.client.display;

import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.user.client.ui.HasWidgets;

import gov.ca.bdo.modeling.dsm2.map.client.model.LoginInfo;
import gov.ca.bdo.modeling.dsm2.map.client.presenter.ContainerPresenter.Display;
import gov.ca.bdo.modeling.dsm2.map.server.data.DSM2Study;
import gov.ca.dsm2.input.model.DSM2Model;
import gov.ca.modeling.dsm2.widgets.client.ContainerWithHeaderFooter;

public class ContainerDisplay implements Display{

	private ContainerWithHeaderFooter container;

	public ContainerDisplay(){
		container = new ContainerWithHeaderFooter();
	}
	
	public HasWidgets getContainer(){
		return container;
	}
	
	public boolean isLoggedIn() {
		return false;
	}

	public void setLoginInfo(LoginInfo result) {
		// setup header panel
		// setup link bar
		// setup footer
	}
	
	public void setStudy(DSM2Study study){
		
	}

	public HasWidgets asHasWidgets() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getCurrentStudy() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getStudies() {
		// TODO Auto-generated method stub
		return null;
	}

	public HasChangeHandlers onStudyChange() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setCurrentStudy(String study) {
		// TODO Auto-generated method stub
		
	}

	public void setModel(DSM2Model result) {
		// TODO Auto-generated method stub
		
	}

	public void setStudies(String[] study) {
		// TODO Auto-generated method stub
		
	}

}
