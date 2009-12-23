package gov.ca.bdo.modeling.dsm2.map.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

import com.google.appengine.api.datastore.Text;

public class DataFileUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 8367618333138027430L;
	private static final int MAX_SIZE_BYTES = 1024 * 1024;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String study = req.getParameter("study");
		String name = req.getParameter("name");
		String type = req.getParameter("type");
		resp.getWriter().println(
				"Got request for study: " + study + " name: " + name
						+ " type: " + type);
	}

	@SuppressWarnings( { "deprecation" })
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		PrintWriter out = resp.getWriter();
		String studyName = null;
		ServletFileUpload upload = new ServletFileUpload();
		upload.setSizeMax(MAX_SIZE_BYTES);
		upload.setHeaderEncoding("UTF-8");
		PersistenceManager persistenceManager = PMF.get()
				.getPersistenceManager();
		try {
			// Parse the request
			FileItemIterator iterator = upload.getItemIterator(req);

			while (iterator.hasNext()) {
				FileItemStream item = iterator.next();
				InputStream inputStream = item.openStream();
				if (item.isFormField()) {
					if (item.getFieldName().equals("studyName")) {
						studyName = Streams.asString(inputStream);
					}
				} else {
					if (item.getFieldName().equals("dataFile")) {
						if ((studyName == null) || studyName.equals("")) {
							return;
						}
						saveData(persistenceManager, studyName, inputStream);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			persistenceManager.close();
		}
		// Return result
		String resultUrl = "/dsm2_grid_map.html";
		resp.sendRedirect(resultUrl + "#study=" + URLEncoder.encode(studyName));
		out.close();
	}

	public void saveData(PersistenceManager persistenceManager,
			String studyName, InputStream fileAsStream) throws Exception {

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				fileAsStream));
		String line = null;
		ArrayList<DataFile> files = new ArrayList<DataFile>();
		ArrayList<StringBuilder> fileContents = new ArrayList<StringBuilder>();
		int count = 0;
		String startTime = "";
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			if (line.startsWith("start_time")) {
				startTime = line.split("=")[1];
			} else if (line.startsWith("interval")) {
			} else if (line.contains("|")) {
				String[] dataSets = line.split(",");
				for (int i = 0; i < dataSets.length; i++) {
					String[] fields = dataSets[i].split("\\|");
					DataFile file = new DataFile();
					file.setStudyName(studyName);
					file.setOwnerName(Utils.getCurrentUserEmail());
					file.setName(fields[0].toLowerCase());
					file.setType(fields[1].toLowerCase());
					files.add(file);
					fileContents.add(new StringBuilder());
				}
			} else {
				String[] data = line.split(",");
				if (data.length == 0) {
					continue;
				}
				if (data.length != fileContents.size()) {
					System.err
							.println("Data length is " + data.length
									+ " and fileContents size is"
									+ fileContents.size());
					System.err.println("Line is " + line);
				}
				count++;
				for (int i = 0; i < data.length; i++) {
					fileContents.get(i).append(data[i]).append("\n");
				}
			}
		}
		for (int i = 0; i < files.size(); i++) {
			fileContents.get(i).insert(0, "startTime=" + startTime + "\n");
			fileContents.get(i).insert(0, "count=" + count + "\n");
			Text contents = new Text(fileContents.get(i).toString());
			files.get(i).setContents(contents);
		}
		DataFileDAO dao = new DataFileDAOImpl(persistenceManager);
		List<DataFile> filesForStudy = dao.getFilesForStudy(studyName);
		if ((filesForStudy != null) && (filesForStudy.size() > 0)) {
			for (DataFile dataFile : filesForStudy) {
				dao.deleteObject(dataFile);
			}
		}
		for (DataFile dataFile : files) {
			dao.createObject(dataFile);
		}

	}
}
