package gov.ca.bdo.modeling.dsm2.map.client.presenter;

import gov.ca.bdo.modeling.dsm2.map.client.service.DSM2InputServiceAsync;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.InitializeEvent;
import com.google.gwt.event.logical.shared.InitializeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.ToggleButton;

public class DSM2GridMapPresenter extends DSM2ModelBasePresenter {
	public interface Display extends DSM2ModelBasePresenter.Display{
		public HandlerRegistration addInitializeHandler(
				InitializeHandler initializeHandler);

		public boolean isInEditMode();

		public void setEditMode(boolean editMode);

		public void updateLinks();

		public HasClickHandlers getTextAnnotationButton();

		public void turnOnTextAnnotation();

		public HasClickHandlers getAddPolylineButton();

		public void stopMeasuringDistanceAlongLine();

		public void startMeasuringDistanceAlongLine();

		public void stopMeasuringAreaInPolygon();

		public void startMeasuringAreaInPolygon();

		public HasClickHandlers getAddPolygonButton();

		public void turnOffTextAnnotation();

		public HasClickHandlers getAddKmlButton();

		public HasText getKmlUrlBox();

		public void addKmlOverlay(String url);

		public HasClickHandlers getFlowLineButton();

		public void showFlowLines();

		public void hideFlowLines();

	}

	private Display display;

	public DSM2GridMapPresenter(DSM2InputServiceAsync dsm2InputService,
			HandlerManager eventBus, Display display, boolean viewOnly) {
		super(dsm2InputService, eventBus, display, viewOnly);
	}

	public void go(HasWidgets container) {
		bind();
		container.clear();
		container.add(display.asWidget());
		display.asWidget().setVisible(true);
	}

	public void bind() {
		display.addInitializeHandler(new InitializeHandler() {

			public void onInitialize(InitializeEvent event) {
					setStudyFromHistory();
					loadStudy(display.getCurrentStudy());
			}
		});

		display.getTextAnnotationButton().addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				Object source = event.getSource();
				if (source instanceof ToggleButton) {
					ToggleButton button = (ToggleButton) source;
					if (button.isDown()) {
						display.turnOnTextAnnotation();
					} else {
						display.turnOffTextAnnotation();
					}
				}
			}
		});

		display.getAddPolylineButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				Object source = event.getSource();
				if (source instanceof ToggleButton) {
					ToggleButton button = (ToggleButton) source;
					if (button.isDown()) {
						display.startMeasuringDistanceAlongLine();
					} else {
						display.stopMeasuringDistanceAlongLine();
					}
				}
			}
		});
		display.getAddPolygonButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				Object source = event.getSource();
				if (source instanceof ToggleButton) {
					ToggleButton button = (ToggleButton) source;
					if (button.isDown()) {
						display.startMeasuringAreaInPolygon();
					} else {
						display.stopMeasuringAreaInPolygon();
					}
				}
			}
		});
		display.getAddKmlButton().addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				String url = display.getKmlUrlBox().getText();
				display.addKmlOverlay(url);
			}
		});
		display.getFlowLineButton().addClickHandler(new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				HasClickHandlers flowLineButton = display.getFlowLineButton();
				if (flowLineButton instanceof ToggleButton){
					ToggleButton flowButton = (ToggleButton) flowLineButton;
					if (flowButton.isDown()){
						display.showFlowLines();
					} else {
						display.hideFlowLines();
					}
				} else{
					Window.alert("This button should be a toggle!!");
				}
			}
		});
	}

}
