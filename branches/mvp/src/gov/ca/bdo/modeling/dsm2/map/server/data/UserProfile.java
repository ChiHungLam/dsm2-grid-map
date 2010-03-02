/**
 *   Copyright (C) 2009, 2010 
 *    Nicky Sandhu
 *    State of California,
 *    Department of Water Resources.
 *    This file is part of DSM2 Grid Map
 *    The DSM2 Grid Map is free software: 
 *    you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *    DSM2 Grid Map is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.

 *    You should have received a copy of the GNU General Public License
 *    along with DSM2 Grid Map.  If not, see <http://www.gnu.org/licenses>.
 */
package gov.ca.bdo.modeling.dsm2.map.server.data;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * Store the profile of this user. Will be used to grant/revoke access and also
 * to enable sharing of information between users
 * 
 * @author nsandhu
 * 
 */
@SuppressWarnings("serial")
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class UserProfile implements Serializable {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	@Persistent
	private String email;
	@Persistent
	private String accessLevel;
	@Persistent
	private String group;

	public UserProfile() {

	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAccessLevel() {
		return accessLevel;
	}

	public void setAccessLevel(String accessLevel) {
		this.accessLevel = accessLevel;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public Long getId() {
		return id;
	}
}
