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
package gov.ca.bdo.modeling.dsm2.map.server.persistence;

public class GeomUtils {
	/**
	 * Calculates distance between the two points (x1,y1) and (x2,y2)
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public static double distanceBetween(double x1, double y1, double x2, double y2){
		double delx=x2-x1;
		double dely=y2-y1;
		return Math.sqrt(delx*delx+dely*dely);
	}

	/**
	 * Calculates angle formed by line from (x1,y1) to (x2,y2)
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public static double angle(double x1, double y1, double x2, double y2){
		double dely = y2-y1;
		double delx = x2-x1;
		if (delx == 0.0 && dely == 0.0){
			return 0;
		}
		return Math.atan(dely/delx);
	}
	/**
	 * Calculate the lengths of projection formed by line between given point (x,y) and (x1,y1)
	 * onto the line formed by (x1,y1) and (x2,y2) as well as the projection prependicular to the latter line.
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return two values, projection along line (x1,y1) to (x2,y2) and projection perpendicular to it.
	 */
	public static double [] projectionOfPointOntoLine(double x, double y, double x1, double y1, double x2, double y2){
		double d = distanceBetween(x1, y1, x, y);
		double a = angle(x1,y1,x,y);
		double al = angle(x1,y1,x2,y2);
		double angle=a-al;
		double lineProjection = Math.abs(d*Math.cos(angle));
		double perpendicularProjection = Math.abs(d*Math.sin(angle));
		double [] projection = {lineProjection, perpendicularProjection};
		return projection;
	}
}