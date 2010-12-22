package gov.ca.bdo.modeling.dsm2.map.client.display;

import gov.ca.bdo.modeling.dsm2.map.client.presenter.XSectionEditorPresenter.Display;
import gov.ca.dsm2.input.model.XSection;
import gov.ca.dsm2.input.model.XSectionProfile;
import gov.ca.modeling.maps.elevation.client.model.BathymetryDataPoint;
import gov.ca.modeling.maps.elevation.client.model.Profile;

import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class XSectionEditorDisplay extends Composite implements Display {
	public FlexTable table;
	private FlowPanel centerPanel;
	private DockLayoutPanel mainPanel;
	public Button shareButton;

	public XSectionEditorDisplay() {
		mainPanel = new DockLayoutPanel(Unit.EM);
		mainPanel.addWest(new FlowPanel(), 5);
		mainPanel.addEast(new FlowPanel(), 5);
		mainPanel.addSouth(new HTML(""), 1);
		mainPanel.add(centerPanel = new FlowPanel());
		//
		initWidget(mainPanel);
	}

	public Widget asWidget() {
		return this;
	}

	public XSection getXSection() {
		// TODO Auto-generated method stub
		return null;
	}

	public XSectionProfile getXSectionProfile() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setBathymetryPoints(List<BathymetryDataPoint> points) {
		// TODO Auto-generated method stub
		
	}

	public void setElevationProfile(Profile profile) {
		// TODO Auto-generated method stub
		
	}

	public void setMapCenter() {
		// TODO Auto-generated method stub
		
	}

	public void setXSection(XSection xsection) {
		// TODO Auto-generated method stub
		
	}

	public void setXSectionProfile(XSectionProfile xsProfile) {
		// TODO Auto-generated method stub
		
	}
}
