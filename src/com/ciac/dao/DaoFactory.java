package com.ciac.dao;

import com.ciac.Image.Query;
import com.ciac.Image.Url;
import com.mongodb.DB;

/**
 * @author Maxwell Ndognkon Manga 
 * <p>Mail: maxwellndognkong@gmail.com</p>
 * <p>Date: 17/09/2016</p>
 * @version 2.0
 * <p>file: DaoFactory.java</p>
 *  Data Access Layer Interface
 * */
public class DaoFactory {

	public static Dao<Query> getQueryDAO(DB db) {
		return new QueryDao(db);
	}

	public static Dao<Url> getUrlDAO(DB db) {
		// TODO Auto-generated method stub
		return new UrlDAO(db);
		// return null;
	}

}