package gov.ca.dsm2.input.model;

import java.io.Serializable;

public class OperatingRule implements Serializable {
	public String name;
	public String action;
	public String trigger;

	public String getGateName() {
		int gateIndex = action.indexOf("gate=");
		if (gateIndex < 0) {
			return "";
		}
		int gateNameEndIndex = action.indexOf(",", gateIndex + 5);
		if (gateNameEndIndex < 0) {
			gateNameEndIndex = action.indexOf(")", gateIndex + 5);
		}
		return action.substring(gateIndex + 5, gateNameEndIndex);
	}
}
