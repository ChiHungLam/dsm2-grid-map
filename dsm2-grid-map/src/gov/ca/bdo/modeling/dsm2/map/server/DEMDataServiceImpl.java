package gov.ca.bdo.modeling.dsm2.map.server;

import gov.ca.bdo.modeling.dsm2.map.client.model.DEMGridSquare;
import gov.ca.bdo.modeling.dsm2.map.client.model.DataPoint;
import gov.ca.bdo.modeling.dsm2.map.client.service.DEMDataService;
import gov.ca.bdo.modeling.dsm2.map.server.data.DEMDataFile;
import gov.ca.bdo.modeling.dsm2.map.server.persistence.DEMDataFileDAO;
import gov.ca.bdo.modeling.dsm2.map.server.persistence.DEMDataFileDAOImpl;
import gov.ca.bdo.modeling.dsm2.map.server.utils.PMF;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class DEMDataServiceImpl extends RemoteServiceServlet implements
		DEMDataService {

	public List<DataPoint> getElevationAlong(double x1, double y1, double x2,
			double y2) throws SerializationException {
		List<DataPoint> points = new ArrayList<DataPoint>();
		double[] utm1 = BathymetryDataServiceImpl.convertToUTM(x1, y1);
		double[] utm2 = BathymetryDataServiceImpl.convertToUTM(x2, y2);
		PersistenceManager persistenceManager = PMF.get()
				.getPersistenceManager();
		try {
			DEMDataFileDAO dao = new DEMDataFileDAOImpl(persistenceManager);
			points = CoordinateGeometryUtils.getIntersectionOfLineAndGrid(
					utm1[0], utm1[1], utm2[0], utm2[1], 10);
			DEMGridSquare grid = null;
			for (DataPoint point : points) {
				double x = point.x;
				double y = point.y;
				int x0 = CoordinateGeometryUtils.roundDown(x, 100);
				int y0 = CoordinateGeometryUtils.roundDown(y, 100);
				if ((grid == null)
						|| ((grid.getX() != x0) || (grid.getY() != y0))) {
					DEMDataFile demDataFile = dao.getFileForLocation(x0, y0);
					if (demDataFile == null) {
						grid = new DEMGridSquare(x0, y0, null);
					} else {
						grid = demDataFile.toDEMGrid();
					}
				}
				point.z = grid.getElevationAt(x, y) / 10.0;
			}
			return points;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			persistenceManager.close();
		}
		return points;
	}

	public double getElevationAt(double latitude, double longitude)
			throws SerializationException {
		DEMGridSquare gridAt = getGridAt(latitude, longitude);
		if (gridAt == null) {
			return DEMGridSquare.NODATA / 10.0;
		} else {
			double[] utm = BathymetryDataServiceImpl.convertToUTM(latitude,
					longitude);
			double x = utm[0];
			double y = utm[1];
			return gridAt.getElevationAt(x, y) / 10.0;
		}
	}

	public DEMGridSquare getGridAt(double latitude, double longitude)
			throws SerializationException {
		double[] utm = BathymetryDataServiceImpl.convertToUTM(latitude,
				longitude);
		double x = utm[0];
		double y = utm[1];
		PersistenceManager persistenceManager = PMF.get()
				.getPersistenceManager();
		try {
			DEMDataFileDAO dao = new DEMDataFileDAOImpl(persistenceManager);
			DEMDataFile demFile = dao.getFileForLocation(x, y);
			if (demFile == null) {
				return null;
			}
			DEMGridSquare demGrid = demFile.toDEMGrid();
			return demGrid;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			persistenceManager.close();
		}
	}

	public List<DEMGridSquare> getGridWithin(double x1, double y1, double x2,
			double y2) {
		// TODO Auto-generated method stub
		return null;
	}

}
