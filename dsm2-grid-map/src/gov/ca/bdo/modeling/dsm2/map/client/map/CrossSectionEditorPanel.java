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

	private DEMDataServiceAsync demService;
	private BathymetryDataServiceAsync bathyService;
	private FlowPanel xsEditorPanel;
	private CrossSectionEditor editor;
	private List<DataPoint> demProfilePoints;
	private List<BathymetryDataPoint> bathymetryPoints;
	private Profile profile;
	private Profile dsm2Profile;
	private XSectionProfile xsProfile;
	private XSection xsection;
	private DataPoint origin;
	private DataPoint secondPointForLine;

	public CrossSectionEditorPanel(final MapPanel mapPanel) {
		demService = GWT.create(DEMDataService.class);
		bathyService = GWT.create(BathymetryDataService.class);
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
				if (origin != null) {
					DataPoint xp1 = xSectionProfilePoints.get(0);
					DataPoint xp2 = xSectionProfilePoints
							.get(xSectionProfilePoints.size() - 1);
					double[] ep1 = new double[] { xp1.x, xp1.z };
					double[] ep2 = new double[] { xp2.x, xp2.z };

					ep1 = GeomUtils
							.calculateUTMFromPointAtFeetDistanceAlongLine(
									xp1.x, origin, secondPointForLine);
					ep1 = GeomUtils.convertToLatLng(ep1[0], ep1[1]);
					ep2 = GeomUtils
							.calculateUTMFromPointAtFeetDistanceAlongLine(
									xp2.x, origin, secondPointForLine);
					ep2 = GeomUtils.convertToLatLng(ep2[0], ep2[1]);

					List<double[]> endPoints = new ArrayList<double[]>();
					endPoints.add(ep1);
					endPoints.add(ep2);
					xsProfile.setEndPoints(endPoints);
				}
				List<double[]> profilePoints = new ArrayList<double[]>();
				double shift = xSectionProfilePoints.get(0).x;
				for (int i = 0; i < xSectionProfilePoints.size(); i++) {
					double[] ppoint = new double[2];
					DataPoint p = xSectionProfilePoints.get(i);
					ppoint[0] = p.x - shift;
					ppoint[1] = p.z;
					profilePoints.add(ppoint);
				}
				xsProfile.setProfilePoints(profilePoints);
				List<XSectionLayer> layers = xsProfile.calculateLayers();
				xsection.setLayers(layers);
				mapPanel.getChannelManager().getxSectionLineClickHandler()
						.updateXSLine();
			}
		});
		Button snapToElevationProfileButton = new Button(
				"Snap To Elevation Profile");
		snapToElevationProfileButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				mapPanel
						.showMessage("Snapped to elevation profile. Click 'Set Profile' if you want to save it.");
				profile.points = new ArrayList<DataPoint>(demProfilePoints);
				editor.setXSectionProfile(profile);
			}
		});
		Button trimProfileButton = new Button("Trim Profile To Highest Points");
		trimProfileButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				mapPanel
						.showMessage("Trimmed profile. Click 'Set Profile' if you want to save it.");
				profile.points = new ArrayList<DataPoint>(ModelUtils
						.getTrimmedPoints(profile.points));
				editor.setXSectionProfile(profile);
			}
		});
		buttonPanel.add(setProfileButton);
		buttonPanel.add(snapToElevationProfileButton);
		buttonPanel.add(trimProfileButton);
		initWidget(mainPanel);
	}

	public void draw(Channel channel, int index, final MapPanel mapPanel) {
		xsection = channel.getXsections().get(index);
		xsProfile = ModelUtils.getOrCalculateXSectionalProfile(xsection,
				channel, mapPanel.getNodeManager().getNodes());
		// get calculated profile based on dsm2 xsection information
		Node upNode = mapPanel.getNodeManager().getNodes().getNode(
				channel.getUpNodeId());
		Node downNode = mapPanel.getNodeManager().getNodes().getNode(
				channel.getDownNodeId());
		XSectionProfile calculatedProfile = ModelUtils.calculateProfileFrom(
				xsection, channel, upNode, downNode);
		profile = generateProfileFromXsectionProfile(xsProfile);
		dsm2Profile = generateProfileFromXsectionProfile(calculatedProfile);

		mapPanel
				.showMessage("Fetching elevation profile and bathymetry points...");
		demService.getBilinearInterpolatedElevationAlong(profile.x1,
				profile.y1, profile.x2, profile.y2,
				new AsyncCallback<List<DataPoint>>() {

					public void onSuccess(final List<DataPoint> demPoints) {
						bathyService.getBathymetryDataPointsAlongLine(
								profile.x1, profile.y1, profile.x2, profile.y2,
								new AsyncCallback<List<BathymetryDataPoint>>() {

									public void onSuccess(
											List<BathymetryDataPoint> bathyPoints) {
										mapPanel
												.showMessage("Drawing profile and points");
										origin = createUTMDataPoint(profile.x1,
												profile.y1);
										secondPointForLine = createUTMDataPoint(
												profile.x2, profile.y2);
										GeomUtils
												.moveOriginAndProjectOntoLineAndConvertToFeet(
														bathyPoints, origin,
														secondPointForLine);
										GeomUtils
												.moveOriginAndProjectOntoLineAndConvertToFeet(
														demPoints, origin,
														secondPointForLine);
										demProfilePoints = demPoints;
										bathymetryPoints = bathyPoints;
										editor = new CrossSectionEditor(
												"xsection", profile,
												dsm2Profile, demPoints,
												bathyPoints, 600, 450);
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
						mapPanel
								.showErrorMessage("Could not load DEM profile data: "
										+ caught.getMessage());
					}
				});

	}

	private void shiftOrigin(double rightShift) {
		for (DataPoint p : profile.points) {
			p.x = p.x - rightShift;
		}
		for (DataPoint p : dsm2Profile.points) {
			p.x = p.x - rightShift;
		}
		for (DataPoint p : demProfilePoints) {
			p.x = p.x - rightShift;
		}
		for (DataPoint p : bathymetryPoints) {
			p.x = p.x - rightShift;
		}
	}

	private Profile generateProfileFromXsectionProfile(XSectionProfile xsProfile) {
		List<double[]> endPoints = xsProfile.getEndPoints();
		List<double[]> profilePoints = xsProfile.getProfilePoints();
		Profile prof = new Profile();
		prof.points = new ArrayList<DataPoint>();
		for (int i = 0; i < profilePoints.size(); i++) {
			double[] ds = profilePoints.get(i);
			DataPoint p = new DataPoint();
			p.x = ds[0];
			p.y = 0;
			p.z = ds[1];
			prof.points.add(p);
		}
		prof.x1 = endPoints.get(0)[0];
		prof.y1 = endPoints.get(0)[1];
		prof.x2 = endPoints.get(1)[0];
		prof.y2 = endPoints.get(1)[1];
		return prof;
	}

	private DataPoint createUTMDataPoint(double latitude, double longitude) {
		DataPoint p = new DataPoint();
		double[] utml = GeomUtils.convertToUTM(latitude, longitude);
		p.x = utml[0];
		p.y = utml[1];
		return p;
	}
}
