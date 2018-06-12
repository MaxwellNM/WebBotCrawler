package com.ciac.extraction;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
/**
 * @author Maxwell Ndognkon Manga
 * <p>Mail: maxwellndognkong@gmail.com</p>
 * <p>Date: 17/09/2016</p>
 * @version 2.0
 * <p>file: UrlMaster.java</p>
 *  Crawler Management parallel Pool
 * */
public class UrlMaster {
	private static ArrayList<String> list_urls;
	private static String folder;
	private static String query;
	private static double treeshold;
	private static String domain;
	private static Map<String, Vector<String>> myMappedurls;
	private static File folderdata_imgs;

	// UrlMaster(folderPath,query, main_query_(person),
	// domain_name_and_list_of_urls, score)
	public UrlMaster(String absolutePath, Query q, String person2, Map<String, Vector<String>> myMapUrls,
			Double double1) {
		UrlMaster.folder = absolutePath;
		UrlMaster.query = q.getQuery();
		UrlMaster.treeshold = double1;
		myMappedurls = myMapUrls;
		folderdata_imgs = new File(folder + File.separator + "originalImage");
		folderdata_imgs.mkdir();
	}

	public static ArrayList<String> getListString() {
		return list_urls;
	}

	public static void parseUrls(String person) {
		// Parallel this;
		int k = Runtime.getRuntime().availableProcessors();
		// int N = ThreadSize;

		ExecutorService executor = Executors.newFixedThreadPool(k);
		String[] domains = { "Google", "Yahoo", "Bing" };
		Vector<String> all_list = new Vector<String>();
		for (int i = 0; i < domains.length; i++) {
			Vector<String> ls_urls = myMappedurls.get(domains[i]);
			all_list.addAll(ls_urls);

		}
		// String p = all_list[0];
		// int blocLength = ((N-(StartRange+1))-StartRange)/ThreadSize;
		Callable  r = null;
		int N = all_list.size();// tab.length;
		int n = N / k;
		int deb = 0;
		int fin = 0;
	    Collection collection = new ArrayList();
	    
		for (int i = 0; i < k; i++) {
			deb = i * n;
			if (i == k - 1)
				fin = N - 1;
			else
				fin = (i + 1) * n - 1;

			if (i < myMappedurls.get(domains[0]).size()) {
				r = new UrlNode(deb, fin, folder, query, person, all_list, "pi", treeshold, "Google");// QueryExtracti(i,blocLength,baseUrl,person,tr_collection,list_q);
			} else if (i < (myMappedurls.get(domains[0]).size() + myMappedurls.get(domains[1]).size())) {
				r = new UrlNode(deb, fin, folder, query, person, all_list, "pi", treeshold, "Yahoo");
			} else {
				r = new UrlNode(deb, fin, folder, query, person, all_list, "pi", treeshold, "Bing");
			}
			
			collection.add(r);
			
			//r.start();
			
		/*	 try {
					r.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					System.out.println("Exception Thread "+r.getName()+"\n"+e);
					//e.printStackTrace();
				}
*/
			//executor.execute(r);
		}
		
		 try {
		        List<Future<Boolean>> futures = executor.invokeAll(collection);

		        boolean works=true;
		        for(Future<Boolean> future : futures){
		              future.get();
		              if (future.isDone()) {
		                  System.out.println("true");
		              }
		              else{
		                  System.out.println("false");works=false;
		              }
		          }
		      if(works)System.out.println("All threads terminated");
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		 
		 
		// Waiting all Threads to terminate
		/*while (!executor.isTerminated()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		executor.shutdown();*/

		// return list_urls;

	}

}
