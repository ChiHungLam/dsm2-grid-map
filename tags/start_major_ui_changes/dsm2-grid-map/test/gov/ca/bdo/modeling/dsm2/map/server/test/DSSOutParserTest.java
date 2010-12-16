package gov.ca.bdo.modeling.dsm2.map.server.test;

import gov.ca.bdo.modeling.dsm2.map.client.model.RegularTimeSeries;
import gov.ca.bdo.modeling.dsm2.map.server.DSSOutParser;

import java.io.FileInputStream;

import junit.framework.TestCase;

public class DSSOutParserTest extends TestCase {

	public void testParser() throws Exception {
		FileInputStream fis = new FileInputStream(
				"resources/sample_data/sample1/output_data.dssout");
		DSSOutParser parser = new DSSOutParser(fis);
		RegularTimeSeries regularTimeSeries = parser.nextSeries();
		assertNotNull(regularTimeSeries);
		RegularTimeSeries regularTimeSeries2 = parser.nextSeries();
		assertNotNull(regularTimeSeries2);
	}
}
