package gov.ca.cdec.maps.client.model;

import java.util.ArrayList;

public class Station {
	public String stationId;
	public String displayName;
	public String elevation;
	public String county;
	public String hydrologicArea;
	public String nearbyCity;
	public double latitude;
	public double longitude;
	public String operator;
	public String dataCollection;
	public ArrayList<Sensor> sensors = new ArrayList<Sensor>();

	public String toString() {
		return "Station " + stationId + " @ (" + latitude + "," + longitude
				+ ") : " + displayName;
	}

}
