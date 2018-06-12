package com.ciac.main;

import java.io.File;
//import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.ciac.extraction.QueryExtractor;
import com.ciac.extraction.UrlExtractor;
import com.ciac.extraction.UrlMaster;
import com.ciac.Image.*;
/**
 * @author Maxwell Ndognkon Manga 
 * <p>Mail: maxwellndognkong@gmail.com</p>
 * <p>Date: 17/09/2016</p>
 * @version 2.0
 * <p>file: Main.java</p>
 * */
public class Main {

	// CascadeClassifier detectorFacePro = new
	// CascadeClassifier("haarcascade_frontalface_default.xml");
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		System.out.println("Starting smart Crawling ... ");
		String person = null;

		if (args.length > 1 && args[1] != null) {
			person = args[0] + " " + args[1];
		} else {
			// person="obama";
		}

		System.getProperty("user.dir");
		String resultFolder = System.getProperty("user.dir") + File.separator + "CrawlerDataset";
		File f = new File(resultFolder);
		if (!f.exists()) {
			f.mkdir();
		}
		File folderdata = new File(resultFolder + File.separator + person);
		folderdata.mkdir();

		ArrayList<Query> ld = QueryExtractor.retrieveQueryFromDb(person);
		System.out.println(ld.size() + " queries found " + " for \"" + person + "\" in database");
		
		

		if (ld.size() <= 0) {
			System.out.println("Getting queries for \"" + person + "\" from keywordtool.io ");
			WebDriver driver = new FirefoxDriver();
			QueryExtractor qe = new QueryExtractor();
			ld = qe.retrieveQuerySeq(driver, person, true); // true to database
			driver.close();
			driver.quit();

		}
		
		Map<String, Vector<String>> myMapUrls;
		int i = 0;

		for (Query q : ld) {
			WebDriver driver = new FirefoxDriver();
			System.out.println("\nStarting Iteration " + i + "/" + ld.size() + " on Query \"" + q.getQuery() + "\"");
			ArrayList<String> list_urls = new ArrayList<String>();
			UrlExtractor u_extract = new UrlExtractor(driver, q, list_urls);
			u_extract.extractUrls();
			myMapUrls = u_extract.getUrls();
			list_urls = u_extract.getUrlsSet();
			driver.close(); 
			driver.quit();
			//System.out.println("Google " + myMapUrls.get("Google").size());
			//System.out.println("Yahoo " + myMapUrls.get("Yahoo").size());
			//System.out.println("Bing " + myMapUrls.get("Bing").size());
			System.out.println("Overall urlslist " + list_urls.size());
			System.out.println(myMapUrls);
			
			UrlMaster umas = new UrlMaster(folderdata.getAbsolutePath(), q, person, myMapUrls, 0.6);
			// UrlMaster(folderPath,query, main_query_(person),
			// domain_name_and_list_of_urls, score)
			umas.parseUrls(person);
			i++;
			 
		}
		long stop = System.currentTimeMillis();

		System.out.println(".... End smart Crawling.");
		System.out.println("Crawling time " + (stop - start) / 1000 + "seconds");

	}
}
