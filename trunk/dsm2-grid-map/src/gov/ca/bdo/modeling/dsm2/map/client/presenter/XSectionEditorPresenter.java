package gov.ca.bdo.modeling.dsm2.map.client.presenter;

import gov.ca.bdo.modeling.dsm2.map.client.Presenter;
import gov.ca.bdo.modeling.dsm2.map.client.service.DSM2InputServiceAsync;
import gov.ca.dsm2.input.model.XSection;
import gov.ca.dsm2.input.model.XSectionProfile;
import gov.ca.modeling.maps.elevation.client.model.BathymetryDataPoint;
import gov.ca.modeling.maps.elevation.client.model.Profile;
import gov.ca.modeling.maps.elevation.client.service.BathymetryDataServiceAsync;
import gov.ca.modeling.maps.elevation.client.service.DEMDataServiceAsync;

import java.util.List;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class XSectionEditorPresenter implements Presenter {

	public interface Display {
		public Widget asWidget();

		public void showErrorMessage(String message);

		public void showMessage(String message);

		public void clearMessages();

		public void setMapCenter();

		public void setXSection(XSection xsection);

		public XSection getXSection();

		public void setXSectionProfile(XSectionProfile xsProfile);

		public XSectionProfile getXSectionProfile();

		public void setElevationProfile(Profile profile);

		public void setBathymetryPoints(List<BathymetryDataPoint> points);

	}

	private DSM2InputServiceAsync dsm2InputService;
	private SimpleEventBus eventBus;
	private Display display;
	private BathymetryDataServiceAsync bathymetryService;
	private DEMDataServiceAsync demService;

	public XSectionEditorPresenter(DSM2InputServiceAsync dsm2InputService,
			BathymetryDataServiceAsync bathymetryService, DEMDataServiceAsync demService,
			SimpleEventBus eventBus2, Display display) {
		this.dsm2InputService = dsm2InputService;
		this.bathymetryService = bathymetryService;
		this.demService = demService;
		this.eventBus = eventBus2;
		this.display = display;
	}
	
	public void bind() {
		display.showMessage("Loading...");		
	}

	public void go(HasWidgets container) {
		bind();
		container.clear();
		container.add(display.asWidget());
	}

}
