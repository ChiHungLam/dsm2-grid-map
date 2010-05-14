package gov.ca.modeling.timeseries.map.shared.service;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ApplicationException extends Exception implements Serializable {
	public ApplicationException() {

	}

	public ApplicationException(String message) {
		super(message);
	}
}
