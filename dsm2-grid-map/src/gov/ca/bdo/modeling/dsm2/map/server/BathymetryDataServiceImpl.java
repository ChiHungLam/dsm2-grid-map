/**
 *    Copyright (C) 2009, 2010 
 *    State of California,
 *    Department of Water Resources.
 *    This file is part of DSM2 Grid Map
 *    The DSM2 Grid Map is free software: 
 *    you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *    DSM2 Grid Map is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details. [http://www.gnu.org/licenses]
 *    
 *    @author Nicky Sandhu
 *    
 */
package gov.ca.bdo.modeling.dsm2.map.server;

import gov.ca.bdo.modeling.dsm2.map.client.model.BathymetryDataPoint;
import gov.ca.bdo.modeling.dsm2.map.client.service.BathymetryDataService;
import gov.ca.bdo.modeling.dsm2.map.server.data.BathymetryDataFile;
import gov.ca.bdo.modeling.dsm2.map.server.persistence.BathymetryDataFileDAO;
import gov.ca.bdo.modeling.dsm2.map.server.persistence.BathymetryDataFileDAOImpl;
import gov.ca.bdo.modeling.dsm2.map.server.persistence.GeomUtils;
import gov.ca.bdo.modeling.dsm2.map.server.utils.PMF;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class BathymetryDataServiceImpl extends RemoteServiceServlet implements
		BathymetryDataService {
	public static final Logger logger = Logger.getLogger("bathymetry");

	public List<BathymetryDataPoint> getBathymetryDataPointsAtXY(double x,
			double y) {
		List<BathymetryDataPoint> list = new ArrayList<BathymetryDataPoint>();
		PersistenceManager persistenceManager = PMF.get()
				.getPersistenceManager();
		try {
			BathymetryDataFileDAO dao = new BathymetryDataFileDAOImpl(
					persistenceManager);
			BathymetryDataFile bathymetryDataFile = dao
					.getFileForLocation(x, y);
			addBathymetryPointsToList(list, bathymetryDataFile);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			persistenceManager.close();
		}
		return list;

	}

	public List<BathymetryDataPoint> getBathymetryDataPoints(double latitude,
			double longitude) throws SerializationException {
		double[] utm = convertToUTM(latitude, longitude);
		return getBathymetryDataPointsAtXY(utm[0], utm[1]);
	}

	public double[] convertToUTM(double latitude, double longitude) {
		CoordinateConversion cc = new CoordinateConversion();
		String latLon2UTM = cc.latLon2UTM(latitude, longitude);
		String[] split = latLon2UTM.split("\\s");
		double x = Double.parseDouble(split[2]);
		double y = Double.parseDouble(split[3]);
		return new double[] { x, y };
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
			data.x = dis.readDouble();
			data.y = dis.readDouble();
			data.elevation = dis.readDouble();
			data.year = dis.readInt();
			data.agency = dis.readUTF();
		} catch (IOException ex) {
			//
		}
		return data;
	}

	public List<BathymetryDataPoint> getBathymetryDataPointsAlongLine(
			double lat1, double lng1, double lat2, double lng2)
			throws SerializationException {
		double[] utm1 = convertToUTM(lat1, lng1);
		double[] utm2 = convertToUTM(lat2, lng2);
		double x1 = utm1[0];
		double y1 = utm1[1];
		double x2 = utm2[0];
		double y2 = utm2[1];
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
		double[] xy = new double[2];
		for (BathymetryDataPoint point : points) {
			xy[0] = point.x;
			xy[1] = point.y;
			double[] projection = GeomUtils.projectionOfPointOntoLine(xy[0],
					xy[1], x1, y1, x2, y2);
			point.x = projection[0];
			point.y = projection[1];
		}
	}

	public double getAverageDepthInPolygon(List<double[]> points)
			throws SerializationException {
		// first get bathymetry data files for grid inside the polygon
		// next get points which are not completely inside the polygon
		// next intersect with bathymetry points where grid is not contained c
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Copyright (c) 1970-2003, Wm. Randolph Franklin
	 * 
	 * Permission is hereby granted, free of charge, to any person obtaining a
	 * copy of this software and associated documentation files (the
	 * "Software"), to deal in the Software without restriction, including
	 * without limitation the rights to use, copy, modify, merge, publish,
	 * distribute, sublicense, and/or sell copies of the Software, and to permit
	 * persons to whom the Software is furnished to do so, subject to the
	 * following conditions:
	 * 
	 * 1. Redistributions of source code must retain the above copyright notice,
	 * this list of conditions and the following disclaimers. 2. Redistributions
	 * in binary form must reproduce the above copyright notice in the
	 * documentation and/or other materials provided with the distribution. 3.
	 * The name of W. Randolph Franklin may not be used to endorse or promote
	 * products derived from this Software without specific prior written
	 * permission.
	 * 
	 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
	 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
	 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
	 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
	 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
	 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
	 * USE OR OTHER DEALINGS IN THE SOFTWARE.
	 * 
	 * @param nvert
	 * @param vertx
	 * @param verty
	 * @param testx
	 * @param testy
	 * @return
	 */
	protected boolean pnpoly(float[] vertx, float[] verty, float testx,
			float testy) {
		int i, j;
		boolean c = false;
		int nvert = vertx.length;
		for (i = 0, j = nvert - 1; i < nvert; j = i++) {
			if (((verty[i] > testy) != (verty[j] > testy))
					&& (testx < (vertx[j] - vertx[i]) * (testy - verty[i])
							/ (verty[j] - verty[i]) + vertx[i])) {
				c = !c;
			}
		}
		return c;
	}

	public List<BathymetryDataPoint> getBathymetryDataPoints(double northLat,
			double westLong, double southLat, double eastLong)
			throws SerializationException {

		List<BathymetryDataPoint> list = new ArrayList<BathymetryDataPoint>();
		PersistenceManager persistenceManager = PMF.get()
				.getPersistenceManager();
		BathymetryDataFileDAO dao = new BathymetryDataFileDAOImpl(
				persistenceManager);
		List<BathymetryDataFile> filesWithin;
		try {
			List<BathymetryDataFile> filesAlongLine = dao.getFilesAlongLine(
					northLat, westLong, southLat, eastLong, 2);
			for (BathymetryDataFile bathymetryDataFile : filesAlongLine) {
				addBathymetryPointsToList(list, bathymetryDataFile);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

}
