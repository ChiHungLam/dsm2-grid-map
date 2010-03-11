package gov.ca.dsm2.input.model.calculator.test;

import gov.ca.dsm2.input.model.Channel;
import gov.ca.dsm2.input.model.XSection;
import gov.ca.dsm2.input.model.XSectionLayer;
import junit.framework.TestCase;

public class TestModelCalculator extends TestCase {

	private Channel channel;

	protected void setUp() {
		channel = new Channel();
		channel.setId("425");
		channel.setDispersion(0.04);
		channel.setMannings(0.1);
		channel.setDownNodeId("5");
		channel.setUpNodeId("33");
		channel.setLength(2500);
		XSection upSection = new XSection();
		XSectionLayer layer0 = new XSectionLayer();
		layer0.setArea(0);
		layer0.setElevation(-5);
		layer0.setTopWidth(0);
		layer0.setWettedPerimeter(0);
		upSection.addLayer(layer0);
		XSectionLayer layer1 = new XSectionLayer();
		layer1.setArea(10);
		layer1.setElevation(-2);
		layer1.setTopWidth(12);
		layer1.setWettedPerimeter(15);
		upSection.addLayer(layer1);
		upSection.setChannelId(channel.getId());
		channel.addXSection(upSection);
		XSection downSection = new XSection();
		downSection.addLayer(layer0);
		downSection.addLayer(layer1);
		channel.addXSection(downSection);
	}
}
