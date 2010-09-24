package gov.ca.bdo.modeling.dsm2.map.server.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.jdo.annotations.IdGeneratorStrategy;
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
public class DEMDataFile {
	public static int FACTOR = 100;
	/**
	 * A unique id for this file
	 */
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	@Persistent
	private int xc;
	@Persistent
	private int yc;
	@Persistent
	private Blob contents;

	public DEMDataFile() {

	}

	public int getXc() {
		return xc;
	}

	public void setXc(int xc) {
		this.xc = xc;
	}

	public int getYc() {
		return yc;
	}

	public void setYc(int yc) {
		this.yc = yc;
	}

	public void setBlobData(int[] values) {
		if (values.length == 100) {
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				DataOutputStream daos = new DataOutputStream(baos);
				for (int i = 0; i < values.length; i++) {
					daos.writeInt(values[i]);
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

	public int[] getGridFromBlob() {
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
}
