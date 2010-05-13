package gov.ca.modeling.maps.widgets.client;


import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.ControlAnchor;
import com.google.gwt.maps.client.control.ControlPosition;
import com.google.gwt.maps.client.control.Control.CustomControl;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class ExpandContractMapControl extends CustomControl {

	public ExpandContractMapControl() {
		super(new ControlPosition(ControlAnchor.TOP_RIGHT, 10, 24));
	}

	@Override
	protected Widget initialize(final MapWidget map) {
		Image upImage = new Image(IconImages.INSTANCE.expandIcon());
		upImage.setTitle("full size");
		Image downImage = new Image(IconImages.INSTANCE.contractIcon());
		downImage.setTitle("normal size");
		final ToggleButton button = new ToggleButton(upImage, downImage);
		button.setStylePrimaryName("expand-contract-button");
		button.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				Style style = map.getElement().getStyle();
				if (button.isDown()) {
					style.setPosition(Position.FIXED);
				} else {
					style.setPosition(Position.ABSOLUTE);
				}
				map.checkResizeAndCenter();
			}
		});
		return button;
	}

	@Override
	public boolean isSelectable() {
		return false;
	}

}