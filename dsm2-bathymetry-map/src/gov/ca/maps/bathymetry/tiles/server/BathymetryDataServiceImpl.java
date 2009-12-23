package gov.ca.maps.bathymetry.tiles.server;

import gov.ca.maps.bathymetry.tiles.client.BathymetryDataService;
import gov.ca.maps.bathymetry.tiles.client.model.BathymetryDataPoint;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class BathymetryDataServiceImpl extends RemoteServiceServlet implements
		BathymetryDataService {

	public List<BathymetryDataPoint> getBathymetryDataPoints(double latitude,
			double longitude) throws SerializationException {
		List<BathymetryDataPoint> list = new ArrayList<BathymetryDataPoint>();
		PersistenceManager persistenceManager = PMF.get()
				.getPersistenceManager();
		try {
			BathymetryDataFileDAO dao = new BathymetryDataFileDAOImpl(
					persistenceManager);
			BathymetryDataFile bathymetryDataFile = dao.getFileForLocation(
					latitude, longitude);
			addBathymetryPointsToList(list, bathymetryDataFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			persistenceManager.close();
		}
		return list;
	}

	private void addBathymetryPointsToList(List<BathymetryDataPoint> list,
			BathymetryDataFile bathymetryDataFile) {
		if (bathymetryDataFile != null) {
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(
					bathymetryDataFile.getContents().getBytes()));
			try {
				while (dis.available() > 0) {
					BathymetryDataPoint dataPoint = readBathymetryDataPoint(dis);
					if (dataPoint != null) {
						list.add(dataPoint);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private BathymetryDataPoint readBathymetryDataPoint(DataInputStream dis) {
		BathymetryDataPoint data = new BathymetryDataPoint();
		try {
			data.latitude = dis.readDouble();
			data.longitude = dis.readDouble();
			data.elevation = dis.readDouble();
			data.year = dis.readInt();
			data.agency = dis.readUTF();
		} catch (IOException ex) {
			//
		}
		return data;
	}

	public List<BathymetryDataPoint> getBathymetryDataPoints(double x1,
			double y1, double x2, double y2) throws SerializationException {
		List<BathymetryDataPoint> list = new ArrayList<BathymetryDataPoint>();
		PersistenceManager persistenceManager = PMF.get()
				.getPersistenceManager();
		BathymetryDataFileDAO dao = new BathymetryDataFileDAOImpl(
				persistenceManager);
		List<BathymetryDataFile> filesAlongLine;
		try {
			filesAlongLine = dao.getFilesAlongLine(x1, y1, x2, y2, 2);
			for (BathymetryDataFile bathymetryDataFile : filesAlongLine) {
				addBathymetryPointsToList(list, bathymetryDataFile);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// project onto line
		projectOntoLine(x1, y1, x2, y2, list);
		return list;
	}

	/**
	 * Draw line thru (x1,y1) and (x2,y2) For each point in BathymetryDataPoint
	 * project onto this line so that elevation remains constant, latitude
	 * becomes distance from this line, longitude becomes the point of
	 * intersection along the line. The units are whatever x1 and y1 are in.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param points
	 */
	public void projectOntoLine(double x1, double y1, double x2, double y2,
			List<BathymetryDataPoint> points) {
		// y = mx+c
		// a1 = m, b1=-1, c1=c
		double m = (y2 - y1) / (x2 - x1);
		double c = y2 - m * x2;
		double a1 = m, b1 = -1, c1 = c;
		double mInverse = -1.0 / m;
		double a2 = mInverse, b2 = -1;
		// y= (-1/m)*x+cInverse
		// a2 = mInverse, b2 = -1, c2=cInverse
		// intersection of these lines
		for (BathymetryDataPoint point : points) {
			double x = point.latitude;
			double y = point.longitude;
			double cInverse = y - mInverse * x;
			double c2 = cInverse;
			x = (b1 * c1 - b2 * c1) / (a1 * b2 - a2 * b1);
			y = (c1 * a2 - c2 * a1) / (a1 * b2 - a2 * b1);
			point.latitude = x;
			point.longitude = y;
		}
	}

}
