package com.ciac.extraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/*import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
*/
import com.ciac.Image.Query;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
/**
 * @author Maxwell Ndognkon Manga
 * <p>Mail: maxwellndognkong@gmail.com</p>
 * <p>Date: 17/09/2016</p>
 * @version 2.0
 * <p>file: UrlExtractor.java</p>
 *  Crawler Extractor Url from query per domain in the web
 *  It also Manage the urls parallel storage
 * */
public class UrlExtractor {

	private Query query;
	private WebDriver driver;
	private Map<String, Vector<String>> list_url_per_domain;
	private ArrayList<String> url_list;

	public UrlExtractor(WebDriver driver, Query query, ArrayList<String> url_list) {
		// queries
		this.query = query;
		//list of urls per domain
		list_url_per_domain = new HashMap<String, Vector<String>>();
		// list of urls 
		this.url_list = url_list;
		this.driver = driver;
	}

	public Map<String, Vector<String>> getUrls() {
		return list_url_per_domain;
	}

	public ArrayList<String> getUrlsSet() {
		return url_list;
	}

	public void extractUrls() {
		ArrayList<String> v = new ArrayList<String>();
		String q = gettokens(query.getQuery());
		
		getUrls().put("Yahoo", new Vector<String>());
		v = getYahooUrlsImage(driver, q);
		getUrls().get("Yahoo").addAll(v);
		
		getUrls().put("Google", new Vector<String>());
		v = getGoogleUrlsImage(driver, q);
		getUrls().get("Google").addAll(v);
		
		getUrls().put("Bing", new Vector<String>());
		v = getBingUrlsImage(driver, q);
		getUrls().get("Bing").addAll(v);
	}

	public static ArrayList<String> getYahooUrlsImage(WebDriver driver,String query) {

		ArrayList<String> listUrls = new ArrayList<String>();

		String base_url = "https://images.search.yahoo.com/"; // search/images?p="								// + query +
																// "&imgsz=large";  
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.manage().deleteAllCookies();

		driver.get(base_url);
		String new_query = query.replace("+", " ");

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
			//System.out.println(eles.size());
			for (WebElement ele : eles) {
				String url = ele.getAttribute("href");
				String url1 = forYahooReadUrl(url);
				listUrls.add(url1);
				
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		System.out.println("yahoo: " + listUrls.size());
		return listUrls;
	}

	public static ArrayList<String> getBingUrlsImage(WebDriver driver,String query) {

		ArrayList<String> listUrls = new ArrayList<String>();
		String url = "https://www.bing.com/?scope=images";

		String bing_query;
		bing_query = query.replace("+", " ");

		new ArrayList<String>();

		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.manage().deleteAllCookies();
		driver.get(url);
		try {
			driver.findElement(By.id("sb_form_q")).click();
			driver.findElement(By.id("sb_form_q")).clear();
			driver.findElement(By.id("sb_form_q")).sendKeys(bing_query);
			driver.findElement(By.id("sb_form_go")).click();
			List<WebElement> eles = driver
					.findElements(By.xpath("//*[@id=\"dg_c\"]//a[@title=\"View image details\"]"));
			for (WebElement ele : eles) {
				String line = ele.getAttribute("m");
				JsonObject jobj = new Gson().fromJson(line, JsonObject.class);

				String result = jobj.get("surl").getAsString();
				listUrls.add(result);
			}
			System.out.println("bing: " + listUrls.size());
		} catch (Exception e1) {
			System.err.println(e1.getMessage());
		}
		return listUrls;

	}

	public static ArrayList<String> getGoogleUrlsImage(WebDriver driver,String query) {
		String base_url = "https://www.google.co.in/imghp";

		String google_query;
		google_query = query.replace("+", " ");

		ArrayList<String> dashboard = new ArrayList<String>();

		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.manage().deleteAllCookies();

		driver.get(base_url);
		try {
			driver.findElement(By.xpath("//input[@name='q']"));
			driver.findElement(By.xpath("//input[@name='q']")).sendKeys(google_query);
			driver.findElement(By.xpath("//button[@name='btnG']")).click();

			String pageSource = driver.findElement(By.xpath("//*[@id=\"res\"]")).getAttribute("innerHTML");
			pageSource = pageSource.replaceAll("<.+?>", "");

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
					dashboard.add(urlStr);
				}
			}
			System.out.println("google: " + dashboard.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
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
