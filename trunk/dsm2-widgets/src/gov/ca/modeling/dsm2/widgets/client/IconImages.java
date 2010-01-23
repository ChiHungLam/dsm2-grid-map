package gov.ca.modeling.dsm2.widgets.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface IconImages extends ClientBundle {
	public static final IconImages INSTANCE = GWT.create(IconImages.class);

	@Source("expand.png")
	public ImageResource expandIcon();

	@Source("contract.png")
	public ImageResource contractIcon();

}
