package gov.ca.bdo.modeling.dsm2.map.client.model;

import java.io.Serializable;
import java.util.ArrayList;

public class StudyInfo implements Serializable {
	public static final String SHARING_PUBLIC = "public";
	public static final String SHARING_PRIVATE = "private";
	public static final String SHARING_UNLISTED = "unlisted";

	private String name;
	private String sharingType;
	private String sharingKey;
	private String sharingName;
	private String ownerEmail;
	private ArrayList<String> sharedWithEmails;

	public StudyInfo() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSharingType() {
		return sharingType;
	}

	public void setSharingType(String sharingType) {
		this.sharingType = sharingType;
	}

	public String getSharingKey() {
		return sharingKey;
	}

	public void setSharingKey(String sharingKey) {
		this.sharingKey = sharingKey;
	}

	public String getSharingName() {
		return sharingName;
	}

	public void setSharingName(String sharingName) {
		this.sharingName = sharingName;
	}

	public String getOwnerEmail() {
		return ownerEmail;
	}

	public void setOwnerEmail(String ownerEmail) {
		this.ownerEmail = ownerEmail;
	}

	public ArrayList<String> getSharedWithEmails() {
		return sharedWithEmails;
	}

	public void setSharedWithEmails(ArrayList<String> sharedWithEmails) {
		this.sharedWithEmails = sharedWithEmails;
	}

}
