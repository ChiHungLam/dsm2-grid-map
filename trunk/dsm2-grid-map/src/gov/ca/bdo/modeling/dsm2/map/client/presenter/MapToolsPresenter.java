package gov.ca.bdo.modeling.dsm2.map.client.presenter;

import gov.ca.bdo.modeling.dsm2.map.client.Presenter;
import gov.ca.dsm2.input.model.Channel;
import gov.ca.modeling.maps.elevation.client.service.BathymetryDataServiceAsync;
import gov.ca.modeling.maps.elevation.client.service.DEMDataServiceAsync;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyEvent;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class MapToolsPresenter implements Presenter{
	
	public static interface Display {

		public abstract void startMeasuringDistanceAlongLine();

		public abstract void stopMeasuringDistanceAlongLine();

		public abstract void startMeasuringAreaInPolygon();

		public abstract void stopMeasuringAreaInPolygon();

		public abstract void addKmlOverlay(final String url);

		public abstract void hideFlowLines();

		public abstract void showFlowLines();

		public abstract void addLine(Channel channel);

		public abstract void startClickingForElevation();

		public abstract void stopClickingForElevation();

		public abstract void startDrawingElevationProfileLine();

		public abstract void stopDrawingElevationProfileLine();

		public abstract void startShowingBathymetryPoints();

		public abstract void stopShowingBathymetryPoints();
		
		public Widget asWidget();

		public TextBox getFindTextBox();

		public Button getFindButton();

		public Button getShowFlowlinesButton();

		public Button getShowChannelAreaButton();

		public Button getMeasureLengthButton();

		public Button getMeasureAreaButton();

		public Button getMeasureVolumeButton();

		public Button getMeasureAverageElevationButton();

		public Button getShowProfileButton();

		public Button getShowElevationButton();

		public Button getShowBathymetryButton();
		
	}

	private DEMDataServiceAsync demService;
	private BathymetryDataServiceAsync bathyService;
	private SimpleEventBus eventBus;
	private Display display;
	private ContainerPresenter containerPresenter;
	
	public MapToolsPresenter(DEMDataServiceAsync demService, BathymetryDataServiceAsync bathyService, SimpleEventBus eventBus, Display display, ContainerPresenter containerPresenter){
		this.demService = demService;
		this.bathyService = bathyService;
		this.eventBus = eventBus;
		this.display = display;
		this.containerPresenter = containerPresenter;
	}

	public void go(HasWidgets container) {
		bind();
		container.clear();
		container.add(display.asWidget());
	}

	public void bind() {
		display.getFindButton().addClickHandler(new ClickHandler() {
			
			public void onClick(ClickEvent event) {
			}
		});
		
		display.getFindTextBox().addKeyDownHandler(new KeyDownHandler() {
			
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER){
				}
			}
		});
	}


}
