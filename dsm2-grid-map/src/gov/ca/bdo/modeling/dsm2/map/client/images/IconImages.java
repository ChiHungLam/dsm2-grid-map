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
}
