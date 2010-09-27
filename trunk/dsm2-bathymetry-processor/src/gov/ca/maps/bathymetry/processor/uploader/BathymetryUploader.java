package gov.ca.maps.bathymetry.processor.uploader;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class BathymetryUploader {
	// String baseUrl =
	// "http://3.latest.dsm2grid.appspot.com/upload_bathymetry.html";

	String baseUrl = "http://localhost:8888/upload_bathymetry.html";

	private BathymetryUploader() {
	}

	public boolean upload(String directory) {
		HtmlUnitDriver driver = new HtmlUnitDriver();
		driver.get(baseUrl);
		File dir = new File(directory);
		long ti = System.currentTimeMillis();
		long ct = ti;
		if (dir.exists() && dir.isDirectory()) {
			String[] files = dir.list(new FilenameFilter() {

				public boolean accept(File dir, String name) {
					return name.endsWith(".csv");
				}
			});
			if ((files == null) || (files.length == 0)) {
				return false;
			}
			for (int i = 0; i < files.length; i++) {
				if (System.currentTimeMillis() - ct > 60000) {
					System.out.println("Uploaded " + (i + 1) + " files ...");
					ct = System.currentTimeMillis();
				}
				String name = files[i];
				System.out.println("Processing file: " + name);
				String filePath = dir + "/" + name;
				driver.findElement(By.name("bathymetryFile"))
						.sendKeys(filePath);
				driver.findElement(By.xpath("//input[@value=\"Send\"]"))
						.click();
				File processedFile = new File(filePath);
				processedFile.renameTo(new File(filePath + ".done"));
			}
			System.out.println("Uploaded " + (files.length) + " files in "
					+ ((System.currentTimeMillis() - ti) / 1000) + " seconds");
		}
		return false;
	}

	public static void main(String[] args) throws IOException,
			InterruptedException {
		if (args.length < 1) {
			return;
		}
		String splitterDir = args[0];
		BathymetryUploader uploader = new BathymetryUploader();
		boolean moreFiles = true;
		while (moreFiles) {
			try {
				moreFiles = uploader.upload(splitterDir);
			} catch (Exception ex) {
			}
		}
	}
}
