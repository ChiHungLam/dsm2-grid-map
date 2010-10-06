package gov.ca.maps.bathymetry.tiles.server;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.urlfetch.FetchOptions;
import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;

@SuppressWarnings("serial")
public class ProxyServlet extends HttpServlet {
	private String proxyURL;
	private String proxyModuleBase;

	public ProxyServlet() {

	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		URLFetchService urlFetchService = URLFetchServiceFactory
				.getURLFetchService();
		URL url = new URL(proxyURL);
		HTTPMethod method = HTTPMethod.POST;
		FetchOptions fetchOptions = FetchOptions.Builder.followRedirects();
		HTTPRequest proxyRequest = new HTTPRequest(url, method, fetchOptions);
		proxyRequest.addHeader(new HTTPHeader("Content-Type",
				"text/x-gwt-rpc; charset=utf-8"));
		proxyRequest.addHeader(new HTTPHeader("X-GWT-Module-Base",
				proxyModuleBase));
		byte[] payload = new byte[request.getContentLength()];
		request.getInputStream().read(payload);
		String payloadStr = new String(payload);
		String moduleBase = payloadStr.split("\\|")[3];
		payloadStr = payloadStr.replace(moduleBase, proxyModuleBase);
		proxyRequest.setPayload(payloadStr.getBytes());
		int retryCount = 0;
		while (retryCount < 2) {
			try {
				HTTPResponse proxyResponse = urlFetchService
						.fetch(proxyRequest);
				byte[] content = proxyResponse.getContent();
				List<HTTPHeader> headers = proxyResponse.getHeaders();
				for (HTTPHeader header : headers) {
					response.addHeader(header.getName(), header.getValue());
				}
				int responseCode = proxyResponse.getResponseCode();
				response.setStatus(responseCode);
				response.setContentLength(content.length);
				response.getOutputStream().write(content);
				response.flushBuffer();
				retryCount = 2;
			} catch (IOException ex) {
				retryCount++;
			}
		}
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		proxyURL = config.getInitParameter("proxyURL");
		if (proxyURL == null) {
			proxyURL = "http://dsm2grid.appspot.com/dsm2_grid_map/dem";
		}

		proxyModuleBase = config.getInitParameter("proxyModuleBase");
		if (proxyModuleBase == null) {
			proxyModuleBase = "http://dsm2grid.appspot.com/dsm2_grid_map/";
		}
		super.init(config);
	}

}