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
package gov.ca.bdo.modeling.dsm2.map.client.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface IconImages extends ClientBundle {
	public static final IconImages INSTANCE = GWT.create(IconImages.class);

	@Source("hide.png")
	public ImageResource hideIcon();

	@Source("show.png")
	public ImageResource showIcon();

	@Source("expand.png")
	public ImageResource expandIcon();

	@Source("close.png")
	public ImageResource closeIcon();

	@Source("pin.png")
	public ImageResource pinIcon();

	@Source("measure_ON.gif")
	public ImageResource measureOnIcon();

	@Source("measure_OFF.gif")
	public ImageResource measureOffIcon();

	@Source("measure-poly.png")
	public ImageResource measurePolygonIcon();

	@Source("adding_text_1.gif")
	public ImageResource addingTextIcon();

	@Source("elevation.gif")
	public ImageResource elevationIcon();

	@Source("elevationProfile.gif")
	public ImageResource elevationProfileIcon();
}
