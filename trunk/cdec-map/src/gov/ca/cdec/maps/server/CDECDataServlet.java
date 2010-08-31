package gov.ca.cdec.maps.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class CDECDataServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String requestPath = req.getRequestURI();
		String cdecUrl = "http://cdec.water.ca.gov" + requestPath + "?"
				+ req.getQueryString();
		URL url = new URL(cdecUrl);
		BufferedReader reader = new BufferedReader(new InputStreamReader(url
				.openStream()));
		StringBuffer buffer = new StringBuffer();
		String line = null;
		int ncolumnInner = 0;
		while ((line = reader.readLine()) != null) {
			if (line.contains("<div class=\"column_inner\"")) {
				ncolumnInner++;
			}
			if (ncolumnInner == 2) {
				if (line.contains("</div>")) {
					break;
				}
				if (line.contains("href")) {
					int index = line.indexOf("href");
					line = line.substring(0, index)
							+ " target=\"external_page\" "
							+ line.substring(index);
				}
				if (line.contains("http://cdec.water.ca.gov:80")) {
					line = line.replace("http://cdec.water.ca.gov:80/", "/");
				}
				if (line.contains("href=")) {
					int matchLength = 6;
					int index = line.indexOf("href=\"");
					if (index == -1) {
						matchLength = 5;
						index = line.indexOf("href=");
					}
					line = line.substring(0, index + matchLength)
							+ "http://cdec.water.ca.gov"
							+ line.substring(index + matchLength);
				}
				buffer.append(line);
			} else {
				continue;
			}
		}
		resp.getWriter().write(buffer.toString());
		resp.getWriter().flush();
		reader.close();
	}

}
