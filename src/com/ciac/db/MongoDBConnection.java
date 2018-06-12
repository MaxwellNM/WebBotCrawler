package com.ciac.db;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.mongodb.DB;
import com.mongodb.MongoClient;

/**
 * @author Maxwell Ndognkon Manga 
 * <p>Mail: maxwellndognkong@gmail.com</p>
 * <p>Date: 17/09/2016</p>
 * @version 2.0
 * <p>file: CiacConnection.java</p>
 *  MongoDB Connexion Class
 * */
public class MongoDBConnection {

	public static final String url   = "localhost";
	public static final String db_MAIN = "main_db";
	
	private static String user = "root";
	private static String passwd = "";	
	private static MongoClient c;
	private static DB db;
	
	public static String coll_img = "Image";
	
	public static DB getInstance(String db_name) throws UnknownHostException{
		if(c == null){
			c = new MongoClient();
			db = c.getDB(db_name);			
		} 
		return db;
	} 
}
