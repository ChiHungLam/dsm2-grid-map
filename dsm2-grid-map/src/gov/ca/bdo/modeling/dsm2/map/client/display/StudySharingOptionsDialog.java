package gov.ca.bdo.modeling.dsm2.map.client.display;

import gov.ca.bdo.modeling.dsm2.map.client.images.IconImages;
import gov.ca.bdo.modeling.dsm2.map.client.model.UserSharingInfo;

import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

public class StudySharingOptionsDialog extends DialogBox {

	private ListBox sharingType;
	private CellList<UserSharingInfo> userInfoList;
	private FlowPanel userInfoListContainer;
	private TextBox emailBox;
	private ListBox userAuthoringType;
	private Button addUpdateButton;
	private Button deleteButton;
	private ListDataProvider<UserSharingInfo> dataProvider;
	private UserSharingInfo currentSelectedInfo;

	public StudySharingOptionsDialog() {
		setText("Study Sharing Options");
		sharingType = new ListBox();
		sharingType.addItem("unlisted");
		sharingType.addItem("public");
		sharingType.addItem("private");
		sharingType.setSelectedIndex(1);

		IconImages images = GWT.create(IconImages.class);
		userInfoList = new CellList<UserSharingInfo>(new UserSharingInfoCell(
				images.contact()), UserSharingInfo.KEY_PROVIDER);
		final SingleSelectionModel<UserSharingInfo> selectionModel = new SingleSelectionModel<UserSharingInfo>(
				UserSharingInfo.KEY_PROVIDER);
		userInfoList.setSelectionModel(selectionModel);
		selectionModel
				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {

					public void onSelectionChange(SelectionChangeEvent event) {
						updateUserInfoForm(selectionModel.getSelectedObject());
					}
				});
		dataProvider = new ListDataProvider<UserSharingInfo>();
		dataProvider.addDataDisplay(userInfoList);

		emailBox = new TextBox();
		userAuthoringType = new ListBox();
		userAuthoringType.addItem("Editor");
		userAuthoringType.addItem("Viewer");
		addUpdateButton = new Button("Add/Update");
		deleteButton = new Button("Delete");
		addUpdateButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				UserSharingInfo info = new UserSharingInfo(emailBox.getText());
				info.setEditor(userAuthoringType.getSelectedIndex() == 0);
				List<UserSharingInfo> list = dataProvider.getList();
				for (UserSharingInfo uinfo : list) {
					if (uinfo.getEmail().equals(info.getEmail())) {
						uinfo.setEmail(info.getEmail());
						uinfo.setEditor(info.isEditor());
						return;
					}
				}
				dataProvider.getList().add(info);
			}
		});
		deleteButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				dataProvider.getList().remove(currentSelectedInfo);
				currentSelectedInfo = null;
			}
		});

		VerticalPanel mainPanel = new VerticalPanel();
		mainPanel.add(sharingType);
		mainPanel.add(userInfoListContainer = new FlowPanel());
		setWidget(mainPanel);

		sharingType.addChangeHandler(new ChangeHandler() {

			public void onChange(ChangeEvent event) {
				if (sharingType.isItemSelected(2)) {
					userInfoListContainer.add(userInfoList);
				} else {
					userInfoListContainer.remove(userInfoList);
				}
			}
		});
	}

	public List<UserSharingInfo> getUserInfoList() {
		return dataProvider.getList();
	}

	public void setUserInfoList(List<UserSharingInfo> list) {
		dataProvider.getList().clear();
		dataProvider.getList().addAll(list);
	}

	protected void updateUserInfoForm(UserSharingInfo userInfo) {
		currentSelectedInfo = userInfo;
		emailBox.setText(userInfo.getEmail());
		if (userInfo.isEditor()) {
			userAuthoringType.setSelectedIndex(0);
		} else {
			userAuthoringType.setSelectedIndex(1);
		}
	}

	public static class UserSharingInfoCell extends
			AbstractCell<UserSharingInfo> {

		private String imageHtml;

		public UserSharingInfoCell(ImageResource image) {
			imageHtml = AbstractImagePrototype.create(image).getHTML();
		}

		@Override
		public void render(com.google.gwt.cell.client.Cell.Context context,
				UserSharingInfo value, SafeHtmlBuilder sb) {
			// Value can be null, so do a null check..
			if (value == null) {
				return;
			}

			sb.appendHtmlConstant("<table>");

			// Add the contact image.
			sb.appendHtmlConstant("<tr><td rowspan='3'>");
			sb.appendHtmlConstant(imageHtml);
			sb.appendHtmlConstant("</td>");

			// Add the name and address.
			sb.appendHtmlConstant("<td style='font-size:95%;'>");
			sb.appendEscaped(value.getEmail());
			sb.appendHtmlConstant("</td></tr><tr><td>");
			if (value.isEditor()) {
				sb.appendEscaped("Editor");
			} else {
				sb.appendEscaped("Viewer");
			}
			sb.appendHtmlConstant("</td></tr></table>");
		}
	}

	public String getSharingType() {
		return "unlisted";
	}
}
