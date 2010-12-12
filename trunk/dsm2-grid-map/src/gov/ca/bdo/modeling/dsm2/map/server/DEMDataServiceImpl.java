package gov.ca.bdo.modeling.dsm2.map.server;

import gov.ca.bdo.modeling.dsm2.map.server.data.DEMDataFile;
import gov.ca.bdo.modeling.dsm2.map.server.persistence.DEMDataFileDAO;
import gov.ca.bdo.modeling.dsm2.map.server.persistence.DEMDataFileDAOImpl;
import gov.ca.bdo.modeling.dsm2.map.server.utils.PMF;
import gov.ca.modeling.maps.elevation.client.model.CalculationState;
import gov.ca.modeling.maps.elevation.client.model.CoordinateGeometryUtils;
import gov.ca.modeling.maps.elevation.client.model.DEMGridSquare;
import gov.ca.modeling.maps.elevation.client.model.DataPoint;
import gov.ca.modeling.maps.elevation.client.model.GeomUtils;
import gov.ca.modeling.maps.elevation.client.model.Geometry;
import gov.ca.modeling.maps.elevation.client.service.DEMDataService;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.appengine.api.labs.taskqueue.TaskOptions.Method;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class DEMDataServiceImpl extends RemoteServiceServlet implements
		DEMDataService {
	static final Logger logger = Logger.getLogger("DEMDataServiceImpl");
	private static final long BIG_VALUE = 100000000l;
	public List<DataPoint> getElevationAlong(double x1, double y1, double x2,
			double y2) throws SerializationException {
		List<DataPoint> points = new ArrayList<DataPoint>();
		double[] utm1 = GeomUtils.convertToUTM(x1, y1);
		double[] utm2 = GeomUtils.convertToUTM(x2, y2);
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
		double[] utm1 = GeomUtils.convertToUTM(x1, y1);
		double[] utm2 = GeomUtils.convertToUTM(x2, y2);
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
			double[] utm = GeomUtils.convertToUTM(latitude, longitude);
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
			double[] utm = GeomUtils.convertToUTM(latitude, longitude);
			double x = utm[0];
			double y = utm[1];
			return gridAt.getElevationAt(x, y) / 10.0;
		}
	}

	public DEMGridSquare getGridAt(double latitude, double longitude)
			throws SerializationException {
		double[] utm = GeomUtils.convertToUTM(latitude, longitude);
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

	public CalculationState checkStatus(CalculationState state) {
		MemcacheService memcacheService = MemcacheServiceFactory
				.getMemcacheService();
		Long value = (Long) memcacheService.get(state.id + ".sum");
		Long number = (Long) memcacheService.get(state.id + ".number");
		Integer counter = (Integer) memcacheService.get(state.id + ".counter");
		logger.info("Sum : "+value);
		logger.info("Number: "+number);
		logger.info("Counter: "+counter);
		state.latestValue = ((value.doubleValue() - BIG_VALUE) / 10.0)
				/ number.doubleValue();
		state.latestValue = Math.round(state.latestValue * 1000) / 1000.0;
		state.numberOfCompletedTasks = state.numberOfTasks - counter.intValue();
		return state;
	}

	public CalculationState startCalculationOfAverageElevationInArea(
			List<DataPoint> points) {
		int size = points.size();
		double[] x = new double[size], y = new double[size];
		int i = 0;
		StringBuffer xcoords = new StringBuffer();
		StringBuffer ycoords = new StringBuffer();
		for (DataPoint p : points) {
			x[i] = p.x;
			y[i] = p.y;
			xcoords.append(x[i]).append(",");
			ycoords.append(y[i]).append(",");
			i++;
		}
		xcoords.deleteCharAt(xcoords.length() - 1);
		ycoords.deleteCharAt(ycoords.length() - 1);
		double[] xExtent = new double[2], yExtent = new double[2];
		Geometry.findPolygonExtent(x, y, null, xExtent, yExtent, null);
		Queue queue = QueueFactory.getQueue("area");
		double xmin = CoordinateGeometryUtils.roundDown(xExtent[0], 100), xmax = CoordinateGeometryUtils
				.roundDown(xExtent[1], 100) + 100;
		double ymin = yExtent[0], ymax = yExtent[1];
		double ys = ymin;
		CalculationState state = new CalculationState();
		state.latestValue = 0;
		state.startTimeInMillis = System.currentTimeMillis();
		state.numberOfCompletedTasks = 0;
		state.id = "area-" + state.startTimeInMillis;
		MemcacheService memcacheService = MemcacheServiceFactory
				.getMemcacheService();
		memcacheService.put(state.id, state);
		ArrayList<TaskOptions> tasks = new ArrayList<TaskOptions>();
		while (ys <= ymax) {
			TaskOptions taskOptions = TaskOptions.Builder
					.url("/task/elevation");
			taskOptions = taskOptions.method(Method.POST);
			taskOptions = taskOptions.param("id", state.id);
			taskOptions = taskOptions.param("xcs", xcoords.toString()).param(
					"ycs", ycoords.toString());
			taskOptions = taskOptions.param("xmin", xmin + "").param("xmax",
					xmax + "").param("ymin", ys + "").param("ymax",
					(ys + 100) + "");
			ys += 100;
			tasks.add(taskOptions);
		}
		logger.info("Inserting counter value: "+tasks.size());
		memcacheService.put(state.id + ".counter", tasks.size());
		logger.info("Inserting sum value: "+new Long(BIG_VALUE));
		memcacheService.put(state.id + ".sum", new Long(BIG_VALUE));
		logger.info("Inserting number: "+new Long(0));
		memcacheService.put(state.id + ".number", new Long(0));
		state.numberOfTasks = tasks.size();
		queue.add(tasks);
		return state;
	}

	public double startCalculationOfVolumeInAreaForElevation(
			List<DataPoint> points, double elevation) {
		// TODO Auto-generated method stub
		return 0;
	}

}
