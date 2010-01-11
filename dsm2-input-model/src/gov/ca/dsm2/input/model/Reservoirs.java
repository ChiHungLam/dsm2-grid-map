package gov.ca.dsm2.input.model;

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

}
