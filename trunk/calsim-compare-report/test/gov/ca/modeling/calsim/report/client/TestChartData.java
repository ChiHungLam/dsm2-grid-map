package gov.ca.modeling.calsim.report.client;

import com.google.gwt.junit.client.GWTTestCase;

public class TestChartData extends GWTTestCase {

	@Override
	public String getModuleName() {
		return "gov.ca.modeling.calsim.report.Test";
	}

	public void testParse() {
		String jsonText = "{\"title\": \"T\", \"series_names\": [\"1\",\"2\",],\"yaxis_name\": \"Y\",\"xaxis_name\": \"X\", \"values\": [[new Date(1918,1,1),62.029896,65.574287],[61015186800000,62.029896,65.574287]]}";
	}
}
