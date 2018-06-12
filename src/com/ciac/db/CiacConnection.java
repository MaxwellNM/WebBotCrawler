package com.ciac.db;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

//import javax.servlet.ServletContext;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;

//import cd.com.db.DAOConfigurationException;
/**
 * @author Maxwell Ndognkon Manga 
 * <p>Mail: maxwellndognkong@gmail.com</p>
 * <p>Date: 17/09/2016</p>
 * @version 2.0
 * <p>file: CiacConnection.java</p>
 *  Database Connection class 
 * */
public class CiacConnection {
	public static boolean is_DB_init = false;
	private static String url = "";
	private static String user = "";
	private static String passwd = "";

	public static String db_MAIN = "";
	public static String db_PA = "";
	public static String db_CD = "";
	public static String db_LD = "";
	public static String db_PI = "";
	public static String db_EV = "";
	public static String db_SC = "";

	public static String coll_job = "";
	public static String coll_dataset = "";
	public static String coll_runningCpuChart = "";
	public static String coll_runningImgChart = "";
	public static String coll_evalPerfChart = "";
	public static String coll_image = "";
	public static String coll_user = "";
	// For Smart Crawler Data Extraction
	public static String coll_ImageCrawled = "";
	public static String coll_Url = "";
	public static String coll_Query = "";

	private static MongoClient c;
	private static DB db;

	public static void initInstance() {
		is_DB_init = true;
		// System.out.println("In the initInstance");
		String path_MongoDB_Access = "MongoDB_Access";
		String p_url = "url";
		String p_user = "user";
		String p_passwd = "passwd";

		String path_MongoDB_DB = "MongoDB_DB";
		String p_db_MAIN = "db_MAIN";
		String p_db_PA = "db_PA";
		String p_db_CD = "db_CD";
		String p_db_LD = "db_LD";
		String p_db_PI = "db_PI";
		String p_db_EV = "db_EV";
		String p_db_SC = "db_SC";

		String path_MongoDB_Coll = "MongoDB_Coll";
		String p_coll_job = "coll_job";
		String p_coll_dataset = "coll_dataset";
		String p_coll_runningCpuChart = "coll_runningCpuChart";
		String p_coll_runningImgChart = "coll_runningImgChart";
		String p_coll_evalPerfChart = "coll_evalPerfChart";
		String p_coll_image = "coll_image";
		String p_coll_user = "coll_user";
		// Smart Crawler Data Extraction
		String p_coll_ImageCrawled = "coll_ImageCrawled";
		String p_coll_Url = "coll_Url";
		String p_coll_Query = "coll_Query";

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		// InputStream fichierProperties = context.getResourceAsStream(
		// FICHIER_PROPERTIES );
		InputStream file_MongoDB_Access = classLoader.getResourceAsStream(path_MongoDB_Access);
		if (file_MongoDB_Access == null)
			System.out.println("Mongo Access is Null");
		// InputStream file_MongoDB_Access = this.get getResourceAsStream(
		// path_MongoDB_Access );
		try {
			Properties properties = new Properties();
			properties.load(file_MongoDB_Access);
			url = properties.getProperty(p_url);
			user = properties.getProperty(p_user);
			passwd = properties.getProperty(p_passwd);
		} catch (IOException e) {
			// throw new DAOConfigurationException( "DB: unable to load file " +
			// path_MongoDB_Access, e );
			System.out.println("Error exception File Mongo access \n" + e);
		}

		InputStream file_MongoDB_DB = classLoader.getResourceAsStream(path_MongoDB_DB);
		try {
			Properties properties = new Properties();
			properties.load(file_MongoDB_DB);
			db_MAIN = properties.getProperty(p_db_MAIN);
			db_PA = properties.getProperty(p_db_PA);
			db_CD = properties.getProperty(p_db_CD);
			db_LD = properties.getProperty(p_db_LD);
			db_PI = properties.getProperty(p_db_PI);
			db_EV = properties.getProperty(p_db_EV);
			db_SC = properties.getProperty(p_db_SC);
			System.out.println("db SmartCrawler =" + db_SC);
		} catch (IOException e) {
			// throw new DAOConfigurationException( "DB: unable to load file " +
			// path_MongoDB_DB, e );
			System.out.println("Error exception File Mongo DB \n" + e);
		}

		InputStream file_MongoDB_Coll = classLoader.getResourceAsStream(path_MongoDB_Coll);
		// InputStream file_MongoDB_Coll = context.getResourceAsStream(
		// path_MongoDB_Coll );
		try {
			Properties properties = new Properties();
			properties.load(file_MongoDB_Coll);
			coll_job = properties.getProperty(p_coll_job);
			coll_dataset = properties.getProperty(p_coll_dataset);
			coll_runningCpuChart = properties.getProperty(p_coll_runningCpuChart);
			coll_runningImgChart = properties.getProperty(p_coll_runningImgChart);
			coll_evalPerfChart = properties.getProperty(p_coll_evalPerfChart);
			coll_image = properties.getProperty(p_coll_image);
			coll_user = properties.getProperty(p_coll_user);
			coll_ImageCrawled = properties.getProperty(p_coll_ImageCrawled);
			coll_Url = properties.getProperty(p_coll_Url);
			coll_Query = properties.getProperty(p_coll_Query);

		} catch (IOException e) {
			// throw new DAOConfigurationException( "DB: unable to load file " +
			// path_MongoDB_Coll, e );
			System.out.println("Error exception File Mongo Collections \n" + e);
		}
	}

	public static DB getInstance(String db_name) {
		// String rootPath = context.getRealPath("/");
		// System.out.println("doPOST: rootPath = "+rootPath);

		if (!is_DB_init) {
			initInstance();
		}

		if (c == null) {
			try {
				System.setProperty("javax.net.ssl.trustStore", "./res/mongodbS.ts");
				System.setProperty("javax.net.ssl.trustStorePassword", "StorePass");

				System.setProperty("javax.net.ssl.keyStore", "./res/mongodbS.jks");
				System.setProperty("javax.net.ssl.keyStorePassword", "StorePass");

				MongoClientOptions.Builder builder = MongoClientOptions.builder();
				// builder.sslEnabled(true).build();
				builder.sslEnabled(false).build();
				// builder.sslInvalidHostNameAllowed(true).build();
				builder.sslInvalidHostNameAllowed(false).build();

				// System.out.println("doPOST: url = "+url);
				// System.out.println("doPOST: user = "+user);
				// System.out.println("doPOST: passwd = "+passwd);
				MongoClientURI uri = new MongoClientURI(
						"mongodb://" + user + ":" + passwd + "@" + url + "/?authSource=admin", builder);

				// c = new MongoClient(uri); //ssl security
				c = new MongoClient();
				db = c.getDB(db_name);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			db = c.getDB(db_name);
		}
		return db;
	}
}
