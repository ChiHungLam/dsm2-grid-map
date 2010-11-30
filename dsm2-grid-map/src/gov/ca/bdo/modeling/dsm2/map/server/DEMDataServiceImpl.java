package gov.ca.bdo.modeling.dsm2.map.server;

import gov.ca.bdo.modeling.dsm2.map.server.data.DEMDataFile;
import gov.ca.bdo.modeling.dsm2.map.server.persistence.DEMDataFileDAO;
import gov.ca.bdo.modeling.dsm2.map.server.persistence.DEMDataFileDAOImpl;
import gov.ca.bdo.modeling.dsm2.map.server.utils.PMF;
import gov.ca.modeling.maps.elevation.client.model.CoordinateGeometryUtils;
import gov.ca.modeling.maps.elevation.client.model.DEMGridSquare;
import gov.ca.modeling.maps.elevation.client.model.DataPoint;
import gov.ca.modeling.maps.elevation.client.service.DEMDataService;

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

	public List<DataPoint> getBilinearInterpolatedElevationAlong(double x1,
			double y1, double x2, double y2) throws SerializationException {
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
				point.z = getBilinearInterpolatedElevationAtUTM(grid, x, y);
			}
			return points;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			persistenceManager.close();
		}
		return points;
	}

	public double getBilinearInterpolatedElevationAt(double latitude,
			double longitude) throws SerializationException {
		DEMGridSquare gridAt = getGridAt(latitude, longitude);
		if (gridAt == null) {
			return DEMGridSquare.NODATA / 10.0;
		} else {
			double[] utm = BathymetryDataServiceImpl.convertToUTM(latitude,
					longitude);
			double x = utm[0];
			double y = utm[1];
			return getBilinearInterpolatedElevationAtUTM(gridAt, x, y);
		}
	}

	public double getBilinearInterpolatedElevationAtUTM(DEMGridSquare gridAt,
			double x, double y) throws SerializationException {
		if (gridAt == null) {
			return DEMGridSquare.NODATA / 10.0;
		} else {
			// f=b1+b2*x+b3*x+b4*x*y
			double f00 = gridAt.getElevationAt(x, y) / 10.0;
			double f10 = getElevationAtUTMWithBestGuessGrid(gridAt, x + 10, y);
			double f01 = getElevationAtUTMWithBestGuessGrid(gridAt, x, y + 10);
			double f11 = getElevationAtUTMWithBestGuessGrid(gridAt, x + 10,
					y + 10);
			double b1 = f00;
			double b2 = f10 - f00;
			double b3 = f01 - f00;
			double b4 = f00 - f10 - f01 + f11;
			double x0 = Math.floor(x / 10) * 10;
			double y0 = Math.floor(y / 10) * 10;
			double nx = (x - x0) / 10.0;
			double ny = (y - y0) / 10.0;
			return b1 + b2 * nx + b3 * ny + b4 * nx * ny;
		}
	}

	public boolean contains(DEMGridSquare grid, double x, double y) {
		double x0 = grid.getX();
		double y0 = grid.getY();
		return (x < x0 + 100) && (y < y0 + 100);
	}

	public double getElevationAtUTMWithBestGuessGrid(DEMGridSquare grid,
			double x, double y) throws SerializationException {
		if (!contains(grid, x, y)) {
			DEMGridSquare gridAt = getGridAtUTM(x, y);
			if (gridAt != null) { // keep best guess if no grid available
				grid = gridAt;
			}
		}
		return grid.getElevationAt(x, y) / 10.0;
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
		return getGridAtUTM(x, y);
	}

	public DEMGridSquare getGridAtUTM(double x, double y)
			throws SerializationException {
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