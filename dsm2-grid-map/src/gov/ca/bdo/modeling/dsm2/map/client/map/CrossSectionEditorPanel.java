package gov.ca.bdo.modeling.dsm2.map.client.map;

import gov.ca.dsm2.input.model.Channel;
import gov.ca.dsm2.input.model.Node;
import gov.ca.dsm2.input.model.XSection;
import gov.ca.dsm2.input.model.XSectionLayer;
import gov.ca.dsm2.input.model.XSectionProfile;
import gov.ca.modeling.maps.elevation.client.CrossSectionEditor;
import gov.ca.modeling.maps.elevation.client.model.BathymetryDataPoint;
import gov.ca.modeling.maps.elevation.client.model.DataPoint;
import gov.ca.modeling.maps.elevation.client.model.GeomUtils;
import gov.ca.modeling.maps.elevation.client.model.Profile;
import gov.ca.modeling.maps.elevation.client.service.BathymetryDataService;
import gov.ca.modeling.maps.elevation.client.service.BathymetryDataServiceAsync;
import gov.ca.modeling.maps.elevation.client.service.DEMDataService;
import gov.ca.modeling.maps.elevation.client.service.DEMDataServiceAsync;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * Displays the cross section editor along with controls to adjust, modify and
 * delete cross section
 * 
 * @author nsandhu
 * 
 */
public class CrossSectionEditorPanel extends Composite {

	private FlowPanel xsEditorPanel;
	private CrossSectionEditor editor;
	private List<DataPoint> elevationProfile;
	private Profile profile;
	private XSectionProfile xsProfile;
	private XSection xsection;
	private MapPanel mapPanel;

