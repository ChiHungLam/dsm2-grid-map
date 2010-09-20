package gov.ca.maps.bathymetry.processor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class GetSurveys {
	static String downloadDirectory = "d:/models/bathymetry/noaa/downloads/";

	public static void main(String[] args) throws IOException {
		WebDriver driver = new HtmlUnitDriver();
		driver
				.get("http://www.ngdc.noaa.gov/nndc/struts/results?op_0=l&v_0=&op_1=l&v_1=&op_2=eq&v_2=&op_3=l&v_3=&op_4=l&v_4=&op_5=l&v_5=&op_6=l&v_6=&op_7=l&v_7=&op_8=l&v_8=&op_9=l&v_9=&op_10=eq&v_10=&op_11=eq&v_11=&op_12=eq&v_12=&op_13=eq&v_13=&op_14=eq&v_14=&op_15=eq&v_15=&op_16=eq&v_16=&op_17=eq&v_17=&op_18=eq&v_18=&op_19=eq&v_19=&op_20=eq&v_20=&op_21=eq&v_21=&op_22=eq&v_22=&t=101523&s=2&d=1&d=2");
		List<WebElement> tableElements = driver.findElements(By
				.tagName("table"));
		WebElement tableWithDataLinks = tableElements.get(3);
		List<WebElement> links = tableWithDataLinks.findElements(By
				.tagName("a"));
		WebDriver driver2 = new HtmlUnitDriver();
		for (WebElement link : links) {
			String surveyName = link.getText();
			File file = new File(getSurveyFilePath(surveyName));
			File fileProcessed = new File(file.getAbsolutePath() + ".processed");
			if (file.exists() || fileProcessed.exists()) {
				System.out.println("Skipping file for " + surveyName);
				continue;
			}
			driver2.get("http://www.ngdc.noaa.gov" + link.getAttribute("href"));
			try {
				System.out.println("Downloading file for " + surveyName);
				driver2.findElement(By.linkText(surveyName + ".xyz.gz"))
						.click();
			} catch (Exception ex) {
				System.err.println("No xyz file for " + surveyName);
				continue;
			}
			String pageSource = driver2.getPageSource();
			FileOutputStream fos = new FileOutputStream(
					getSurveyFilePath(surveyName));
			fos.write(pageSource.getBytes());
			fos.close();
		}
		driver2.close();
		driver.close();
	}

	private static String getSurveyFilePath(String surveyName) {
		return downloadDirectory + surveyName + ".xyz";
	}
}
