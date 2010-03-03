package gov.ca.bdo.modeling.dsm2.map.client.display;

import gov.ca.bdo.modeling.dsm2.map.client.HeaderPanel;
import gov.ca.bdo.modeling.dsm2.map.client.presenter.DSM2StudyManagerPresenter.Display;

import java.util.ArrayList;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class StudyManagerDisplay extends Composite implements Display {
	public FlexTable table;
	private FlowPanel studyPanel;
	private DockLayoutPanel mainPanel;
	private HeaderPanel headerPanel;
	private Button deleteButton;
	public Button shareButton;

	public StudyManagerDisplay() {
		mainPanel = new DockLayoutPanel(Unit.EM);
		headerPanel = new HeaderPanel();
		headerPanel.showMessage(true, "Loading...");
		mainPanel.addNorth(headerPanel, 2);
		mainPanel.addSouth(new HTML(""), 1);
		mainPanel.add(studyPanel = new FlowPanel());
		clearTable();
		studyPanel.add(deleteButton = new Button("Delete Selected"));
		studyPanel.add(shareButton = new Button("Share Selected"));
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
	}

	public void clearTable() {
		if (table != null) {
			table.clear();
		} else {
			table = new FlexTable();
			studyPanel.add(table);
			headerPanel.showMessage(false, "");
		}
		table.setHTML(0, 0, "<b>Selected</b>");
		table.setHTML(0, 1, "<b>Study Name</b>");
	}

	public void showErrorMessage(String message) {
		headerPanel.showError(true, message);
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

	public void showMessage(String message) {
		headerPanel.showMessage(true, message);
	}

	public void clearMessages() {
		headerPanel.clearMessages();
	}
}
