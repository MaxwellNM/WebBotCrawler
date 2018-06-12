package com.ciac.extraction;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
//import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.firefox.*;

import com.ciac.Image.Query;
import com.ciac.dao.Dao;
import com.ciac.dao.DaoFactory;
import com.ciac.db.CiacConnection;
import com.mongodb.DB;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * @author Maxwell Ndognkon Manga
 * <p>Mail: maxwellndognkong@gmail.com</p>
 * <p>Date: 17/09/2016</p>
 * @version 2.0
 * <p>file: QueryExtractor.java</p>
 *  Crawler web browser automation class to fetch urls in the web
 * */
public class QueryExtractor {
	private static String baseUrl;
	private static Dao<Query> queryDAO;
	private static ArrayList<Query> list_q;

	public static ArrayList<Query> getListQuery() {
		return list_q;
	}

	public static ArrayList<Query> retrieveQuery(String person, Boolean persistResult) {
		/*
		 * CiacConnection con = new CiacConnection(); if (con==null){
		 * System.out.println("Null connexion"); System.exit(-1); }
		 * con.initInstance();
		 */
		DB dbb = CiacConnection.getInstance(CiacConnection.db_SC);
		queryDAO = DaoFactory.getQueryDAO(dbb);

		WebDriver driver = new FirefoxDriver();
		baseUrl = "http://keywordtool.io";
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

		driver.get(baseUrl + "/");
		driver.findElement(By.id("edit-keyword")).clear();
		driver.findElement(By.id("edit-keyword")).sendKeys(person);
		/*
		 * //Wait Results 5 seconds driver.manage().timeouts().implicitlyWait(5,
		 * TimeUnit.SECONDS);
		 */
		driver.findElement(By.id("edit-submit")).click(); 
		// Wait Results 5 seconds
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		WebElement table_element = driver.findElement(By.id("results-suggestions"));
		// List<WebElement>
		// tr_collection=table_element.findElements(By.xpath("id('results-suggestions')/tbody/tr"));
		List<WebElement> tr_collection = table_element
				.findElements(By.xpath("id('results-suggestions')/div/div/table/tbody/tr"));

		list_q = new ArrayList<Query>();
		System.out.println("NUMBER OF ROWS IN THIS TABLE = " + tr_collection.size());
		int row_num, col_num;
		row_num = 1;

		// Parallel this;
		int k = Runtime.getRuntime().availableProcessors();

		ExecutorService executor = Executors.newFixedThreadPool(k);

		int N = tr_collection.size();
		int n = N / k;
		int deb = 0;
		int fin = 0;
		for (int i = 0; i < k; i++) {
			deb = i * n;
			if (i == k - 1)
				fin = N - 1;
			else
				fin = (i + 1) * n - 1;
			Runnable r = new QueryExtracti(deb, fin, baseUrl, person, tr_collection, list_q);
			executor.execute(r);
		}

		// Waiting all Threads to terminate
		while (!executor.isTerminated()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		executor.shutdown();
		System.out.println("Number of Query " + list_q.size());

		// Close driver Connexion
		driver.close();
		driver.quit();

		// saving to database
		if (persistResult) {
			for (Query q : list_q) {
				ArrayList<Query> ls_im = queryDAO.findOnConstraint(CiacConnection.coll_Query, "query", q.getQuery());
				Query im;// = ls_im.get(0);
				if (ls_im != null) {

					im = queryDAO.update(q, CiacConnection.coll_Query);

				} else {
					queryDAO.create(q, CiacConnection.coll_Query);
				}
			}
		}

		return list_q;

	}

	public static ArrayList<Query> retrieveQuerySeq(WebDriver driver, String person, Boolean persistResult) {
		CiacConnection con = new CiacConnection();
		/*
		 * if (con==null){ System.out.println("Null connexion");
		 * System.exit(-1); } con.initInstance();
		 */
		DB dbb = CiacConnection.getInstance(CiacConnection.db_SC);
		queryDAO = DaoFactory.getQueryDAO(dbb);

		
		baseUrl = "http://keywordtool.io/";
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

		driver.get(baseUrl + "/");
		driver.findElement(By.id("edit-keyword")).clear();
		driver.findElement(By.id("edit-keyword")).sendKeys(person);

		driver.findElement(By.id("edit-submit")).click();
		// Wait Results 30 seconds
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

		list_q = new ArrayList<Query>();

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();
		driver.findElement(By.xpath("//button[contains(.,'Copy all')]")).click();
		String copy_button;
		try {
			copy_button = (String) clipboard.getData(DataFlavor.stringFlavor);
			// System.out.println(copy_button);
			String[] list_of_queries = copy_button.split("\n");
			// System.out.println(list_of_queries);
			for (int j = 0; j < list_of_queries.length; j++) {

				Query q = new Query();
				q.setDomain(baseUrl);
				q.setPerson(person);
				q.setListQuery(list_of_queries[j]);
				list_q.add(q);
			}
			System.out.printf("%s NUMBER OF QUERIES FOUND FOR \"%s\".", list_of_queries.length, person);
		} catch (UnsupportedFlavorException | IOException e) {
			e.printStackTrace();
		}
		if (persistResult) {
			for (Query q : list_q) {
				queryDAO.create(q, CiacConnection.coll_Query);
			}
		}
		return list_q; 
	}

	/**
	 * Remove token in string with length 1
	 */
	private static String[] pre_processquery(String[] list_key) {
		// TODO Auto-generated method stub
		int len = list_key.length;
		List<String> list_res = new ArrayList<String>();
		int cpt = 0;
		for (int i = 0; i < len; i++) {
			if (list_key[i].length() > 1)
				list_res.add(list_key[i]);
		}

		String[] res = new String[list_res.size()];
		return list_res.toArray(res);
	}

	public static ArrayList<Query> retrieveQueryFromDb(String person) {

		CiacConnection.initInstance();
		DB dbb = CiacConnection.getInstance(CiacConnection.db_SC);
		queryDAO = DaoFactory.getQueryDAO(dbb);
		return queryDAO.findOnConstraint(CiacConnection.coll_Query, "person", person);
		// return queryDAO.findAll(CiacConnection.coll_Query);

	}
}
