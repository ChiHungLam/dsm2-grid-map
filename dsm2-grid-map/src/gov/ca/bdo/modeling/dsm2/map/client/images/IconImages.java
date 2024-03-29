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

	@Source("measureLength.gif")
	public ImageResource measureLengthIcon();

	@Source("measureArea.png")
	public ImageResource measureAreaIcon();

	@Source("adding_text_1.gif")
	public ImageResource addingTextIcon();

	@Source("add.png")
	public ImageResource addIcon();

	@Source("edit.png")
	public ImageResource editIcon();

	@Source("save.png")
	public ImageResource saveIcon();

	@Source("download.png")
	public ImageResource downloadIcon();

	@Source("find.png")
	public ImageResource findIcon();

	@Source("delete.png")
	public ImageResource deleteIcon();

	@Source("flowline.png")
	public ImageResource flowLineIcon();

	@Source("blue_MarkerG.png")
	public ImageResource blueGMarkerIcon();

	@Source("blue_MarkerN.png")
	public ImageResource blueNMarkerIcon();

	@Source("blue_MarkerO.png")
	public ImageResource blueOMarkerIcon();

	@Source("blue_MarkerR.png")
	public ImageResource blueRMarkerIcon();

	@Source("contact.jpg")
	public ImageResource contact();
}
