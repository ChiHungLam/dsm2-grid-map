package gov.ca.bdo.modeling.dsm2.map.client.model;

import com.google.gwt.view.client.ProvidesKey;

public class UserSharingInfo {

	/**
	 * The key provider that provides the unique ID of a contact.
	 */
	public static final ProvidesKey<UserSharingInfo> KEY_PROVIDER = new ProvidesKey<UserSharingInfo>() {
		public Object getKey(UserSharingInfo item) {
			return item == null ? null : item.getEmail();
		}
	};
	private String email;
	private boolean editor;

	public UserSharingInfo(String email) {
		this.email = email;
		editor = false;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isEditor() {
		return editor;
	}

	public void setEditor(boolean editor) {
		this.editor = editor;
	}
}
