package gov.ca.bdo.modeling.dsm2.map.server.data;

import gov.ca.modeling.maps.elevation.client.model.DEMGridSquare;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Blob;

/**
 * Stores a digital elevation model in a file Each files contains a set of 10
 * rows, each of which has 10 columns. No data values are indicated by -9999.
 * Elevation is in tenths of a foot and is represented by an integer value, e.g.
 * 1 ~ 1/10th of a foot The xc,yc are coordinates of the top left corner for the
 * grid in meters
 * 
 * @author nsandhu
 * 
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class DEMDataFile {
	public static int FACTOR = 100;
	/**
	 * A unique id for this file
	 */
	@PrimaryKey
	private String name;
	@Persistent
	private int x;
	@Persistent
	private int y;
	@Persistent
	private Blob contents;

	public DEMDataFile() {

	}

	public int getX() {
		return x;
	}

	public void setX(int xc) {
		x = xc;
	}

	public int getY() {
		return y;
	}

	public void setY(int yc) {
		y = yc;
	}

	public void setBlob(Blob blob) {
		contents = blob;
	}

	public Blob getBlob() {
		return contents;
	}

	public void setBlobData(int[] values) {
		if (values.length == 100) {
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				DataOutputStream daos = new DataOutputStream(baos);
				for (int value : values) {
					daos.writeInt(value);
				}
				contents = new Blob(baos.toByteArray());
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		} else {
			throw new RuntimeException(
					"Provide 100 integer values representing elevations in tenths of a foot");
		}
	}

	public int[] getBlobData() {
		try {
			DataInputStream dais = new DataInputStream(
					new ByteArrayInputStream(contents.getBytes()));
			int[] values = new int[100];
			for (int i = 0; i < 100; i++) {
				values[i] = dais.readInt();
			}
			return values;
		} catch (IOException e) {
			e.printStackTrace();
			return new int[0];
		}
	}

	public static int roundOff(double v) {
		return (int) Math.floor(v / FACTOR) * FACTOR;
	}

	public DEMGridSquare toDEMGrid() {
		DEMGridSquare sq = new DEMGridSquare(x, y, getBlobData());
		return sq;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
