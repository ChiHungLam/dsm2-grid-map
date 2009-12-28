package gov.ca.dsm2.input.model.diff;

import gov.ca.dsm2.input.model.DSM2Model;

/**
 * This object contains the result of diffing two DSM2Models. In other words
 * model2-model1 is something like this model
 * 
 * If an element in model2 is identical in model 1 then it is not present in the
 * diff
 * 
 * If an element in model2 is not identical in model 1, the it is present here
 * as the original element in model 1 and the new element in model 2
 * 
 * If an element in model 2 is not present in model 1, it is considered a pure
 * add and the element in model 1 is null
 * 
 * If an element in model 1 is not present in model 2, it is considered a pure
 * delete and the element in model 2 is null
 * 
 * @author nsandhu
 * 
 */
public class DSM2ModelDiff {
	DSM2Model addToBase;
	DSM2Model deleteFromBase;
	DSM2Model changeToBase;

	public DSM2ModelDiff(DSM2Model modelBase, DSM2Model modelChanged) {
		// modelChanged-modelBase = addToBase
		// modelBase - modelChanged = deleteFromBase
		// modelBase intersection modelChanged = changeToBase
	}

}
