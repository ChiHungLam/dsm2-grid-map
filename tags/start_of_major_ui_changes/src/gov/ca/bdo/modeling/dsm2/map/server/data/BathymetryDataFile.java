/**
 *    Copyright (C) 2009, 2010 
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
 *    GNU General Public License for more details. [http://www.gnu.org/licenses]
 *    
 *    @author Nicky Sandhu
 *    
 */
package gov.ca.bdo.modeling.dsm2.map.server.data;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Blob;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class BathymetryDataFile {
	public static int FACTOR = 100;
	/**
	 * A unique id for this file
	 */
	@PrimaryKey
	private String name;
	@Persistent
	private int x;
	@Persistent
	private int y;
	@Persistent
	private Blob contents;

	public BathymetryDataFile() {
	}

	public int getX100() {
		return x;
	}

	public void setX100(int x100) {
		this.x = x100;
	}

	public int getY100() {
		return y;
	}

	public void setY100(int y100) {
		this.y = y100;
	}

	public Blob getContents() {
		return contents;
	}

	public void setContents(Blob contents) {
		this.contents = contents;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public static int roundOff(double v){
		return (int) (Math.round(v/FACTOR)*FACTOR);
	}

}
