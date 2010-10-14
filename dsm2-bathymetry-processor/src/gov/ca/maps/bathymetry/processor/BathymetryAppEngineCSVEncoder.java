package gov.ca.maps.bathymetry.processor;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import sun.misc.BASE64Encoder;

public class BathymetryAppEngineCSVEncoder {
	public static BASE64Encoder base64Encoder = new BASE64Encoder();
	public static String LINE_SEPARATOR = System.getProperty("line.separator");
	private static Connection conn;
	private static PreparedStatement statement;

	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.out
					.println("Usage: BathymetryAppEngineCSVEncoder <directory> <out_file> [<in_file>]");
			System.out
					.println(" <directory> contains .csv files containing the data");
			System.out
					.println(" <out_file> is the name of .csv file which will contain the transformed data");
			System.out
					.println(" <in_file> is the optional argument of a .csv file containing key for existing x and y values");
			System.exit(1);
		}
		if (args.length == 3) {
			buildKeyDB(args[2]);
			statement = conn.prepareStatement("select id from bathymetrydatafile where x=? and y=?");
		}
		PrintWriter pw_noKey = null;
		PrintWriter pw_key = null;
		File dir = new File(args[0]);
		if (dir.exists() && dir.isDirectory()) {
			String[] files = dir.list(new FilenameFilter() {

				public boolean accept(File dir, String name) {
					return name.endsWith(".csv");
				}
			});
			if ((files == null) || (files.length == 0)) {
				System.out.println("No files ending with .csv");
				System.exit(0);
			}
			PrintWriter pw = null;
			int i = 0;
			for (String file : files) {
				String[] split = file.split("_");
				int x = Integer.parseInt(split[0].substring(1));
				String[] split2 = split[1].split("\\.");
				int y = Integer.parseInt(split2[0]);
				String id = null;
				if (conn != null) {
					id = getIdFor(x, y);
					if (id == null) {
						System.err.println("No key found for y,x:" + y + ","
								+ x);
						if (pw_noKey == null) {
							String noKeyFile = args[1] + "_nokeys.csv";
							pw_noKey = new PrintWriter(noKeyFile);
							pw_noKey.println("y,x,contents");
						}
						pw = pw_noKey;
					} else {
						if (pw_key == null) {
							pw_key = new PrintWriter(args[1]);
							pw_key.println("y,x,contents,key");
						}
						pw = pw_key;
					}
				}
				pw.print(y);
				pw.print(",");
				pw.print(x);
				pw.print(",");
				String encodedValue = encodeFile(dir + "/" + file);
				pw.print(encodedValue);
				if (id != null) {
					pw.print(",");
					pw.print(id);
				}
				if (i % 10000 == 9999) {
					pw.flush();
					System.out.println("Processed " + (i + 1)
							+ " files out of " + files.length);
				}
				i++;
				pw.println();
			}
			if (pw_key != null) {
				pw_key.close();
			}
			if (pw_noKey != null) {
				pw_noKey.close();
			}
			statement.close();
			conn.close();
		}
	}

	private static String getIdFor(int x, int y) throws Exception {
		statement.clearParameters();
		statement.setInt(1, x);
		statement.setInt(2, y);
		ResultSet rs = statement.executeQuery();
		if (rs.next()) {
			return rs.getInt(1) + "";
		} else {
			return null;
		}
	}

	private static void buildKeyDB(String keyFile) throws Exception {
		String sqlFile = keyFile + ".sql3";
		Class.forName("org.sqlite.JDBC");
		conn = DriverManager.getConnection("jdbc:sqlite:" + sqlFile);
		conn.setAutoCommit(false);
		Statement statement = conn.createStatement();
		statement.executeUpdate("drop table if exists bathymetrydatafile");
		statement
				.executeUpdate("create table bathymetrydatafile(x int, y int, id int)");
		PreparedStatement pstmt = conn
				.prepareStatement("insert into bathymetrydatafile values(?,?,?)");

		LineNumberReader reader = new LineNumberReader(new FileReader(keyFile));
		String line = reader.readLine();
		while ((line = reader.readLine()) != null) {
			String[] fields = line.split(",");
			String y = fields[0];
			String x = fields[1];
			String key = fields[3];
			pstmt.setInt(1, Integer.parseInt(x));
			pstmt.setInt(2, Integer.parseInt(y));
			pstmt.setInt(3, Integer.parseInt(key));
			pstmt.addBatch();
			if (reader.getLineNumber() % 2000 == 0) {
				pstmt.executeBatch();
			}
		}
		pstmt.executeBatch();
		conn.commit();
		pstmt.close();
		statement.close();
	}

	public static String encodeFile(String file) throws Exception {
		LineNumberReader reader = new LineNumberReader(new FileReader(file));
		String line = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		while ((line = reader.readLine()) != null) {
			String[] fields = line.split(",");
			dos.writeDouble(Double.parseDouble(fields[0]));
			dos.writeDouble(Double.parseDouble(fields[1]));
			dos.writeDouble(Double.parseDouble(fields[2]));
			dos.writeInt(Integer.parseInt(fields[3]));
			dos.writeUTF(fields[4]);
		}
		reader.close();
		baos.close();
		String val = base64Encoder.encode(baos.toByteArray());
		return val.replace("\r\n", "");
	}
}
