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
}
