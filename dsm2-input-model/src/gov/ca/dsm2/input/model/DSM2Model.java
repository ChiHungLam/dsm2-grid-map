package gov.ca.dsm2.input.model;

import java.io.Serializable;
@SuppressWarnings("serial")
public class DSM2Model implements Serializable{
	private Channels channels;
	private Nodes nodes;
	private Reservoirs reservoirs;
	private Gates gates;
	private Outputs outputs;
	private BoundaryInputs inputs;
	/**
	 * 
	 */
	public DSM2Model(){
		channels = new Channels();
		nodes = new Nodes();
		reservoirs = new Reservoirs();
		gates = new Gates();
	}

	public Reservoirs getReservoirs() {
		return reservoirs;
	}

	public void setReservoirs(Reservoirs reservoirs) {
		this.reservoirs = reservoirs;
	}

	public Gates getGates() {
		return gates;
	}

	public void setGates(Gates gates) {
		this.gates = gates;
	}

	public void setChannels(Channels channels) {
		this.channels = channels;
	}

	public void setNodes(Nodes nodes) {
		this.nodes = nodes;
	}

	public Channels getChannels(){
		return channels;
	}
	
	public Nodes getNodes(){
		return nodes;
	}

	public void setInputs(BoundaryInputs inputs) {
		this.inputs = inputs;
	}

	public BoundaryInputs getInputs() {
		return inputs;
	}

	public void setOutputs(Outputs outputs) {
		this.outputs = outputs;
	}

	public Outputs getOutputs() {
		return outputs;
	}
}
