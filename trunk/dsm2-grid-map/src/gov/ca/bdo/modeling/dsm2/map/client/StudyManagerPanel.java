package gov.ca.bdo.modeling.dsm2.map.client;

import gov.ca.bdo.modeling.dsm2.map.client.service.DSM2InputServiceAsync;

import com.google.gwt.gen2.table.client.FixedWidthFlexTable;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextBox;

public class StudyManagerPanel extends Composite {

	private DSM2InputServiceAsync service;

	public StudyManagerPanel(DSM2InputServiceAsync dsm2Service) {
		this.service = dsm2Service;
		FlowPanel panel = new FlowPanel();
		initWidget(panel);
		service.getStudyNames(new AsyncCallback<String[]>() {

			public void onSuccess(final String[] result) {
				FlexTable table = new FlexTable();
				table.setHTML(0, 0, "<b>Study Name</b>");
				table.setHTML(0, 1, "<b>Selected</b>");
				for (int i = 0; i < result.length; i++) {
					TextBox studyBox = new TextBox();
					studyBox.setText(result[i]);
					table.setWidget(i + 1, 0, studyBox);
					table.setWidget(i + 1, 1, new CheckBox());
				}
				FlowPanel flowPanel = (FlowPanel) getWidget();
				flowPanel.clear();
				flowPanel.add(table);
			}

			public void onFailure(Throwable caught) {
			}
		});
	}

	private FixedWidthFlexTable createHeaderTable() {
		FixedWidthFlexTable table = new FixedWidthFlexTable();
		table.setHTML(0, 0, "<b>Study Name</b>");
		table.setHTML(0, 1, "<b>Selected</b>");
		return table;
	}

}
