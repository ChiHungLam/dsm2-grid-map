package gov.ca.bdo.modeling.dsm2.map.client.event;

import gov.ca.dsm2.input.model.DSM2Model;

import com.google.gwt.event.shared.GwtEvent;

public class DSM2StudyEvent extends GwtEvent<DSM2StudyEventHandler> {
	public static final int ADD = 20;
	public static final int DELETE = 10;
	public static Type<DSM2StudyEventHandler> TYPE = new Type<DSM2StudyEventHandler>();
	private String study;
	private DSM2Model model;
	private String oldName;
	private int operationType;

	public DSM2StudyEvent(String study, DSM2Model model) {
		this.study = study;
		this.model = model;
		this.oldName = null;
	}
	
	public DSM2StudyEvent(String studyNewName, String studyOldName){
		this.study = studyNewName;
		this.oldName = studyOldName;
	}
	
	public DSM2StudyEvent(String study, int operationType){
		this.study=study;
		this.operationType = operationType;
	}

	public String getOldName() {
		return oldName;
	}

	public String getStudy() {
		return study;
	}

	public DSM2Model getModel() {
		return model;
	}


	public int getOperationType() {
		return operationType;
	}
	
	@Override
	public Type<DSM2StudyEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DSM2StudyEventHandler handler) {
		if (oldName == null){
			handler.onChange(this);
		} else {
			handler.onStudyNameChange(this);
		}
	}



}
