package gov.ca.bdo.modeling.dsm2.map.client.display;

import gov.ca.bdo.modeling.dsm2.map.client.model.LoginInfo;
import gov.ca.bdo.modeling.dsm2.map.client.presenter.ContainerPresenter.Display;
import gov.ca.dsm2.input.model.DSM2Model;
import gov.ca.modeling.dsm2.widgets.client.ContainerWithHeaderFooter;
import gov.ca.modeling.dsm2.widgets.client.events.MessageEvent;

import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class ContainerDisplay implements Display {

	private ContainerWithHeaderFooter container;
	private String[] studies;
	private DSM2Model model;
	private ListBox studyBox;
	private LoginInfo loginInfo;

	public ContainerDisplay() {
		container = new ContainerWithHeaderFooter();
	}

	public HasWidgets getContainer() {
		return container;
	}

	public boolean isLoggedIn() {
		return (loginInfo != null) && loginInfo.isLoggedIn();
	}

	public void setLoginInfo(LoginInfo result) {
		loginInfo = result;
		// setup header panel
		container.getHeaderPanel().addWidgetToLeft(
				new Image("images/dsm2_icon.png"));
		HTML studyLabel = new HTML("<b>Study :<b>");
		container.getHeaderPanel().addWidgetToRight(studyLabel);
		container.getHeaderPanel().addWidgetToRight(studyBox = new ListBox());
		studyBox.getElement().getStyle().setFloat(Float.LEFT);
		HTML emailLabel = new HTML("<b>" + result.getEmailAddress() + "</b>");
		container.getHeaderPanel().addWidgetToRight(emailLabel);
		container.getHeaderPanel().addWidgetToRight(
				new Anchor("Profile", "#profile"));
		container.getHeaderPanel().addWidgetToRight(new Anchor("Sign out", result.getLogoutUrl()));
		// setup link bar
		container.addLinkToMainBar(new Anchor("Map", "#map"));
		container.addLinkToMainBar(new Anchor("Studies", "#studies"));
		container.addLinkToMainBar(new Anchor("Tables", "#tables"));
		this.setLinkBarActive(History.getToken());
		// setup footer
		String footer = "<div align=\"center\" class=\"footer\"><font color=\"#666666\">"
				+ "&copy;2010 California Department of Water Resources (DWR) </font>"
				+ " - <a href=\"http://water.ca.gov/\">DWR</a>"
				+ " - <a href=\"http://www.water.ca.gov/canav/conditions.cfm\">Conditions ofUse</a>"
				+ " - <a href=\"http://water.ca.gov/privacy.html\">Privacy Policy</a>"
				+ " - <a href=\"http://code.google.com/p/dsm2-grid-map/wiki\">Help</a></div>"
				+ "</div>";
		container.setFooterPanel(new HTML(footer));
	}

	public HasWidgets asHasWidgets() {
		return container;
	}

	public String getCurrentStudy() {
		if (studyBox == null || studyBox.getSelectedIndex()==-1){
			return null;
		}
		return studyBox.getItemText(studyBox.getSelectedIndex());
	}

	public String[] getStudies() {
		if (studyBox==null){
			return null;
		}
		String[] studies = new String[studyBox.getItemCount()];
		for(int i=0; i < studyBox.getItemCount(); i++){
			studies[i] = studyBox.getItemText(i);
		}
		return studies;
	}

	public HasChangeHandlers onStudyChange() {
		return studyBox;
	}

	public void setCurrentStudy(String study) {
		for(int i=0; i < studyBox.getItemCount(); i++){
			if (studyBox.getItemText(i).equals(study)){
				studyBox.setSelectedIndex(i);
			}
		}
	}

	public void setModel(DSM2Model result) {
		model = result;
	}

	public DSM2Model getModel() {
		return model;
	}

	public void setStudies(String[] studies) {
		this.studies = studies;
		studyBox.clear();
		if (studies == null) {
			return;
		}
		for (String study : studies) {
			studyBox.addItem(study);
		}
	}

	public void showMessage(String message, int type, int delayInMillis) {
		if (type == MessageEvent.ERROR) {
			if (delayInMillis > 0) {
				container.showErrorFor(message, delayInMillis);
			} else {
				container.showError(true, message);
			}
		} else {
			if (delayInMillis > 0) {
				container.showWarningFor(message, delayInMillis);
			} else {
				container.showWarning(true, message);
			}
		}
	}

	public Widget asWidget() {
		return container;
	}

	public void setLinkBarActive(String token) {
		if (token.indexOf("/") >= 0) {
			token = token.substring(0,token.indexOf("/"));
		}
		container.setActiveMainLink('#'+token);
	}
}