	public CrossSectionEditorPanel(final MapPanel mapPanel) {
		this.mapPanel = mapPanel;
		FlowPanel mainPanel = new FlowPanel();
		HorizontalPanel buttonPanel = new HorizontalPanel();
		mainPanel.add(buttonPanel);
		xsEditorPanel = new FlowPanel();
		mainPanel.add(xsEditorPanel);
		xsEditorPanel.getElement().setId("xsection");
		xsEditorPanel.clear();
		Button setProfileButton = new Button("Set Profile");
		setProfileButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				mapPanel.showMessage("Profile set as shown");
				List<DataPoint> xSectionProfilePoints = editor
						.getXSectionProfilePoints();
				List<double[]> profilePoints = new ArrayList<double[]>();
				for (int i = 0; i < xSectionProfilePoints.size(); i++) {
					double[] ppoint = new double[2];
					DataPoint p = xSectionProfilePoints.get(i);
					ppoint[0] = p.x;
					ppoint[1] = p.z;
					profilePoints.add(ppoint);
				}
				xsProfile.setProfilePoints(profilePoints);
				List<XSectionLayer> layers = xsProfile.calculateLayers();
				xsection.setLayers(layers);
			}
		});
		Button snapToElevationProfileButton = new Button(
				"Snap To Elevation Profile");
		snapToElevationProfileButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				mapPanel
						.showMessage("Snapped to elevation profile. Click 'Set Profile' if you want to save it.");
				profile.points = new ArrayList<DataPoint>(elevationProfile);
				editor.setXSectionProfile(profile);
				editor.redraw();
			}
		});
		Button trimProfileButton = new Button("Trim Profile To Highest Points");
		trimProfileButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				mapPanel
						.showMessage("Trimmed profile. Click 'Set Profile' if you want to save it.");
				profile.points = ModelUtils.getTrimmedPoints(profile.points);
				DataPoint p1 = profile.points.get(0);
				DataPoint p2 = profile.points.get(profile.points.size() - 1);
				profile.x1 = p1.x;
				profile.y1 = p1.y;
				profile.x2 = p2.x;
				profile.y2 = p2.y;
				editor.setXSectionProfile(profile);
				editor.redraw();
			}
		});
		buttonPanel.add(setProfileButton);
		buttonPanel.add(snapToElevationProfileButton);
		buttonPanel.add(trimProfileButton);
		initWidget(mainPanel);
	}

	public void draw(Channel channel, int index, final MapPanel mapPanel) {
		xsection = channel.getXsections().get(index);
		DEMDataServiceAsync demService = GWT.create(DEMDataService.class);
		final BathymetryDataServiceAsync bathyService = GWT
				.create(BathymetryDataService.class);
		XSectionProfile profileFrom = xsection.getProfile();
		NodeMarkerDataManager nodeManager = mapPanel.getNodeManager();
		if (profileFrom == null) {
			Node upNode = nodeManager.getNodes().getNode(channel.getUpNodeId());
			Node downNode = nodeManager.getNodes().getNode(
					channel.getDownNodeId());
			profileFrom = ModelUtils.calculateProfileFrom(xsection, channel,
					upNode, downNode);
			xsection.setProfile(profileFrom);
		}
		xsProfile = profileFrom;
		List<double[]> endPoints = xsProfile.getEndPoints();
		List<double[]> profilePoints = xsProfile.getProfilePoints();
		profile = new Profile();
		profile.points = new ArrayList<DataPoint>();
		for (int i = 0; i < profilePoints.size(); i++) {
			double[] ds = profilePoints.get(i);
			DataPoint p = new DataPoint();
			p.x = ds[0];
			p.y = 0;
			p.z = ds[1];
			profile.points.add(p);
		}
		profile.x1 = endPoints.get(0)[0];
		profile.y1 = endPoints.get(0)[1];
		profile.x2 = endPoints.get(1)[0];
		profile.y2 = endPoints.get(1)[1];
		// get calculated profile based on dsm2 xsection information
		Node upNode = mapPanel.getNodeManager().getNodes().getNode(
				channel.getUpNodeId());
		Node downNode = mapPanel.getNodeManager().getNodes().getNode(
				channel.getDownNodeId());
		XSectionProfile calculatedProfile = ModelUtils.calculateProfileFrom(
				xsection, channel, upNode, downNode);
		final Profile dsm2xs = new Profile();
		dsm2xs.points = new ArrayList<DataPoint>();
		for (int i = 0; i < calculatedProfile.getProfilePoints().size(); i++) {
			double[] ds = calculatedProfile.getProfilePoints().get(i);
			DataPoint p = new DataPoint();
			p.x = ds[0];
			p.y = 0;
			p.z = ds[1];
			dsm2xs.points.add(p);
		}
		dsm2xs.x1 = endPoints.get(0)[0];
		dsm2xs.y1 = endPoints.get(0)[1];
		dsm2xs.x2 = endPoints.get(1)[0];
		dsm2xs.y2 = endPoints.get(1)[1];
		mapPanel
				.showMessage("Fetching elevation profile and bathymetry points...");
		demService.getBilinearInterpolatedElevationAlong(profile.x1,
				profile.y1, profile.x2, profile.y2,
				new AsyncCallback<List<DataPoint>>() {

					public void onSuccess(final List<DataPoint> demProfilePoints) {
						bathyService.getBathymetryDataPointsAlongLine(
								profile.x1, profile.y1, profile.x2, profile.y2,
								new AsyncCallback<List<BathymetryDataPoint>>() {

									public void onSuccess(
											List<BathymetryDataPoint> bathymetryPoints) {
										mapPanel
												.showMessage("Drawing profile and points");
										ArrayList<BathymetryDataPoint> bathyPoints = new ArrayList<BathymetryDataPoint>(
												bathymetryPoints);
										for (int i = 0; i < bathymetryPoints
												.size(); i++) {
											BathymetryDataPoint bp = bathymetryPoints
													.get(i);
											BathymetryDataPoint p = new BathymetryDataPoint();
											p.x = bp.x;
											p.y = bp.y;
											p.z = bp.z;
											p.year = bp.year;
											bathyPoints.add(p);
										}
										double[] utm1 = GeomUtils.convertToUTM(
												profile.x1, profile.y1);
										double[] utm2 = GeomUtils.convertToUTM(
												profile.x2, profile.y2);
										DataPoint origin = new DataPoint();
										origin.x = utm1[0];
										origin.y = utm1[1];
										DataPoint secondPointForLine = new DataPoint();
										secondPointForLine.x = utm2[0];
										secondPointForLine.y = utm2[1];
										GeomUtils
												.moveOriginAndProjectOntoLineAndConvertToFeet(
														demProfilePoints,
														origin,
														secondPointForLine);
										elevationProfile = demProfilePoints;
										GeomUtils
												.moveOriginAndProjectOntoLineAndConvertToFeet(
														bathyPoints, origin,
														secondPointForLine);
										editor = new CrossSectionEditor(
												"xsection", profile, dsm2xs,
												demProfilePoints, bathyPoints,
												600, 450);
										mapPanel.showMessage("");
									}

									public void onFailure(Throwable caught) {
										mapPanel
												.showErrorMessage("Could not load Bathymetry data: "
														+ caught.getMessage());
									}

								});
					}

					public void onFailure(Throwable caught) {
						// TODO: add the xsection line and other relevant info
						GWT.log("Could not load DEM profile data", caught);
					}
				});

	}
}
