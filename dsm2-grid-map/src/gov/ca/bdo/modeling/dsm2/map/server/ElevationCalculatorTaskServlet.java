package gov.ca.bdo.modeling.dsm2.map.server;

import gov.ca.bdo.modeling.dsm2.map.server.data.DEMDataFile;
import gov.ca.bdo.modeling.dsm2.map.server.persistence.DEMDataFileDAO;
import gov.ca.bdo.modeling.dsm2.map.server.persistence.DEMDataFileDAOImpl;
import gov.ca.bdo.modeling.dsm2.map.server.utils.PMF;
import gov.ca.modeling.maps.elevation.client.model.DEMGridSquare;
import gov.ca.modeling.maps.elevation.client.model.Geometry;

import java.io.IOException;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

@SuppressWarnings("serial")
public class ElevationCalculatorTaskServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		double sumElevations = 0;
		double nElevations = 0;
		double xmin = Double.parseDouble(req.getParameter("xmin"));
		double xmax = Double.parseDouble(req.getParameter("xmax"));
		double ymin = Double.parseDouble(req.getParameter("ymin"));
		double ymax = Double.parseDouble(req.getParameter("ymax"));
		PersistenceManager persistenceManager = PMF.get()
				.getPersistenceManager();
		try {
			DEMDataFileDAO dao = new DEMDataFileDAOImpl(persistenceManager);
			List<DEMDataFile> filesWithin = dao.getFilesWithin(xmin, ymin,
					xmax, ymax);
			System.out.println("Getting files within those bounds: "
					+ (filesWithin != null ? filesWithin.size() : 0));
			for (DEMDataFile demFile : filesWithin) {
				DEMGridSquare demGrid = demFile.toDEMGrid();
				// elevations in 10ths of a foot, e.g 1 ft == 10
				int[] elevations = demGrid.getElevations();
				for (int i = 0; i < elevations.length; i++) {
					if (elevations[i] != DEMGridSquare.NODATA) {
						sumElevations += elevations[i] / 10.;
						nElevations++;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			persistenceManager.close();
		}
		double value = sumElevations / nElevations;
		System.out.println("Sum of elevations: " + sumElevations);
		System.out.println("No. of elevations: " + nElevations);
		resp.getWriter().println("Sum of Elevations: "+sumElevations);
		resp.getWriter().println("No. of elevations: "+nElevations);
		resp.getWriter().println("Average Elevation: "+value);
	}

	/**
	 * Calculates the average elevation for the intersection of a 100 m strip
	 * from xmin to max and from ymin to ymax where it intersects with the
	 * polygon defined by xcs and ycs (comma separated values) stores value
	 * using atomic operation and id into memcache and decrements counter to
	 * indicate successful completion
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		double sumElevations = 0;
		double nElevations = 0;
		double[] x = parseArray(req.getParameter("xcs"));
		double[] y = parseArray(req.getParameter("ycs"));
		double xmin = Double.parseDouble(req.getParameter("xmin"));
		double xmax = Double.parseDouble(req.getParameter("xmax"));
		double ymin = Double.parseDouble(req.getParameter("ymin"));
		double ymax = Double.parseDouble(req.getParameter("ymax"));
		System.out.println("Start task to average elevation in (" + xmin + ","
				+ ymin + ") to (" + xmax + "," + ymax + ")");
		PersistenceManager persistenceManager = PMF.get()
				.getPersistenceManager();
		try {
			DEMDataFileDAO dao = new DEMDataFileDAOImpl(persistenceManager);
			List<DEMDataFile> filesWithin = dao.getFilesWithin(xmin, ymin,
					xmax, ymax);
			System.out.println("Getting files within those bounds: "
					+ (filesWithin != null ? filesWithin.size() : 0));
			for (DEMDataFile demFile : filesWithin) {
				DEMGridSquare demGrid = demFile.toDEMGrid();
				// elevations in 10ths of a foot, e.g 1 ft == 10
				int[] elevations = demGrid.getElevations();
				for (int i = 0; i < elevations.length; i++) {
					if (elevations[i] != DEMGridSquare.NODATA) {
						int rowPos = i / 10;
						int colPos = i % 10;
						double pointX = demGrid.getX() + 10 * colPos;
						double pointY = demGrid.getY() + 10 * rowPos;
						boolean pointInsidePolygon = Geometry
								.isPointInsidePolygon(x, y, pointX, pointY);
						if (pointInsidePolygon) {
							sumElevations += elevations[i] / 10.;
							nElevations++;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			persistenceManager.close();
		}
		double value = sumElevations / nElevations;
		System.out.println("Sum of elevations: " + sumElevations);
		System.out.println("No. of elevations: " + nElevations);
		String id = req.getParameter("id");
		MemcacheService memcacheService = MemcacheServiceFactory
				.getMemcacheService();
		memcacheService.increment(id + ".value", Math.round(value * 1000));
		memcacheService.increment(id + ".counter", -1);
		resp.getWriter().println("Sum of Elevations: "+sumElevations);
		resp.getWriter().println("No. of elevations: "+nElevations);
		resp.getWriter().println("Average Elevation: "+value);
	}

	private double[] parseArray(String str) {
		String[] fields = str.split(",");
		double[] x = new double[fields.length];
		for (int i = 0; i < fields.length; i++) {
			x[i] = Double.parseDouble(fields[i]);
		}
		return x;
	}

}
