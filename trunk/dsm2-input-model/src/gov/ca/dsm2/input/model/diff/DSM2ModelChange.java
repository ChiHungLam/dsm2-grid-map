package gov.ca.dsm2.input.model.diff;

import gov.ca.dsm2.input.model.Channel;
import gov.ca.dsm2.input.model.DSM2Model;

/**
 * A structure to represent to change in a model.
 * 
 * This can be generically represented as a tuple of (Model[Base], Model[New])
 * where the tuple is present for all elements that are present in both models
 * but are different in their intrinsic properties.
 * 
 * @author nsandhu
 * 
 */
public class DSM2ModelChange {
	private final DSM2Model modelBase;
	private final DSM2Model modelChanged;

	public DSM2ModelChange() {
		modelBase = new DSM2Model();
		modelChanged = new DSM2Model();
	}

	public void addChannelChange(Channel channelBase, Channel channelChanged) {
	}
}
