package gov.ca.maps.bathymetry.processor.uploader;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class DEMUploader {
	// String baseUrl =
	// "http://dsm2bathymetry.appspot.com/upload_bathymetry.html";

	static String baseUrl = "http://localhost:8888/upload_dem.html";

	private DEMUploader() {
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
				String filePath = uploadDemFile(driver, dir, name);
				if (i == 0) {
					doLogin(driver);
					driver.get(baseUrl);
					uploadDemFile(driver, dir, name);
				}
				File processedFile = new File(filePath);
				processedFile.renameTo(new File(filePath + ".done"));
			}
			System.out.println("Uploaded " + (files.length) + " files in "
					+ ((System.currentTimeMillis() - ti) / 1000) + " seconds");
		}
		return false;
	}

	private String uploadDemFile(HtmlUnitDriver driver, File dir, String name) {
		System.out.println("Processing file: " + name);
		String filePath = dir + "/" + name;
		WebElement fileElement = driver.findElement(By.name("demFile"));
		fileElement.sendKeys(filePath);
		driver.findElement(By.xpath("//input[@value=\"Send\"]")).click();
		return filePath;
	}

	public static void main(String[] args) throws IOException,
			InterruptedException {
		if (args.length < 1) {
			System.out.println("Usage DEMUploader <directory>");
			System.exit(1);
		}
		String splitterDir = args[0];
		DEMUploader uploader = new DEMUploader();
		boolean moreFiles = true;
		while (moreFiles) {
			try {
				moreFiles = uploader.upload(splitterDir);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private static void doLogin(HtmlUnitDriver driver) {
		driver.findElementById("email").sendKeys("test@example.com");
		driver.findElementById("isAdmin").click();
		driver.findElementByName("action").click();
	}
}
