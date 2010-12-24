package gov.ca.bdo.modeling.dsm2.map.client.display;

import gov.ca.bdo.modeling.dsm2.map.client.presenter.DSM2StudyManagerPresenter.Display;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;

public class StudyManagerDisplay extends ResizeComposite implements Display {
	public FlexTable table;
	private FlowPanel studyPanel;
	private Button deleteButton;
	public Button shareButton;
	private ContainerDisplay containerDisplay;
	private UploadStudyDataDisplay uploadStudyData;
	private UploadStudyDisplay uploadStudy;

	public StudyManagerDisplay(ContainerDisplay containerDisplay) {
		this.containerDisplay = containerDisplay;
		studyPanel = new FlowPanel();
		clearTable();
		studyPanel.add(deleteButton = new Button("Delete Selected"));
		studyPanel.add(shareButton = new Button("Share Selected"));
		TabLayoutPanel mainPanel = new TabLayoutPanel(22, Unit.PX);
		mainPanel.add(studyPanel, "Manage");
		mainPanel.add(uploadStudy = new UploadStudyDisplay(), "Upload Study");
		mainPanel.add(uploadStudyData = new UploadStudyDataDisplay(),
				"Upload Data");
		initWidget(mainPanel);
	}

	public Widget asWidget() {
		return this;
	}

	public void addRowForStudy(String study) {
		if (table == null) {
			clearTable();
		}
		int rowIndex = table.getRowCount();
		Label studyBox = new Label(study);
		table.setWidget(rowIndex, 0, new CheckBox());
		table.setWidget(rowIndex, 1, studyBox);
		table.setWidget(rowIndex, 2, createDownloadHydroButton(study));
		table.setWidget(rowIndex, 3, createDownloadGisButton(study));

	}

	private Widget createDownloadGisButton(final String study) {

		Button downloadButton = new Button("Download GIS");
		downloadButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				String url = GWT.getModuleBaseURL()
						+ "dsm2_download?studyName=" + URL.encode(study)
						+ "&inputName=" + URL.encode("gis_inp");
				Window.open(url, "_blank", null);
			}
		});
		return downloadButton;
	}

	private Widget createDownloadHydroButton(final String study) {
		Button downloadButton = new Button("Download Input");
		downloadButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				String url = GWT.getModuleBaseURL()
						+ "dsm2_download?studyName=" + URL.encode(study)
						+ "&inputName=" + URL.encode("hydro_echo_inp");
				Window.open(url, "_blank", null);
			}
		});
		return downloadButton;
	}

	public void clearTable() {
		if (table != null) {
			table.clear();
		} else {
			table = new FlexTable();
			table.setStyleName("blue-bordered");
			studyPanel.add(table);
		}
		HTML header = new HTML("<b>Selected</b>");
		table.setWidget(0, 0, header);
		header = new HTML("<b>Study Name</b>");
		table.setWidget(0, 1, header);
		header = new HTML("<b>Download Hydro</b>");
		table.setWidget(0, 2, header);
		header = new HTML("<b>Download GIS</b>");
		table.setWidget(0, 3, header);
		table.getRowFormatter().addStyleName(0, "table-header");
		table.getColumnFormatter().setWidth(0, "3em");
		table.getColumnFormatter().setWidth(1, "25em");
		table.getColumnFormatter().setWidth(2, "10em");
		table.getColumnFormatter().setWidth(2, "10em");
	}

	public HasClickHandlers getDeleteButton() {
		return deleteButton;
	}

	public HasClickHandlers getShareButton() {
		return shareButton;
	}

	public ArrayList<String> getSelectedStudies() {
		int rows = table.getRowCount();
		ArrayList<String> selectedRows = new ArrayList<String>();
		for (int i = 0; i < rows; i++) {
			Widget checkBox = table.getWidget(i, 0);
			if (checkBox instanceof CheckBox) {
				if (((CheckBox) checkBox).getValue().booleanValue()) {
					Label label = (Label) table.getWidget(i, 1);
					selectedRows.add(label.getText());
				}
			}
		}
		return selectedRows;
	}

	public void removeStudy(String study) {
		int rows = table.getRowCount();
		for (int i = 0; i < rows; i++) {
			Widget label = table.getWidget(i, 1);
			if (label instanceof Label) {
				if (((Label) label).getText().equals(study)) {
					table.removeRow(i);
					break;
				}
			}
		}
	}

	public void addShareUrl(String study, String url) {
		int rows = table.getRowCount();
		for (int i = 0; i < rows; i++) {
			Widget label = table.getWidget(i, 1);
			if (label instanceof Label) {
				if (((Label) label).getText().equals(study)) {
					table.setWidget(i, 2, new Anchor(url, url));
				}
			}
		}
	}

	public HasChangeHandlers getDataFile() {
		return uploadStudyData.getDataFile();
	}

	public HasText getStudyDataName() {
		return uploadStudyData.getStudyName();
	}

	public HasClickHandlers getUploadDataButton() {
		return uploadStudyData.getUploadButton();
	}

	public void submitDataForm() {
		uploadStudyData.submitForm();
	}

	public void addSubmitDataCompleteHandler(SubmitCompleteHandler handler) {
		uploadStudyData.addSubmitCompleteHandler(handler);
	}

	public HasChangeHandlers getEchoFile() {
		return uploadStudy.getEchoFile();
	}

	public HasChangeHandlers getGisFile() {
		return uploadStudy.getGisFile();
	}

	public HasText getStudyName() {
		return uploadStudy.getStudyName();
	}

	public HasClickHandlers getUploadButton() {
		return uploadStudy.getUploadButton();
	}

	public void submitForm() {
		uploadStudy.submitForm();
	}

	public void addSubmitCompleteHandler(SubmitCompleteHandler handler) {
		uploadStudy.addSubmitCompleteHandler(handler);
	}

}
