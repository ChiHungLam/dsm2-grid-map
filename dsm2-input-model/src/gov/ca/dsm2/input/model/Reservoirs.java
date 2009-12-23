package gov.ca.dsm2.input.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("serial")
public class Reservoirs implements Serializable {
	private ArrayList<Reservoir> reservoirs;
	private HashMap<String, Reservoir> reservoirIdMap;

	public Reservoirs() {
		reservoirs = new ArrayList<Reservoir>();
		reservoirIdMap = new HashMap<String, Reservoir>();
	}

	public void addReservoir(Reservoir reservoir) {
		reservoirs.add(reservoir);
		reservoirIdMap.put(reservoir.getName(), reservoir);
	}

	public Reservoir getReservoir(String reservoirId) {
		return reservoirIdMap.get(reservoirId);
	}

	public void removeReservoir(Reservoir reservoir) {
		reservoirs.remove(reservoir);
		reservoirIdMap.remove(reservoir.getName());
	}

	public List<Reservoir> getReservoirs() {
		return reservoirs;
	}

	public String buildGISTable() {
		StringBuilder buf = new StringBuilder();
		buf.append("RESERVOIR_GIS\n");
		buf.append("ID\tLAT_LNG\tINTERIOR_LAT_LNG\n");
		for (Reservoir reservoir : reservoirs) {
			buf.append(reservoir.getName()).append("\t");
			buf.append(TableUtil.buildLatLng(reservoir.getLatitude(), reservoir
					.getLongitude()));
			buf.append("\t").append(
			TableUtil.buildInteriorLatLngPoints(reservoir.getLatLngPoints()))
					.append("\n");
		}
		buf.append("END\n");
		return buf.toString();
	}
}
