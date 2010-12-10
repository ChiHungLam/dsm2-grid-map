package gov.ca.bdo.modeling.dsm2.map.client.presenter;

import gov.ca.bdo.modeling.dsm2.map.client.service.DSM2InputServiceAsync;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Controls the presentation of a view of the DSM2Model as a list
 * of tables. Provides for a view-only or edit mode.
 * @author nsandhu
 *
 */
public class DSM2ModelTablePresenter extends DSM2ModelBasePresenter{

	public DSM2ModelTablePresenter(DSM2InputServiceAsync dsm2InputService,
			HandlerManager eventBus, Display display) {
		super(dsm2InputService, eventBus, display);
	}

}
