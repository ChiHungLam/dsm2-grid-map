package gov.ca.maps.bathymetry.tiles.server;

import gov.ca.maps.bathymetry.tiles.client.model.BathymetryDataPoint;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.google.appengine.api.datastore.Blob;

@SuppressWarnings("serial")
public class BathymetryDataFileUploadServlet extends HttpServlet {
	private static final int MAX_SIZE_BYTES = 1024 * 1024 * 10;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String xStr = req.getParameter("latitude");
		String yStr = req.getParameter("longitude");
		double latitude = Double.parseDouble(xStr);
		double longitude = Double.parseDouble(yStr);
		PersistenceManager persistenceManager = PMF.get()
				.getPersistenceManager();
		try {
			BathymetryDataFileDAO dao = new BathymetryDataFileDAOImpl(
					persistenceManager);
			BathymetryDataFile bathymetryDataFile = dao.getFileForLocation(
					latitude, longitude);
			if (bathymetryDataFile != null) {
				resp.getWriter().println(
						"Bathymetry Data File: "
								+ bathymetryDataFile.getLatitude100() + ","
								+ bathymetryDataFile.getLongitude100());
				DataInputStream dis = new DataInputStream(
						new ByteArrayInputStream(bathymetryDataFile
								.getContents().getBytes()));
				while (dis.available() > 0) {
					BathymetryDataPoint dataPoint = readBathymetryDataPoint(dis);
					resp.getWriter().println(dataPoint.toString());
				}
			} else {
				resp.getWriter().println("No data found!");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			persistenceManager.close();
		}
	}

	private BathymetryDataPoint readBathymetryDataPoint(DataInputStream dis) {
		BathymetryDataPoint data = new BathymetryDataPoint();
		try {
			data.latitude = dis.readDouble();
			data.longitude = dis.readDouble();
			data.elevation = dis.readDouble();
			data.year = dis.readInt();
			data.agency = dis.readUTF();
		} catch (IOException ex) {
			//
		}
		return data;
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		PrintWriter out = resp.getWriter();
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
					}
				} else {
					if (item.getFieldName().equals("bathymetryFile")) {
						saveData(persistenceManager, inputStream);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			persistenceManager.close();
		}
		// Return result
		String resultUrl = "upload_bathymetry.html";
		resp.sendRedirect(resultUrl);
		out.close();
	}

	public void saveData(PersistenceManager persistenceManager,
			InputStream fileAsStream) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				fileAsStream));
		String line = null;
		HashMap<String, ByteArrayOutputStream> fileMap = new HashMap<String, ByteArrayOutputStream>();
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			String[] fields = line.split(",");
			if (fields.length < 5) {
				System.err
						.println("Line: "
								+ line
								+ " has less than required 4 fields: lat, lon, depth, year, agency");
				continue;
			}
			double latitude = Double.parseDouble(fields[0]);
			double longitude = Double.parseDouble(fields[1]);
			int x = (int) Math.floor(latitude * BathymetryDataFile.FACTOR);
			int y = (int) Math.floor(longitude * BathymetryDataFile.FACTOR);

			ByteArrayOutputStream baos = fileMap.get(x + "_" + y);
			if (baos == null) {
				baos = new ByteArrayOutputStream();
				fileMap.put(x + "_" + y, baos);
			}
			DataOutputStream dos = new DataOutputStream(baos);
			dos.writeDouble(latitude);
			dos.writeDouble(longitude);
			dos.writeDouble(Double.parseDouble(fields[2]));
			dos.writeInt(Integer.parseInt(fields[3]));
			dos.writeUTF(fields[4]);
		}
		BathymetryDataFileDAO dao = new BathymetryDataFileDAOImpl(
				persistenceManager);
		for (String key : fileMap.keySet()) {
			String[] fields = key.split("_");
			double x = Double.parseDouble(fields[0])
					/ BathymetryDataFile.FACTOR;
			double y = Double.parseDouble(fields[1])
					/ BathymetryDataFile.FACTOR;
			ByteArrayOutputStream baos = fileMap.get(key);
			BathymetryDataFile bathymetryDataFile = dao
					.getFileForLocation(x, y);
			if (bathymetryDataFile == null) {
				bathymetryDataFile = new BathymetryDataFile();
				bathymetryDataFile.setLatitude100((int) Math.floor(x
						* BathymetryDataFile.FACTOR));
				bathymetryDataFile.setLongitude100((int) Math.floor(y
						* BathymetryDataFile.FACTOR));
				bathymetryDataFile.setContents(new Blob(new byte[0]));
				dao.createObject(bathymetryDataFile);
			}
			boolean append = false;
			byte[] data = baos.toByteArray();
			if (append) {
				byte[] existing = bathymetryDataFile.getContents().getBytes();
				byte[] newdata = baos.toByteArray();
				data = new byte[existing.length + newdata.length];
				System.arraycopy(existing, 0, data, 0, existing.length);
				System.arraycopy(newdata, 0, data, existing.length,
						newdata.length);
			}
			bathymetryDataFile.setContents(new Blob(data));
			dao.updateObject(bathymetryDataFile);
		}
	}
}
