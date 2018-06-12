package com.ciac.extraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.URLDecoder;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
/**
 * @author Maxwell Ndognkon Manga & Ganesh Chavan
 * <p>Mail: maxwellndognkong@gmail.com</p>
 * <p>Date: 17/09/2016</p>
 * @version 2.0
 * <p>file: UrlExtracti.java</p>
 *  Crawler web browser automation class to fetch urls in the web
 * */
public class UrlExtracti implements Runnable {
	private String domain;
	private Map<String, Vector<String>> myMap;
	private String query;
	static URLDecoder udec;
	ArrayList<String> list_urls;

	public UrlExtracti(String dom, String quer, Map<String, Vector<String>> p, ArrayList<String> list_urls) {
		this.domain = dom;
		this.myMap = p;
		this.query = quer;
		this.list_urls = list_urls;
		udec = new URLDecoder();
	}

	@Override
	public void run() {
		String quer = gettokens(query);
		ArrayList<String> v = new ArrayList<String>();
		if (domain.equalsIgnoreCase("Google")) {
			v = getGoogleUrlsImage(quer);
			myMap.get("Google").addAll(v);
			// System.out.println("google");

		} else if (domain.equalsIgnoreCase("Yahoo")) {
			// System.out.println("yahoo");
			v = getYahooUrlsImage(quer);
			myMap.get("Yahoo").addAll(v);

		} else if (domain.equalsIgnoreCase("Bing")) {
			v = getBingUrlsImage(quer);

			myMap.get("Bing").addAll(v);
			// System.out.println("bing");
		}

		list_urls.addAll(v);
	}

