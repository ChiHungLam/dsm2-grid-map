package gov.ca.bdo.modeling.dsm2.map.client.presenter;

import gov.ca.bdo.modeling.dsm2.map.client.service.DSM2InputServiceAsync;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.ToggleButton;

public class DSM2GridMapPresenter extends DSM2ModelBasePresenter {
	public interface Display extends DSM2ModelBasePresenter.Display {
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

	public DSM2GridMapPresenter(DSM2InputServiceAsync dsm2InputService,
			HandlerManager eventBus, Display display, boolean viewOnly) {
		super(dsm2InputService, eventBus, display, viewOnly);
	}

	public void go(HasWidgets container) {
		super.go(container);
	}

	protected void bind() {
		super.bind();
		final Display d = (Display) display;
		d.getTextAnnotationButton().addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				Object source = event.getSource();
				if (source instanceof ToggleButton) {
					ToggleButton button = (ToggleButton) source;
					if (button.isDown()) {
						d.turnOnTextAnnotation();
					} else {
						d.turnOffTextAnnotation();
					}
				}
			}
		});

		d.getAddPolylineButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				Object source = event.getSource();
				if (source instanceof ToggleButton) {
					ToggleButton button = (ToggleButton) source;
					if (button.isDown()) {
						d.startMeasuringDistanceAlongLine();
					} else {
						d.stopMeasuringDistanceAlongLine();
					}
				}
			}
		});
		d.getAddPolygonButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				Object source = event.getSource();
				if (source instanceof ToggleButton) {
					ToggleButton button = (ToggleButton) source;
					if (button.isDown()) {
						d.startMeasuringAreaInPolygon();
					} else {
						d.stopMeasuringAreaInPolygon();
					}
				}
			}
		});
		d.getAddKmlButton().addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				String url = d.getKmlUrlBox().getText();
				d.addKmlOverlay(url);
			}
		});
		d.getFlowLineButton().addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				HasClickHandlers flowLineButton = d.getFlowLineButton();
				if (flowLineButton instanceof ToggleButton) {
					ToggleButton flowButton = (ToggleButton) flowLineButton;
					if (flowButton.isDown()) {
						d.showFlowLines();
					} else {
						d.hideFlowLines();
					}
				} else {
					Window.alert("This button should be a toggle!!");
				}
			}
		});
	}

}
