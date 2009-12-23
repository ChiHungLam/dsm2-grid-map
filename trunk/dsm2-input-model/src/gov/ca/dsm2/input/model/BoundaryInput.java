package gov.ca.dsm2.input.model;

import java.io.Serializable;

/**
 * The boundary input to the model. This generally represented by a file and
 * path that contains the time series providing this information
 * 
 * @author psandhu
 * 
 */
@SuppressWarnings("serial")
public class BoundaryInput implements Serializable {
	/**
	 * Name of boundary input
	 */
	public String name;
	/**
	 * The id of the node at which the input is attached
	 */
	public String nodeId;
	/**
	 * The sign (+ve/-ve) that the input needs to be multiplied by before use
	 */
	public int sign;
	/**
	 * what kind of fillin if any should be used when values may be missing in
	 * the time series, e.g. last value
	 */
	public String fillIn;
	/**
	 * file name in which this input resides
	 */
	public String file;
	/**
	 * a reference to the data within that file (multiple inputs maybe in a
	 * file)
	 */
	public String path;

	public BoundaryInput() {
		sign = 1;
	}

}
