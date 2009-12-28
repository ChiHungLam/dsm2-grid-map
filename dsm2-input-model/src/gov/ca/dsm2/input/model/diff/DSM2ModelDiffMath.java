package gov.ca.dsm2.input.model.diff;

import gov.ca.dsm2.input.model.Channels;
import gov.ca.dsm2.input.model.DSM2Model;

/**
 * Does set math on DSM2Model
 * 
 * @author nsandhu
 * 
 */
public class DSM2ModelDiffMath {
	/**
	 * Calculates the set difference between model 1 and model 2.
	 * 
	 * <p>
	 * The result are the artifacts in model 1 that do not exist in model 2
	 * <p>
	 * 
	 * @param model1
	 * @param model2
	 * @return
	 */
	public static DSM2Model difference(DSM2Model model1, DSM2Model model2) {
		DSM2Model diffModel = new DSM2Model();
		diffModel.setChannels(difference(model1.getChannels(), model2
				.getChannels()));
		return null;
	}

	public static Channels difference(Channels channels1, Channels channels2) {
		Channels diffChannels = new Channels();
		return diffChannels;
	}

	/**
	 * Calculates the intersection of model 1 and model 2, ie the elements that
	 * are common to both models. Then removes the elements that are identical
	 * even in their properties and retains a change model for the elements
	 * 
	 * @param model1
	 * @param model2
	 * @return
	 */
	public static DSM2Model intersection(DSM2Model model1, DSM2Model model2) {
		DSM2Model model = new DSM2Model();
		return model;
	}
}
