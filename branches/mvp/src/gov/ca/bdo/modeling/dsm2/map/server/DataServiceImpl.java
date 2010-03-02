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
package gov.ca.bdo.modeling.dsm2.map.server;

import gov.ca.bdo.modeling.dsm2.map.client.map.TextAnnotation;
import gov.ca.bdo.modeling.dsm2.map.client.model.RegularTimeSeries;
import gov.ca.bdo.modeling.dsm2.map.client.service.DataService;
import gov.ca.bdo.modeling.dsm2.map.server.data.DataFile;
import gov.ca.bdo.modeling.dsm2.map.server.persistence.DataFileDAO;
import gov.ca.bdo.modeling.dsm2.map.server.persistence.DataFileDAOImpl;
import gov.ca.bdo.modeling.dsm2.map.server.utils.PMF;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;

import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class DataServiceImpl extends RemoteServiceServlet implements
		DataService {

	public List<RegularTimeSeries> getRegularTimeSeries(String studyName,
			String name, String[] variables) {
		ArrayList<RegularTimeSeries> list = new ArrayList<RegularTimeSeries>();
		PersistenceManager persistenceManager = PMF.get()
				.getPersistenceManager();
		try {
			DataFileDAO dao = new DataFileDAOImpl(persistenceManager);
			List<DataFile> filesForStudyAndName = dao.getFilesForStudyAndName(
					studyName, name);
			if ((filesForStudyAndName == null)
					|| (filesForStudyAndName.size() == 0)) {
				return list;
			}
			for (DataFile dataFile : filesForStudyAndName) {
				RegularTimeSeries timeSeries = new RegularTimeSeries();
				String contents = dataFile.getContents().getValue();
				timeSeries.setName(dataFile.getName());
				timeSeries.setType(dataFile.getType());
				timeSeries.setInterval("1DAY");
				BufferedReader reader = new BufferedReader(new StringReader(
						contents));
				String line = null;
				double[] data = null;
				int i = 0;
				while ((line = reader.readLine()) != null) {
					line = line.trim();
					if (line.startsWith("startTime")) {
						String startTimeStr = line.split("=")[1];
						Date date = new Date(Long.parseLong(startTimeStr));
						timeSeries.setStartTime(date);
					} else if (line.startsWith("interval")) {
						timeSeries.setInterval(line.split("=")[1]);
					} else if (line.startsWith("count")) {
						data = new double[Integer.parseInt(line.split("=")[1])];
					} else {
						data[i++] = Double.parseDouble(line);
					}
				}
				timeSeries.setData(data);
				list.add(timeSeries);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			persistenceManager.close();
		}
		return list;
	}

	public List<TextAnnotation> getNotes(String studyName) {
		ArrayList<TextAnnotation> list = new ArrayList<TextAnnotation>();
		PersistenceManager persistenceManager = PMF.get()
				.getPersistenceManager();
		try {
			DataFileDAO dao = new DataFileDAOImpl(persistenceManager);
			List<DataFile> filesForStudyAndName = dao.getFilesForStudyAndName(
					studyName, "text.annotations");
			if ((filesForStudyAndName == null)
					|| (filesForStudyAndName.size() == 0)) {
				return list;
			} else {
				DataFile dataFile = filesForStudyAndName.get(0);
				String value = dataFile.getContents().getValue();
				String[] lines = value.split("\n");
				for (String line : lines) {
					String[] fields = line.split(",");
					TextAnnotation annotation = new TextAnnotation();
					annotation.setLatitude(Double.parseDouble(fields[0]));
					annotation.setLongitude(Double.parseDouble(fields[1]));
					String text = "";
					if (fields.length > 2) {
						for (int k = 2; k < fields.length; k++) {
							text += fields[k] + (k > 2 ? "," : "");
						}
					}
					annotation.setText(text);
					list.add(annotation);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		} finally {
			persistenceManager.close();
		}
		return list;
	}

	public void saveNotes(String studyName, List<TextAnnotation> annotations) {
		PersistenceManager persistenceManager = PMF.get()
				.getPersistenceManager();
		DataFile annotationFile = null;
		try {
			DataFileDAO dao = new DataFileDAOImpl(persistenceManager);
			List<DataFile> filesForStudyAndName = dao.getFilesForStudyAndName(
					studyName, "text.annotations");
			StringBuilder builder = new StringBuilder();
			for (TextAnnotation textAnnotation : annotations) {
				builder.append(textAnnotation.getLatitude()).append(",");
				builder.append(textAnnotation.getLongitude()).append(",");
				builder.append(textAnnotation.getText()).append("\n");
			}
			if ((filesForStudyAndName == null)
					|| (filesForStudyAndName.size() == 0)) {
				annotationFile = new DataFile();
				annotationFile.setOwnerName(UserServiceFactory.getUserService()
						.getCurrentUser().getEmail());
				annotationFile.setStudyName(studyName);
				annotationFile.setType("text.annotations");
				annotationFile.setName("text.annotations");
				annotationFile.setContents(new Text(builder.toString()));
				dao.createObject(annotationFile);
			} else {
				annotationFile = filesForStudyAndName.get(0);
				annotationFile.setContents(new Text(builder.toString()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		} finally {
			persistenceManager.close();
		}
	}

}
