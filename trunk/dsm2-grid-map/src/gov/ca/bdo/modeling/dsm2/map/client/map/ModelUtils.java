/*******************************************************************************
 *     Copyright (C) 2009, 2010 Nicky Sandhu, State of California, Department of Water Resources.
 *
 *     DSM2 Grid Map : An online map centric tool to visualize, create and modify 
 *                               DSM2 input and output 
 *     Version 1.0
 *     by Nicky Sandhu
 *     California Dept. of Water Resources
 *     Modeling Support Branch
 *     1416 Ninth Street
 *     Sacramento, CA 95814
 *     psandhu@water.ca.gov
 *
 *     Send bug reports to psandhu@water.ca.gov
 *
 *     This file is part of DSM2 Grid Map
 *     The DSM2 Grid Map is free software and is licensed to you under the terms of the GNU 
 *     General Public License, version 3, as published by the Free Software Foundation.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program; if not, contact the 
 *     Free Software Foundation, 675 Mass Ave, Cambridge, MA
 *     02139, USA.
 *
 *     THIS SOFTWARE AND DOCUMENTATION ARE PROVIDED BY THE CALIFORNIA
 *     DEPARTMENT OF WATER RESOURCES AND CONTRIBUTORS "AS IS" AND ANY
 *     EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *     IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *     PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE CALIFORNIA
 *     DEPARTMENT OF WATER RESOURCES OR ITS CONTRIBUTORS BE LIABLE FOR
 *     ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *     CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 *     OR SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA OR PROFITS; OR
 *     BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *     LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *     (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 *     USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 *     DAMAGE.
 *******************************************************************************/
/**
 *   Copyright (C) 2009, 2010 
 *    Nicky Sandhu
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
 *    GNU General Public License for more details.

 *    You should have received a copy of the GNU General Public License
 *    along with DSM2 Grid Map.  If not, see <http://www.gnu.org/licenses>.
 */
package gov.ca.bdo.modeling.dsm2.map.client.map;

import gov.ca.dsm2.input.model.Channel;
import gov.ca.dsm2.input.model.Node;
import gov.ca.dsm2.input.model.XSection;
import gov.ca.dsm2.input.model.XSectionLayer;
import gov.ca.dsm2.input.model.XSectionProfile;
import gov.ca.modeling.maps.elevation.client.model.GeomUtils;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.maps.client.geom.LatLng;

public class ModelUtils {
	public static LatLng[] getPointsForChannel(Channel channel, Node upNode,
			Node downNode) {
		LatLng[] points = null;
		LatLng upPoint = LatLng.newInstance(upNode.getLatitude(), upNode
				.getLongitude());
		LatLng downPoint = LatLng.newInstance(downNode.getLatitude(), downNode
				.getLongitude());
		List<double[]> latLngPoints = channel.getLatLngPoints();
		if ((latLngPoints != null) && (latLngPoints.size() > 0)) {
			int size = latLngPoints.size();
			points = new LatLng[size + 2];
			for (int i = 1; i < points.length - 1; i++) {
				double[] ds = latLngPoints.get(i - 1);
				points[i] = LatLng.newInstance(ds[0], ds[1]);
			}
		} else {
			points = new LatLng[2];
		}
		points[0] = upPoint;
		points[points.length - 1] = downPoint;
		return points;
	}

	public static LatLng[] getChannelOutlinePoints(Channel channel,
			Node upNode, Node downNode) {
		LatLng[] points = null;
		LatLng upPoint = LatLng.newInstance(upNode.getLatitude(), upNode
				.getLongitude());
		LatLng downPoint = LatLng.newInstance(downNode.getLatitude(), downNode
				.getLongitude());
		List<double[]> latLngPoints = channel.getLatLngPoints();
		if ((latLngPoints != null) && (latLngPoints.size() > 0)) {
			int size = latLngPoints.size();
			points = new LatLng[size + 2];
			for (int i = 1; i < points.length - 1; i++) {
				double[] ds = latLngPoints.get(i - 1);
				points[i] = LatLng.newInstance(ds[0], ds[1]);
			}
		} else {
			points = new LatLng[2];
		}
		points[0] = upPoint;
		points[points.length - 1] = downPoint;
		return points;
	}

	/**
	 * Calculates the profile from the xsection characteristics, the flow line
	 * of the channel and using the relative distance along channel length to
	 * calculate the position of the xsection line end points
	 * 
	 * @param xSection
	 * @param channel
	 * @param upNode
	 * @param downNode
	 * @return
	 */
	public static XSectionProfile calculateProfileFrom(XSection xSection,
			Channel channel, Node upNode, Node downNode) {
		XSectionProfile profile = new XSectionProfile();
		profile.setChannelId(Integer.parseInt(xSection.getChannelId()));
		profile.setDistance(xSection.getDistance());
		List<double[]> endPoints = new ArrayList<double[]>();
		profile.setEndPoints(endPoints);
		LatLng[] endPointLatLngs = calculateEndPoints(xSection, channel,
				upNode, downNode);
		for (LatLng endPointLatLng : endPointLatLngs) {
			double[] point = new double[2];
			point[0] = endPointLatLng.getLatitude();
			point[1] = endPointLatLng.getLongitude();
			endPoints.add(point);
		}
		//
		List<double[]> profilePoints = new ArrayList<double[]>();
		profile.setProfilePoints(profilePoints);
		double maxWidth = getMaxTopWidth(xSection);
		for (XSectionLayer layer : xSection.getLayers()) {
			double w = layer.getTopWidth();
			double[] point1 = new double[2];
			double[] point2 = new double[2];
			point1[0] = maxWidth / 2 - w / 2;
			point1[1] = layer.getElevation();
			if (w > 0) {
				point2[0] = maxWidth / 2 + w / 2;
				point2[1] = layer.getElevation();
			}
			profilePoints.add(0, point1);
			if (w > 0) {
				profilePoints.add(profilePoints.size(), point2);
			}
		}
		return profile;
	}

	private static LatLng[] calculateEndPoints(XSection xSection,
			Channel channel, Node upNode, Node downNode) {
		LatLng[] channelOutlinePoints = getChannelOutlinePoints(channel,
				upNode, downNode);
		double distance = xSection.getDistance();
		distance = channel.getLength() * distance;
		int segmentIndex = GeomUtils.findSegmentAtDistance(
				channelOutlinePoints, distance);
		LatLng point1 = channelOutlinePoints[segmentIndex];
		LatLng point2 = channelOutlinePoints[segmentIndex + 1];
		double segmentDistance = GeomUtils.findDistanceUptoSegment(
				segmentIndex, channelOutlinePoints);
		LatLng point0 = GeomUtils.findPointAtDistance(point1, point2, distance
				- segmentDistance);
		double slope = GeomUtils.getSlopeBetweenPoints(point1, point2);
		double width = getMaxTopWidth(xSection);
		return GeomUtils.getLineWithSlopeOfLengthAndCenteredOnPoint(-1 / slope,
				width, point0);
	}

	public static double getMaxTopWidth(XSection xSection) {
		double width = Double.MIN_VALUE;
		for (XSectionLayer layer : xSection.getLayers()) {
			width = Math.max(width, layer.getTopWidth());
		}
		return width;
	}

}
