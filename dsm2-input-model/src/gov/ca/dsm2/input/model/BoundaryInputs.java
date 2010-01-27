/**
 *    Copyright (C) 2009, 2010 
 *    State of California,
 *    Department of Water Resources.
 *    This file is part of DSM2 Grid Map
 *    The DSM2 Grid Map is free software: 
 *    you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *    DSM2 Grid Map is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details. [http://www.gnu.org/licenses]
 *    
 *    @author Nicky Sandhu
 *    
 */
package gov.ca.dsm2.input.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class BoundaryInputs implements Serializable {
	public ArrayList<BoundaryInput> stageInputs;
	public ArrayList<BoundaryInput> flowInputs;
	public ArrayList<BoundaryInput> sourceFlowInputs;

	public BoundaryInputs() {
		stageInputs = new ArrayList<BoundaryInput>();
		flowInputs = new ArrayList<BoundaryInput>();
		sourceFlowInputs = new ArrayList<BoundaryInput>();
	}

	public void addFlow(BoundaryInput input) {
		flowInputs.add(input);
	}

	public void addStage(BoundaryInput input) {
		stageInputs.add(input);
	}

	public void addSourceFlow(BoundaryInput input) {
		sourceFlowInputs.add(input);
	}

	public List<BoundaryInput> getFlowInputs() {
		return flowInputs;
	}

	public List<BoundaryInput> getStageInputs() {
		return stageInputs;
	}

	public List<BoundaryInput> getSourceFlowInputs() {
		return sourceFlowInputs;
	}
}
