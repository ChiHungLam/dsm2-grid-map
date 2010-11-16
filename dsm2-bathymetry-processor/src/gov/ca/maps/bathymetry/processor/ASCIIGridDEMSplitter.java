package gov.ca.maps.bathymetry.processor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class ASCIIGridDEMSplitter {
	private static final int SPLIT_FACTOR = 100;
	private final HashMap<String, int[]> nameToValueMap;
	private Connection conn;
	private BASE64Encoder base64Encoder = new BASE64Encoder();
	private BASE64Decoder base64Decoder = new BASE64Decoder();

	public static void main(String[] args) {
		if (args.length < 2) {
			System.out
					.println("Usage: ASCIIGridDEMSplitter <DEM file> <output_db_filename> <csv_filename>");
			System.exit(2);
		}
		ASCIIGridDEMSplitter tiler = new ASCIIGridDEMSplitter(args[0], args[1]);
		try {
			if (args.length == 2) {
				tiler.split();
			}
			if (args.length == 3) {
				tiler.dump(args[2]);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private File inFile;
	private String directory;
	private PreparedStatement insertStatement;
	private PreparedStatement updateStatement;
	private PreparedStatement selectStatement;
	private PreparedStatement deleteStatement;
	private String dbFile;

	public ASCIIGridDEMSplitter(String inFileName, String dbFile) {
		inFile = new File(inFileName);
		this.dbFile = new File(inFile.getParent(), dbFile).getPath();
		if (!inFile.exists() || !inFile.isFile()) {
			throw new IllegalArgumentException(inFile
					+ " either doesn't exist or is not a file");
		}
		nameToValueMap = new HashMap<String, int[]>();
	}

	private void dump(String outFile) throws Exception {
		outFile = new File(inFile.getParent(), outFile).getPath();
		Class.forName("org.sqlite.JDBC");
		conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
		conn.setAutoCommit(false);
		Statement statement = conn.createStatement();
		ResultSet resultSet = statement
				.executeQuery("select name,contents from filenames");
		PrintWriter pw = new PrintWriter(outFile);
		pw.println("y,x,contents");
		while (resultSet.next()) {
			String xy = resultSet.getString(1);
			String contents = resultSet.getString(2);
			String[] fields = xy.split("_");
			pw.print(fields[1]);
			pw.print(",");
			pw.print(fields[0]);
			pw.print(",");
			pw.println(contents);
		}
		pw.close();
		statement.close();
		conn.close();
	}

	private void initializeNameDB() {
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
			conn.setAutoCommit(false);
			Statement statement = conn.createStatement();
			statement.executeUpdate("drop table if exists filenames");
			statement
					.executeUpdate("create table filenames(name text,contents text)");
			statement
					.executeUpdate("create unique index if not exists name_index on filenames(name)");
			conn.commit();
			insertStatement = conn
					.prepareStatement("insert into filenames(name, contents) values(?,?)");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void closeDB() {
		try {
			updateStatement.close();
			insertStatement.close();
			conn.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void addFilenameToDB(String filename, int[] values) {
		try {
			insertStatement.setString(1, filename);
			insertStatement.setString(2, encode(values));
			insertStatement.addBatch();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private String encode(int[] values) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			for (int value : values) {
				dos.writeInt(value);
			}
			baos.close();
			String val = base64Encoder.encode(baos.toByteArray());
			return val.replace("\r\n", "");
		} catch (Exception ex) {
			ex.printStackTrace();
			return "";
		}
	}

	private int[] decode(String value) {
		int[] values = initializeNew();
		try {
			byte[] buffer = base64Decoder.decodeBuffer(value);
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(
					buffer));
			int i = 0;
			while (dis.available() > 0) {
				values[i++] = dis.readInt();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return values;
	}

	public void split() throws IOException {
		initializeNameDB();
		BufferedReader reader = new BufferedReader(new FileReader(inFile));
		String line = reader.readLine();
		int count = 0;
		int ncols = 0;
		int nrows = 0;
		double xllcorner = 0;
		double yllcorner = 0;
		double cellsize = 0;
		int nodataValue = 0;
		while (count < 6) {
			String[] fields = line.split("\\s+");
			if (fields.length != 2) {
				continue;
			}
			if (fields[0].equalsIgnoreCase("ncols")) {
				ncols = Integer.parseInt(fields[1]);
			} else if (fields[0].equalsIgnoreCase("nrows")) {
				nrows = Integer.parseInt(fields[1]);
			} else if (fields[0].equalsIgnoreCase("xllcorner")) {
				xllcorner = Double.parseDouble(fields[1]);
			} else if (fields[0].equalsIgnoreCase("yllcorner")) {
				yllcorner = Double.parseDouble(fields[1]);
			} else if (fields[0].equalsIgnoreCase("cellsize")) {
				cellsize = Double.parseDouble(fields[1]);
			} else if (fields[0].equalsIgnoreCase("nodata_value")) {
				nodataValue = Integer.parseInt(fields[1]);
			}
			line = reader.readLine();
			count++;
		}
		int pct = nrows / 100;
		for (int i = 0; i < nrows; i++) {
			String[] fields = line.split("\\s");
			if (i % pct == 0) {
				System.out.println("Processed " + i + " of " + nrows
						+ " rows from file " + inFile);
			}
			for (int j = 0; j < fields.length; j++) {
				double x = xllcorner + j * cellsize;
				double y = yllcorner + (nrows - i) * cellsize;
				int depth = 0;
				// int rawDepth = Integer.parseInt(fields[j]);
				// ASCII Grid values in meters in float format while output is
				// 10ths of feet in integer format
				float rawDepth = Float.parseFloat(fields[j]);
				if (Math.abs(rawDepth - nodataValue) <= 1e-5) {
					depth = -9999;
				} else {
					depth = (int) Math.round(10 * rawDepth / 0.3048);
				}
				appendToFile(x, y, depth);
			}
			line = reader.readLine();
		}
		closeAll();
		nameToValueMap.clear();
		reader.close();
		closeDB();
	}

	private void appendToFile(double x, double y, int depth) throws IOException {
		String file = getSplitFilename(x, y);
		int xo = offSet(x);
		int yo = offSet(y);
		int[] values = nameToValueMap.get(file);
		if (values == null) {
			values = getOrInsertDB(file);
			nameToValueMap.put(file, values);
		}
		values[10 * yo + xo] = depth;
		if (nameToValueMap.size() > 10000) {
			closeAll();
			nameToValueMap.clear();
		}
	}

	private int[] initializeNew() {
		int[] values;
		values = new int[100];
		for (int i = 0; i < 100; i++) {
			values[i] = -9999;
		}
		return values;
	}

	private int[] getOrInsertDB(String file) {
		int[] values = null;
		try {
			if (selectStatement == null) {
				selectStatement = conn
						.prepareStatement("select contents from filenames where name=?");
			}
			selectStatement.setString(1, file);
			ResultSet resultSet = selectStatement.executeQuery();
			if (resultSet.next()) {
				String contents = resultSet.getString(1);
				values = decode(contents);
			} else {
				values = initializeNew();
				addFilenameToDB(file, values);
			}
		} catch (SQLException ex) {
			values = initializeNew();
			addFilenameToDB(file, values);
		}
		return values;
	}

	private void updateDB(String filename, String contents) {
		try {
			if (updateStatement == null) {
				updateStatement = conn
						.prepareStatement("update filenames set contents = ? where name = ?");
			}
			updateStatement.setString(1, contents);
			updateStatement.setString(2, filename);
			updateStatement.addBatch();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeToFile(String filename, int[] values) {
		try {
			if (allNoDataValues(values)) {
				if (deleteStatement == null) {
					deleteStatement = conn
							.prepareStatement("delete from filenames where name=?");
				}
				deleteStatement.setString(1, filename);
				deleteStatement.addBatch();
				return;
			}
			updateDB(filename, encode(values));
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
		}
	}

	private boolean allNoDataValues(int[] values) {
		for (int value : values) {
			if (value != -9999) {
				return false;
			}
		}
		return true;
	}

	private String getSplitFilename(double x, double y) {
		long x0 = roundDown(x);
		long y0 = roundDown(y);
		return x0 + "_" + y0;
	}

	private long roundDown(double val) {
		return (long) Math.floor(val / SPLIT_FACTOR) * SPLIT_FACTOR;
	}

	private int offSet(double val) {
		return (int) (val - roundDown(val) - 5) / 10;
	}

	private void closeAll() {
		for (String filename : nameToValueMap.keySet()) {
			writeToFile(filename, nameToValueMap.get(filename));
		}
		try {
			System.out.println("Committing results");
			if (insertStatement != null) {
				insertStatement.executeBatch();
			}
			if (deleteStatement != null) {
				deleteStatement.executeBatch();
			}
			if (updateStatement != null) {
				updateStatement.executeBatch();
			}
			conn.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
