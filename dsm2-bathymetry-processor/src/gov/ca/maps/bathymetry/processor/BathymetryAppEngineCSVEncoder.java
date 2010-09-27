package gov.ca.maps.bathymetry.processor;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.LineNumberReader;
import java.io.PrintWriter;

import sun.misc.BASE64Encoder;

public class BathymetryAppEngineCSVEncoder {
	public static BASE64Encoder base64Encoder = new BASE64Encoder();

	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			System.out
					.println("Usage: BathymetryAppEngineCSVEncoder <directory> <out_file>");
			System.exit(1);
		}
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
			PrintWriter pw = new PrintWriter(args[1]);
			pw.println("y,x,contents");
			for (String file : files) {
				String[] split = file.split("_");
				int x = Integer.parseInt(split[0].substring(1));
				String[] split2 = split[1].split("\\.");
				int y = Integer.parseInt(split2[0]);
				pw.print(y);
				pw.print(",");
				pw.print(x);
				pw.print(",");
				String encodedValue = encodeFile(dir + "/" + file);
				pw.print(encodedValue);
				pw.println();
			}
			pw.close();
		}
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
