package gov.ca.bdo.modeling.dsm2.map.client.presenter;

import gov.ca.bdo.modeling.dsm2.map.client.Presenter;
import gov.ca.bdo.modeling.dsm2.map.client.event.DSM2StudyEvent;
import gov.ca.bdo.modeling.dsm2.map.client.event.DSM2StudyEventHandler;
import gov.ca.bdo.modeling.dsm2.map.client.service.DSM2InputServiceAsync;
import gov.ca.dsm2.input.model.DSM2Model;
import gov.ca.modeling.dsm2.widgets.client.events.MessageEvent;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.InitializeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * Presents the model as a set of tables or a map
 * 
 * @author nsandhu
 * 
 */
public class DSM2ModelBasePresenter implements Presenter {
	public interface Display {
		public void setEditMode(boolean editMode);

		public void refresh();

		public void setModel(DSM2Model model);

		public HasClickHandlers getSaveEditButton();

		public Widget asWidget();

		public HandlerRegistration addInitializeHandler(
				InitializeHandler initializeHandler);

	}

	protected Display display;
	protected SimpleEventBus eventBus;
	protected DSM2InputServiceAsync dsm2InputService;
	private ContainerPresenter containerPresenter;

	public DSM2ModelBasePresenter(DSM2InputServiceAsync dsm2InputService,
			SimpleEventBus eventBus2, Display display,
			ContainerPresenter containerPresenter) {
		this.display = display;
		this.containerPresenter = containerPresenter;
		eventBus = eventBus2;
		this.dsm2InputService = dsm2InputService;
	}

	public void go(HasWidgets container) {
		bind();

		container.clear();
		container.add(display.asWidget());
		display.asWidget().setVisible(true);
	}

	protected void bind() {

		eventBus.addHandler(DSM2StudyEvent.TYPE, new DSM2StudyEventHandler() {

			public void onStudyNameChange(DSM2StudyEvent event) {
			}

			public void onChange(DSM2StudyEvent event) {
				display.setModel(event.getModel());
				display.refresh();
			}
		});

		display.getSaveEditButton().addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				Object source = event.getSource();
				if (source instanceof ToggleButton) {
					ToggleButton toggleButton = (ToggleButton) source;
					display.setEditMode(toggleButton.isDown());
					if (toggleButton.isDown()) {
						return;
					}
				}
				final String study = containerPresenter.getCurrentStudy();
				final DSM2Model model = containerPresenter.getModel();
				dsm2InputService.saveModel(study, model,
						new AsyncCallback<Void>() {

							public void onFailure(Throwable caught) {

								eventBus.fireEvent(new MessageEvent(
										"Could not save study: " + study
												+ "! Error message: "
												+ caught.getMessage(),
										MessageEvent.ERROR, 5000));

							}

							public void onSuccess(Void result) {
								eventBus.fireEvent(new MessageEvent(
										"Saved study: " + study,
										MessageEvent.WARNING, 5000));
							}
						});
			}
		});
		
		containerPresenter.fireStudyLoadedEvent();
	}

	public ContainerPresenter getContainerPresenter() {
		return containerPresenter;
	}

}
