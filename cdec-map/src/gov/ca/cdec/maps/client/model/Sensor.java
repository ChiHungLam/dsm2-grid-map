package gov.ca.cdec.maps.client.model;

public class Sensor {
	public String description;
	public String duration;
	public String dataCollection;
	public String dataAvailable;
	public String sensorNumber;

	public String toString() {
		return "Sensor #" + sensorNumber + " " + description;
	}

}
