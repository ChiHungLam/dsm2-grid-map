package gov.ca.dsm2.input.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class BoundaryInputs implements Serializable{
	public ArrayList<BoundaryInput> stageInputs;
	public ArrayList<BoundaryInput> flowInputs;
	public ArrayList<BoundaryInput> sourceFlowInputs;
	public BoundaryInputs(){
		stageInputs = new ArrayList<BoundaryInput>();
		flowInputs = new ArrayList<BoundaryInput>();
		sourceFlowInputs = new ArrayList<BoundaryInput>();
	}
	
	public void addFlow(BoundaryInput input){
		flowInputs.add(input);
	}
	
	public void addStage(BoundaryInput input){
		stageInputs.add(input);
	}
	
	public void addSourceFlow(BoundaryInput input){
		sourceFlowInputs.add(input);
	}
	
	public List<BoundaryInput> getFlowInputs(){
		return flowInputs;
	}
		
	public List<BoundaryInput> getStageInputs(){
		return stageInputs;
	}
	
	public List<BoundaryInput> getSourceFlowInputs(){
		return sourceFlowInputs;
	}
}
