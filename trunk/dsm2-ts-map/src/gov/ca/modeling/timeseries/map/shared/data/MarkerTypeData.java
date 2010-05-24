package gov.ca.modeling.timeseries.map.shared.data;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Entity;

@Entity
public class MarkerTypeData {
	@Id
	private Long id;
	private String name;
	private String imageURL;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public Long getId() {
		return id;
	}
}
