package gov.ca.bdo.modeling.dsm2.map.server.data;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Text;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class DSM2ModelFile {
	/**
	 * A unique id for this file
	 */
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	/**
	 * Name of study. Studies group together files
	 */
	@Persistent
	private String studyName;
	/**
	 * Name of the file
	 */
	@Persistent
	private String name;
	/**
	 * Creator/owner of this file
	 */
	@Persistent
	private String owner;
	/**
	 * file contents
	 */
	@Persistent
	private Text contents;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Text getContents() {
		return contents;
	}

	public void setContents(Text contents) {
		this.contents = contents;
	}

	public void setStudyName(String studyName) {
		this.studyName = studyName;
	}

	public String getStudyName() {
		return studyName;
	}
}
