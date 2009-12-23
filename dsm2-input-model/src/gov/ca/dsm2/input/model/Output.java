package gov.ca.dsm2.input.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Output implements Serializable{
	public String name;
	public String variable;
	public String interval;
	public String period_op;
	public String file;
	public Output(){
		
	}
}
