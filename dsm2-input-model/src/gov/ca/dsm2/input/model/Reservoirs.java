package gov.ca.dsm2.input.model;

import gov.ca.dsm2.input.parser.TableUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A container for {@link Reservoir} objects {@link #getReservoirs()}. There is
 * also ability to retrieve them from their name {@link #getReservoir(String)}.
 * 
 * @author nsandhu
 * 
 */
@SuppressWarnings("serial")
public class Reservoirs implements Serializable {
	private final ArrayList<Reservoir> reservoirs;
	private final HashMap<String, Reservoir> reservoirIdMap;

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
			buf.append(TableUtil.fromLatLng(reservoir.getLatitude(), reservoir
					.getLongitude()));
			buf.append("\t").append(
					TableUtil.fromLatLngPoints(reservoir.getLatLngPoints()))
					.append("\n");
		}
		buf.append("END\n");
		return buf.toString();
	}
}
