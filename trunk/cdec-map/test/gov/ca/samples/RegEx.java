package gov.ca.samples;

public class RegEx {
	public static void main(String[] args) {
		String line = "href=abcdecf href=\"xyz\"";
		String line2 = line.replaceAll("(href=\"|href=)", "$1http://");
		System.out.println(line2);
		String line3 = line.replaceAll("href=\"", "yyy=\"");
		System.out.println(line3);
	}
}
