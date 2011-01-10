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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.apache.commons.codec.binary.Base64;

import com.google.appengine.api.datastore.Text;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class DSM2ModelFile {
	static final Logger logger = Logger.getLogger("DSM2ModelFile");
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

	// FIXME: remove once all entities have been migrated.
	@Persistent
	private String zipped;

	public boolean isZipped() {
		return zipped != null && zipped.equals("zipped");
	}

	public void setZipped(boolean zipped) {
		this.zipped = "zipped";
	}

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

	public String getContents() {
		if (isZipped()) {
			StringBuffer buffer = new StringBuffer();
			try {
				byte[] decoded = Base64.decodeBase64(contents.getValue().getBytes());
				GZIPInputStream zipis = new GZIPInputStream(
						new ByteArrayInputStream(decoded));
				LineNumberReader reader = new LineNumberReader(
						new InputStreamReader(zipis));
				String line = null;
				while ((line = reader.readLine()) != null) {
					buffer.append(line).append("\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
				buffer.append(e.getMessage());
			} finally {
			}
			String str = buffer.toString();
			return str;
		} else {
			return contents.getValue();
		}
	}

	public void setContents(String str) {
		// FIXME: remove this once all entities have been migrated to zipped
		setZipped(true);
		if (isZipped()) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream(str.length());
			try {
				GZIPOutputStream zipOutputStream = new GZIPOutputStream(baos);
				zipOutputStream.write(str.getBytes());
				zipOutputStream.flush();
				zipOutputStream.close();
			} catch (IOException e) {
				logger.severe(e.getMessage());
			}
			byte[] encodeBase64 = Base64.encodeBase64(baos.toByteArray());
			this.contents = new Text(new String(encodeBase64));
		} else {
			this.contents = new Text(str);
		}
	}

	public void setStudyName(String studyName) {
		this.studyName = studyName;
	}

	public String getStudyName() {
		return studyName;
	}
}
