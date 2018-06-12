package com.ciac.extraction;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.ciac.Image.Query;

/**
 * @author Maxwell Ndognkon Manga
 * <p>Mail: maxwellndognkong@gmail.com</p>
 * <p>Date: 17/09/2016</p>
 * @version 2.0
 * <p>file: QueryExtractor.java</p>
 *  Crawler web browser automation class to fetch urls in the web
 * */
public class QueryExtracti implements Runnable {

	private int debut, fin;
	int StartRange = 0;
	List<WebElement> tr_col;
	ArrayList<Query> list_queries;
	private String baseUrl;
	private String person;

	public QueryExtracti(int deb, int block, String url, String tag, List<WebElement> tr_collection,
			ArrayList<Query> list_q) {
		this.debut = deb;
		this.fin = block;
		tr_col = tr_collection;
		list_queries = list_q;
		baseUrl = url;
		person = tag;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		// Each thread execute task in the block section assigned
		for (int i = debut; i <= fin; i++) { // System.out.println("["+System.currentTimeMillis()+"]["+Thread.currentThread().getName()+"]::
												// Element is "+i);
			List<WebElement> td_collection = tr_col.get(i).findElements(By.xpath("td"));
			// System.out.println("NUMBER OF COLUMNS="+td_collection.size());

			Query q = new Query();
			q.setDomain(baseUrl);
			q.setPerson(person);
			if (td_collection != null) {
				q.setListQuery(td_collection.get(2).getText());
				list_queries.add(q);
			}
		}

	}

}
