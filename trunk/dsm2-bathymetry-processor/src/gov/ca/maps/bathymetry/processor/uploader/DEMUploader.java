package gov.ca.maps.bathymetry.processor.uploader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class DEMUploader {
	// String baseUrl =
	// "http://dsm2bathymetry.appspot.com/upload_bathymetry.html";

	static String baseUrl = "http://localhost:8888/upload_dem.html";
	private int uploadEntityLimit;
	private int lastUploadedRow;

	private DEMUploader(int limit) {
		uploadEntityLimit = limit;
		lastUploadedRow = 0;
	}

	public boolean upload(String csvFile) throws IOException {
		boolean hasMoreLines = false;
		HtmlUnitDriver driver = new HtmlUnitDriver();
		driver.get(baseUrl);
		long ti = System.currentTimeMillis();
		long ct = ti;
		LineNumberReader lnr = new LineNumberReader(new FileReader(csvFile));
		String header = lnr.readLine();
		for (int i = 0; i < lastUploadedRow; i++) {
			lnr.readLine();
		}
		String line = null;
		File cFile = new File(csvFile);
		String dir = cFile.getParent();
		String uploadFile = dir + "/current_upload_dem.csv";
		PrintWriter pw = new PrintWriter(uploadFile);
		pw.println(header);
		int count = 0;
		while ((line = lnr.readLine()) != null) {
			pw.println(line);
			count++;
			if (count >= uploadEntityLimit) {
				hasMoreLines = true;
				break;
			}
		}
		pw.close();
		lnr.close();
		uploadDemFile(driver, uploadFile);
		try {
			WebElement emailElement = driver.findElement(By.name("email"));
			if (emailElement != null) {
				doLogin(driver);
				driver.get(baseUrl);
				uploadDemFile(driver, uploadFile);
			}
		} catch (NoSuchElementException ex) {
			// do nothing
		}
		lastUploadedRow += count;
		System.out.println("Uploaded " + lastUploadedRow + " in "
				+ ((System.currentTimeMillis() - ti) / 1000) + " seconds");
		return hasMoreLines;
	}

	private void uploadDemFile(HtmlUnitDriver driver, String filePath) {
		WebElement fileElement = driver.findElement(By.name("demFile"));
		fileElement.sendKeys(filePath);
		driver.findElement(By.xpath("//input[@value=\"Send\"]")).click();
	}

	public static void main(String[] args) throws IOException,
			InterruptedException {
		if (args.length < 1) {
			System.out.println("Usage DEMUploader <encoded_csv_file>");
			System.exit(1);
		}
		String encodedFile = args[0];
		DEMUploader uploader = new DEMUploader(1000);
		boolean moreFiles = true;
		while (moreFiles) {
			try {
				moreFiles = uploader.upload(encodedFile);
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