	public static ArrayList<String> getYahooUrlsImage(String query) {

		ArrayList<String> listUrls = new ArrayList<String>();

		String base_url = "https://images.search.yahoo.com/"; // search/images?p="
																// "&imgsz=large";
		WebDriver driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.manage().deleteAllCookies();

		driver.get(base_url);
		String new_query = query.replace("+", " ");
		// System.out.println(query);
		// System.out.println(new_query);
		try {
			driver.findElement(By.xpath("//input[@class='yschsp']"));
			driver.findElement(By.xpath("//input[@class='yschsp']")).sendKeys(new_query);
			driver.findElement(By.xpath("//input[@value='Search']")).click();

			while (true) {
				try {
					driver.findElement(By.xpath("//button[contains(.,'Show More Images')]")).click();
				} catch (Exception e) {
					break;
				}
			}

			List<WebElement> eles = driver.findElements(By.xpath("//*[@id=\"sres\"]/li/a"));
			System.out.println(eles.size());
			for (WebElement ele : eles) {
				String url = ele.getAttribute("href");

				// System.out.println(url);
				// System.out.println("\n\n");
				String url1 = forYahooReadUrl(url);
				String url2 = udec.decode(url1,"UTF-8");
				listUrls.add(url2);
				//System.out.println(url1);
				// break;

			}
			// *[@id="yui_3_5_1_1_1470665124273_1877"]
			/*
			 * while (true) { try { driver.findElement(By.xpath(
			 * "//button[contains(.,'Show More Images')]")).click(); } catch
			 * (Exception e) { break; } } JavascriptExecutor jse =
			 * (JavascriptExecutor) driver;
			 * jse.executeScript("window.scrollBy(0,document.body.scrollHeight)"
			 * ); jse.executeScript(
			 * "window.scrollBy(0,-document.body.scrollHeight)"); for
			 * (WebElement ele : eles) { // JavascriptExecutor jsx =
			 * (JavascriptExecutor) driver;
			 * 
			 * try { // jsx.executeScript("arguments[0].click();", ele);
			 * ele.click(); String link = driver.findElement(By.xpath(
			 * "//a[@title='View Page']")).getAttribute("href");
			 * listUrls.add(link); // System.out.println(link); } catch
			 * (Exception e) { // e.printStackTrace(); continue; } }
			 */
			System.out.println("yahoo: " + listUrls.size());
			/*
			 * pageSource = pageSource.replaceAll("<.+?>", ""); File f = new
			 * File("C:\\Users\\\\Desktop\\out.txt"); if (!f.exists()) {
			 * f.createNewFile(); } FileWriter fw = new
			 * FileWriter(f.getAbsoluteFile()); BufferedWriter bw = new
			 * BufferedWriter(fw); bw.write(pageSource); bw.close();
			 */
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		driver.close();
		// System.exit(0);
		return listUrls;

	}

	public static ArrayList<String> getBingUrlsImage(String query) {

		ArrayList<String> listUrls = new ArrayList<String>();
		String url = "https://www.bing.com/?scope=images";

		String bing_query;
		// System.out.println(query);
		bing_query = query.replace("+", " ");
		// System.out.println(bing_query);

		new ArrayList<String>();

		WebDriver driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.manage().deleteAllCookies();
		driver.get(url);
		try {
			driver.findElement(By.id("sb_form_q")).click();
			driver.findElement(By.id("sb_form_q")).clear();
			driver.findElement(By.id("sb_form_q")).sendKeys(bing_query);
			driver.findElement(By.id("sb_form_go")).click();
			// *[@id="dg_c"]
			List<WebElement> eles = driver
					.findElements(By.xpath("//*[@id=\"dg_c\"]//a[@title=\"View image details\"]"));
			// System.out.println(eles.size());
			for (WebElement ele : eles) {
				String line = ele.getAttribute("m");
				JsonObject jobj = new Gson().fromJson(line, JsonObject.class);

				String result = jobj.get("surl").getAsString();
				String url2 = udec.decode(result,"UTF-8");
				listUrls.add(url2);
				//System.out.println(result);

			}
			System.out.println("bing: " + listUrls.size());
			// System.out.println(ele);
			// driver.findElement(By.id("detail_viewPage")).getAttribute("href");
			/*
			 * while(true) { try { String link =
			 * driver.findElement(By.id("detail_viewPage")).getAttribute("href")
			 * ; System.out.println(link); } catch (Exception e) {
			 * System.out.println(e.getMessage()); break; }
			 * 
			 * }
			 */
		} catch (Exception e1) {
			System.out.println(e1.getMessage());
		}
		driver.close();
		// System.exit(0);
		return listUrls;

	}

	public static ArrayList<String> getGoogleUrlsImage(String query) {
		String base_url = "https://www.google.co.in/imghp";

		String google_query;
		// System.out.println(query);
		google_query = query.replace("+", " ");
		// System.out.println(google_query);

		ArrayList<String> dashboard = new ArrayList<String>();

		WebDriver driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.manage().deleteAllCookies();

		driver.get(base_url);
		try {
			driver.findElement(By.xpath("//input[@name='q']"));
			driver.findElement(By.xpath("//input[@name='q']")).sendKeys(google_query);
			driver.findElement(By.xpath("//button[@name='btnG']")).click();
			/*
			 * List<WebElement> eles =
			 * driver.findElements(By.xpath("//*[@id=\"rg_s\"]/div"));
			 * System.out.println(eles.size()); for (WebElement ele : eles) {
			 * //JavascriptExecutor jsx = (JavascriptExecutor) driver;
			 * 
			 * try { ele.click(); //jsx.executeScript("arguments[0].click();",
			 * ele); driver.findElement(By.linkText("Visit page"
			 * )).click();//getAttribute("data-href"); // String link =
			 * driver.findElement(By.xpath(
			 * "//*[@id=\"irc_cc\"]//table[1]//span[contains(.,'Visit page')]"
			 * )).getAttribute("href"); //System.out.println(link); } catch
			 * (Exception e) { e.printStackTrace(); continue; } }
			 */
			String pageSource = driver.findElement(By.xpath("//*[@id=\"res\"]")).getAttribute("innerHTML");
			pageSource = pageSource.replaceAll("<.+?>", "");

			/*
			 * File f = new File("C:\\Users\\\\Desktop\\out.txt"); if
			 * (!f.exists()) { f.createNewFile(); } FileWriter fw = new
			 * FileWriter(f.getAbsoluteFile()); BufferedWriter bw = new
			 * BufferedWriter(fw); bw.write(pageSource); bw.close();
			 */
			Pattern p = Pattern.compile("\\b(((ht|f)tp(s?)\\:\\/\\/|~\\/|\\/)|www.)"
					+ "(\\w+:\\w+@)?(([-\\w]+\\.)+(com|org|net|gov" + "|mil|biz|info|mobi|name|aero|jobs|museum"
					+ "|travel|[a-z]{2}))(:[\\d]{1,5})?" + "(((\\/([-\\w~!$+|.,=]|%[a-f\\d]{2})+)+|\\/)+|\\?|#)?"
					+ "((\\?([-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?" + "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)"
					+ "(&(?:[-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?" + "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)*)*"
					+ "(#([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)?\\b");
			Matcher m = p.matcher(pageSource);

			while (m.find()) {
				String urlStr = m.group();
				if (!urlStr.endsWith("images?q")) {
					// System.out.println(urlStr);
					String url = udec.decode(urlStr,"UTF-8");
					dashboard.add(url);
				}
			}
			System.out.println("google: " + dashboard.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		driver.close();
		// System.exit(0);
		return dashboard;
	}

	


	private static String[] pre_processquery(String[] list_key) {
		// TODO Auto-generated method stub
		int len = list_key.length;
		List<String> list_res = new ArrayList<String>();
		for (int i = 0; i < len; i++) {
			if (list_key[i].length() > 1)
				list_res.add(list_key[i]);
		}

		String[] res = new String[list_res.size()];
		return list_res.toArray(res);
	}

	public static String getTokenMetaData(String data, String token) {
		String st = "";
		// String token ="imgrefurl=";
		int j = 0;
		j = data.indexOf(token);
		int deb = j + token.length();
		while (data.charAt(deb) != '&') {
			st += data.charAt(deb);
			deb++;
		}
		// System.out.println("\nlink "+href);
		return st;

	}

	public static String gettokens(String query) {
		String[] tab = query.split(" ");
		int i = 0;
		String st = "";
		while (i < tab.length) {
			if (i == tab.length - 1)
				st += tab[i];
			else
				st += tab[i] + "+";
			i++;

		}
		return st;
	}

	public static String forYahooReadUrl(String url) {
		String newUrl = null;
		for (String re : url.split("&")) {
			if (re.startsWith("rurl")) {
				for (String newUrl1 : re.split("=")) {
					if (!newUrl1.startsWith("rurl")) {
						newUrl = newUrl1;
						// System.out.println("forYahooReadUrl: "+newUrl1);
					}
				}
			}
		}
		newUrl = newUrl.replaceAll("%2F", "/");
		newUrl = newUrl.replaceAll("%3A", ":");
		//if (newUrl.contains("%2")) {
		//	System.out.println(url);
		//	System.out.println(newUrl);
		//}
		return newUrl;
	}
}
