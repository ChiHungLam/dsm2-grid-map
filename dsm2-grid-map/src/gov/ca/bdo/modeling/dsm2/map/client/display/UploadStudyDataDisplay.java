package gov.ca.bdo.modeling.dsm2.map.client.display;

import gov.ca.bdo.modeling.dsm2.map.client.presenter.DSM2StudyDataUploadPresenter.Display;

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

public class UploadStudyDataDisplay extends Composite implements Display {

	private TextBox studyNameField;
	private Button uploadButton;
	private FileUpload dataFileUpload;
	private DockLayoutPanel mainPanel;
	private FormPanel formPanel;

	public UploadStudyDataDisplay() {
		studyNameField = new TextBox();
		studyNameField.setName("studyName");
		studyNameField.setMaxLength(30);
		dataFileUpload = new FileUpload();
		dataFileUpload.setName("dataFile");
		dataFileUpload.setWidth("30em");
		uploadButton = new Button("Upload");
		Label title = new Label("Upload Study Data");
		title.setStylePrimaryName("bordered-title");
		formPanel = new FormPanel();
		formPanel.setStyleName("bordered-content");
		formPanel.setAction("/dsm2_grid_map/upload_data");
		formPanel.setEncoding("multipart/form-data");
		formPanel.setMethod("post");

		FlowPanel elementsPanel = new FlowPanel();
		formPanel.add(elementsPanel);
		elementsPanel.add(new HTML("<p>Enter name of study:<br>"));
		elementsPanel.add(studyNameField);
		elementsPanel.add(new HTML(
				"<p>Please specify the output data file: <br>"));
		elementsPanel.add(dataFileUpload);
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
		mainPanel.addWest(new FlowPanel(), 5);
		mainPanel.addEast(new FlowPanel(), 5);
		mainPanel.addSouth(new HTML(""), 1);
		mainPanel.add(containerPanel);
		initWidget(mainPanel);
	}

	public HasChangeHandlers getDataFile() {
		return dataFileUpload;
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
