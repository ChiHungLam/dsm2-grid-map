package gov.ca.bdo.modeling.dsm2.map.client.display;

import gov.ca.bdo.modeling.dsm2.map.client.HeaderPanel;
import gov.ca.bdo.modeling.dsm2.map.client.presenter.DSM2StudyUploadPresenter.Display;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;

public class UploadStudyDisplay extends Composite implements Display {

	private FileUpload gisFileUpload;
	private TextBox studyNameField;
	private Button uploadButton;
	private FileUpload echoFileUpload;
	private DockLayoutPanel mainPanel;
	private HeaderPanel headerPanel;
	private FormPanel formPanel;

	public UploadStudyDisplay() {
		studyNameField = new TextBox();
		studyNameField.setName("studyName");
		studyNameField.setMaxLength(30);
		echoFileUpload = new FileUpload();
		echoFileUpload.setName("hydro_echo_inp");
		echoFileUpload.setWidth("30em");
		gisFileUpload = new FileUpload();
		gisFileUpload.setName("gis_inp");
		gisFileUpload.setWidth("30em");
		uploadButton = new Button("Upload");

		Label title = new Label("Upload Study Grid");
		title.setStylePrimaryName("bordered-title");

		formPanel = new FormPanel();
		formPanel.setStyleName("bordered-content");
		formPanel.setAction("/dsm2_grid_map/upload_study");
		formPanel.setEncoding("multipart/form-data");
		formPanel.setMethod("post");
		FlowPanel elementsPanel = new FlowPanel();
		formPanel.add(elementsPanel);
		elementsPanel.add(new HTML("<p>Enter name of study:<br>"));
		elementsPanel.add(studyNameField);
		elementsPanel.add(new HTML(
				"<p>Please specify a hydro echo input file: <br>"));
		elementsPanel.add(echoFileUpload);
		elementsPanel.add(new HTML("<p>Please specify a gis input file: <br>"));
		elementsPanel.add(gisFileUpload);
		elementsPanel
				.add(new HTML(
						"<p>Use <a href=\"gis.inp\">this file</a> if you don't have a gis file initially</p>"));
		FlowPanel buttonPanel = new FlowPanel();
		buttonPanel.add(uploadButton);
		elementsPanel.add(buttonPanel);

		FlowPanel containerPanel = new FlowPanel();
		containerPanel.add(title);
		containerPanel.add(formPanel);

		mainPanel = new DockLayoutPanel(Unit.EM);
		headerPanel = new HeaderPanel();
		headerPanel.clearMessages();
		mainPanel.addWest(new FlowPanel(), 5);
		mainPanel.addEast(new FlowPanel(), 5);
		mainPanel.addNorth(headerPanel, 2);
		mainPanel.addSouth(new HTML(""), 1);
		mainPanel.add(containerPanel);
		initWidget(mainPanel);
	}

	public HasChangeHandlers getEchoFile() {
		return echoFileUpload;
	}

	public HasChangeHandlers getGisFile() {
		return gisFileUpload;
	}

	public HasText getStudyName() {
		return studyNameField;
	}

	public HasClickHandlers getUploadButton() {
		return uploadButton;
	}

	public void submitForm() {
		formPanel.submit();
	}

	public Widget asWidget() {
		return this;
	}

	public void addSubmitCompleteHandler(SubmitCompleteHandler handler) {
		formPanel.addSubmitCompleteHandler(handler);
	}

}
