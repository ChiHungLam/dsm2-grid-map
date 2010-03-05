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

import gov.ca.bdo.modeling.dsm2.map.client.service.DSM2InputService;
import gov.ca.bdo.modeling.dsm2.map.server.data.DSM2ModelFile;
import gov.ca.bdo.modeling.dsm2.map.server.persistence.DSM2ModelFileDAOImpl;
import gov.ca.bdo.modeling.dsm2.map.server.utils.PMF;
import gov.ca.dsm2.input.model.DSM2Model;
import gov.ca.dsm2.input.parser.InputTable;
import gov.ca.dsm2.input.parser.Parser;
import gov.ca.dsm2.input.parser.Tables;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import javax.jdo.PersistenceManager;

import org.apache.tools.ant.filters.StringInputStream;

import com.google.appengine.api.datastore.Text;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class DSM2InputServiceImpl extends RemoteServiceServlet implements
		DSM2InputService {

	public DSM2Model getInputModel(String studyName) {
		Parser inputParser = new Parser();
		String hydro_echo_inp = "";
		String gis_inp = "";
		try {
			PersistenceManager persistenceManager = PMF.get()
					.getPersistenceManager();
			try {
				DSM2ModelFileDAOImpl dao = new DSM2ModelFileDAOImpl(
						persistenceManager);
				List<DSM2ModelFile> filesForStudy = dao
						.getFilesForStudy(studyName);
				if (filesForStudy.size() == 0) {
					return null;
				}
				for (DSM2ModelFile dsm2ModelFile : filesForStudy) {
					if (dsm2ModelFile.getName().equals("hydro_echo_inp")) {
						hydro_echo_inp = dsm2ModelFile.getContents().getValue();
					}
					if (dsm2ModelFile.getName().equals("gis_inp")) {
						gis_inp = dsm2ModelFile.getContents().getValue();
					}
				}
			} finally {
				persistenceManager.close();
			}
			InputStream inputStream = new StringInputStream(hydro_echo_inp);
			Tables model = inputParser.parseModel(inputStream);
			inputParser.parseAndAddToModel(model,
					new StringInputStream(gis_inp));
			return model.toDSM2Model();
		} catch (Exception ex) {
			ex.printStackTrace();
			return new DSM2Model();
		}
	}

	public void saveModel(String studyName, DSM2Model model) {
		Parser inputParser = new Parser();
		String hydro_echo_inp = "";
		String gis_inp = "";
		PersistenceManager persistenceManager = PMF.get()
				.getPersistenceManager();
		try {
			DSM2ModelFileDAOImpl dao = new DSM2ModelFileDAOImpl(
					persistenceManager);
			List<DSM2ModelFile> filesForStudy = dao.getFilesForStudy(studyName);
			if (filesForStudy.size() == 0) {
				return;
			}
			for (DSM2ModelFile dsm2ModelFile : filesForStudy) {
				if (dsm2ModelFile.getName().equals("hydro_echo_inp")) {
					hydro_echo_inp = dsm2ModelFile.getContents().getValue();
				}
				if (dsm2ModelFile.getName().equals("gis_inp")) {
					gis_inp = dsm2ModelFile.getContents().getValue();
				}
			}
			InputStream inputStream = new StringInputStream(hydro_echo_inp);
			Tables tables = inputParser.parseModel(inputStream);
			inputParser.parseAndAddToModel(tables, new StringInputStream(
					gis_inp));
			tables.fromDSM2Model(model);
			//
			StringBuilder hydro_echo_inp_builder = new StringBuilder();
			StringBuilder gis_inp_builder = new StringBuilder();
			for (InputTable table : tables.getTables()) {
				if (table.getName().contains("GIS")) {
					gis_inp_builder.append(table.toStringRepresentation());
				} else {
					hydro_echo_inp_builder.append(table
							.toStringRepresentation());
				}
			}
			for (DSM2ModelFile dsm2ModelFile : filesForStudy) {
				if (dsm2ModelFile.getName().equals("hydro_echo_inp")) {
					dsm2ModelFile.setContents(new Text(hydro_echo_inp_builder
							.toString()));
				}
				if (dsm2ModelFile.getName().equals("gis_inp")) {
					dsm2ModelFile.setContents(new Text(gis_inp_builder
							.toString()));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			persistenceManager.close();
		}
	}

	public String[] getStudyNames() {
		PersistenceManager persistenceManager = PMF.get()
				.getPersistenceManager();
		try {
			DSM2ModelFileDAOImpl dao = new DSM2ModelFileDAOImpl(
					persistenceManager);
			Collection<String> studyNames = dao.getStudyNamesForCurrentUser();
			String[] studyNamesArray = new String[studyNames.size()];
			return studyNames.toArray(studyNamesArray);
		} catch (Exception e) {
			e.printStackTrace();
			return new String[0];
		} finally {
			persistenceManager.close();
		}
	}

	public void removeStudy(String studyName) {
		PersistenceManager persistenceManager = PMF.get()
				.getPersistenceManager();
		try {
			DSM2ModelFileDAOImpl dao = new DSM2ModelFileDAOImpl(
					persistenceManager);
			List<DSM2ModelFile> filesForStudy = dao.getFilesForStudy(studyName);
			if (filesForStudy.size() == 0) {
				return;
			}
			for (DSM2ModelFile dsm2ModelFile : filesForStudy) {
				dao.deleteObject(dsm2ModelFile);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			persistenceManager.close();
		}
	}

	public String showInput(String studyName, String inputName) {
		PersistenceManager persistenceManager = PMF.get()
				.getPersistenceManager();
		try {
			DSM2ModelFileDAOImpl dao = new DSM2ModelFileDAOImpl(
					persistenceManager);
			List<DSM2ModelFile> filesForStudy = dao.getFilesForStudy(studyName);
			if (filesForStudy.size() == 0) {
				return "";
			}
			for (DSM2ModelFile dsm2ModelFile : filesForStudy) {
				if (dsm2ModelFile.getName().equals(inputName)) {
					return dsm2ModelFile.getContents().getValue();
				}
			}
			return "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		} finally {
			persistenceManager.close();
		}
	}

	public String showGISInput(String studyName) {
		return showInput(studyName, "gis_inp");
	}

	public String showHydroInput(String studyName) {
		return showInput(studyName, "hydro_echo_inp");
	}
}