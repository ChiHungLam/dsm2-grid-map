package gov.ca.bdo.modeling.dsm2.map.server.data;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Blob;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class BathymetryDataFile {
	public static int FACTOR = 1000;
	/**
	 * A unique id for this file
	 */
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	@Persistent
	private int latitude100;
	@Persistent
	private int longitude100;
	@Persistent
	private Blob contents;

	public BathymetryDataFile() {

	}

	public int getLatitude100() {
		return latitude100;
	}

	public void setLatitude100(int latitude100) {
		this.latitude100 = latitude100;
	}

	public int getLongitude100() {
		return longitude100;
	}

	public void setLongitude100(int longitude100) {
		this.longitude100 = longitude100;
	}

	public Blob getContents() {
		return contents;
	}

	public void setContents(Blob contents) {
		this.contents = contents;
	}

	public Long getId() {
		return id;
	}
}
