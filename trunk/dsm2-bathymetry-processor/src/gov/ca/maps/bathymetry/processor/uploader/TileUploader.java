package gov.ca.maps.bathymetry.processor.uploader;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class TileUploader {
	String baseUrl = "http://noaabathy.appspot.com/upload.jsp";
	// String baseUrl = "http://localhost:8888/upload.html";
	// String baseUrl = "http://dsm2bathymetry.appspot.com/upload.html";
	private final int zoomLevel;

	private TileUploader(int zoomLevel) {
		this.zoomLevel = zoomLevel;
	}

	public void upload(String directory, String prefix) {
		HtmlUnitDriver driver = new HtmlUnitDriver();
		driver.get(baseUrl);
		File dir = new File(directory);
		long ti = System.currentTimeMillis();
		long ct = ti;
		if (dir.exists() && dir.isDirectory()) {
			String[] files = dir.list(new FilenameFilter() {

				public boolean accept(File dir, String name) {
					return name.endsWith("_" + zoomLevel + ".png");
				}
			});
			for (int i = 0; i < files.length; i++) {
				if (System.currentTimeMillis() - ct > 60000) {
					System.out.println("Uploaded " + (i + 1)
							+ " files @ zoomLevel = " + zoomLevel + " ...");
					ct = System.currentTimeMillis();
				}
				String name = prefix + files[i];
				String filePath = dir + "/" + files[i];
				driver.findElement(By.name("name")).sendKeys(name);
				driver.findElement(By.name("file")).sendKeys(filePath);
				driver.findElement(By.xpath("//input[@value=\"Send\"]"))
						.click();
			}
			System.out.println("Uploaded " + (files.length)
					+ " files @ zoomLevel = " + zoomLevel + " in "
					+ ((System.currentTimeMillis() - ti) / 1000) + " seconds");
		}
	}

	public static void main(String[] args) throws IOException,
			InterruptedException {
		if (args.length < 1) {
			return;
		}
		final String tileDir = args[0];
		String prefix = "";
		if (args.length == 2) {
			prefix = args[2];
		}
		for (int i = 4; i < 17; i++) {
			final int zoomLevel = i;
			TileUploader uploader = new TileUploader(zoomLevel);
			uploader.upload(tileDir, prefix);
		}
	}
}
