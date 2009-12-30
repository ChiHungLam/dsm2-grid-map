package gov.ca.bdo.modeling.dsm2.map.server;

import gov.ca.bdo.modeling.dsm2.map.client.model.RegularTimeSeries;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Parses data in dss out format. (Obtained from HEC-DSSVue v 2.0 using export
 * to text option) Assumes that all data is regular time series
 * 
 * @author psandhu
 * 
 */
public class DSSOutParser {
	private final BufferedReader reader;

	public DSSOutParser(InputStream inputStream) {
		reader = new BufferedReader(new InputStreamReader(inputStream));

	}

	public RegularTimeSeries nextSeries() throws Exception {
		String line = null;
		RegularTimeSeries series = null;
		double[] data = null;
		int index = 0;
		while (((line = reader.readLine()) != null) && !line.contains("END")) {
			if (series == null) {
				series = new RegularTimeSeries();
			}
			if (line.startsWith("/")) {
				// parsePathname(line);
				String[] fields = line.split("/");
				series.setName(fields[2]);
				series.setType(fields[3]);
				series.setInterval("1DAY");
			} else if (line.startsWith("RTS")) {
				// parse type & meta data
			} else if (line.startsWith("Start:")) {
				// parse start time, number of data points
				int indexOfNumber = line.indexOf("Number:");
				int ndata = Integer.parseInt(line.substring(indexOfNumber + 7)
						.trim());
				data = new double[ndata];
			} else if ((line.startsWith("Units:"))) {
				String units = line.split("Type:")[0].split("Units:")[1].trim();
				series.setUnits(units);
				// parse units, sampling type
			} else {
				// parse data values
				int semiColonIndex = line.indexOf(";");
				if (index == 0) {
					String timeStr = line.substring(0, semiColonIndex);
					timeStr = timeStr.replace(", ", " ").trim();
					SimpleDateFormat format = new SimpleDateFormat(
							"ddMMMyyyy HHmm");
					Date date = format.parse(timeStr);
					series.setStartTime(date);
				}
				data[index++] = Double.parseDouble(line.substring(
						semiColonIndex + 1).trim());
			}
		}
		if (series != null) {
			series.setData(data);
		}
		return series;
	}
}
