package gov.ca.dsm2.input.parser;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Converts the text representation of tables into objects containing the same
 * information.
 * 
 * @see Tables
 * @author nsandhu
 * 
 */
public class Parser {

	public Parser() {
	}

	public Tables parseModel(String file) throws IOException {
		return parseModel(new FileInputStream(file));
	}

	public Tables parseModel(InputStream inputStream) throws IOException {
		Tables tables = new Tables();
		parseAndAddToModel(tables, inputStream);
		return tables;
	}

	public void parseAndAddToModel(Tables tables, InputStream inputStream)
			throws IOException {
		LineNumberReader reader = new LineNumberReader(new InputStreamReader(
				inputStream));
		InputTable table = null;
		try {
			do {
				table = parseTable(reader);
				if (table == null) {
					break;
				}
				tables.addTable(table);
			} while (true);
		} catch (IOException e) {
			if (!(e instanceof EOFException)) {
				throw e;
			}
		} finally {
			reader.close();
		}
	}

	/**
	 * @param reader
	 * @return
	 * @throws IOException
	 */
	public InputTable parseTable(LineNumberReader reader) throws IOException {
		String line = nextLine(reader);
		InputTable table = new InputTable();
		table.setName(line);
		line = nextLine(reader);
		table.setHeaders(getFields(line));
		line = nextLine(reader);
		ArrayList<ArrayList<String>> values = new ArrayList<ArrayList<String>>();
		while (!line.equals("END")) {
			values.add(getFields(line));
			line = nextLine(reader);
		}
		table.setValues(values);
		return table;
	}

	String nextLine(LineNumberReader reader) throws IOException {
		String line = null;
		do {
			line = reader.readLine();
			if (line != null) {
				line = line.trim();
			} else {
				throw new EOFException("No more lines");
			}
		} while (line.equals("") || line.startsWith("#"));
		line = stripComment(line);
		return line;
	}

	String stripComment(String text) {
		int index = text.indexOf("#");
		if (index < 0) {
			return text;
		} else {
			return text.substring(0, index);
		}
	}

	ArrayList<String> getFields(String text) {
		String[] fields = text.split("(\\s)+");
		ArrayList<String> fieldList = new ArrayList<String>();
		fieldList.addAll(Arrays.asList(fields));
		return fieldList;
	}
}
