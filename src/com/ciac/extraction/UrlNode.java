package com.ciac.extraction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.Callable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ciac.Image.Image_cr;
import com.ciac.Image.Url;
import com.ciac.dao.Dao;
import com.ciac.dao.DaoFactory;
import com.ciac.dao.UrlDAO;
import com.ciac.db.CiacConnection;
//import com.smartcrawl.Urls;

import com.ciac.download.UrlManager;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
/**
 * @author Maxwell Ndognkon Manga
 * <p>Mail: maxwellndognkong@gmail.com</p>
 * <p>Date: 17/09/2016</p>
 * @version 2.0
 * <p>file: UrlNode.java</p>
 *  Semantic web page Analysis based to score relevancy
 * */
public class UrlNode implements Callable<String> {

	private double PageScore = 0.0;
	private Image_cr img;
	private String Webpage;
	private String query;
	private ArrayList<Image_cr> ls = null;
	private final String tag_div = "div";
	private final String tag_h = "h";
	private final String tag_span = "span";
	private final String tag_p = "p";
	private Document doc;
	private String[] tab;// keyword of query
	private double[] weigths = null;
	private Dao<Url> UrlDAO;
	private double treeshold = 0.0;
	private String searchDomain;
	private ArrayList<ImageInfos> list_images_relevant = null;

	private List<String> cookies;
	private HttpsURLConnection conn;

	private static final String USER_AGENT = "Mozilla/5.0";
	private String tag;
	private Vector<String> myUrl;
	private String folder;
	private UrlManager um;
	private int debut;
	private int fin;

	private File file;

	private File file_pos;
	private File file_neg;
	private int terminate=0;

	public UrlNode(int deb, int fi, String fold, String quer, String person, Vector<String> url, String techn,
			Double double1, String searchdomain) {

		this.folder = fold;
		query = quer;
		techno = techn;
		treeshold = double1;
		tag = person;
		myUrl = url;
		searchDomain = searchdomain;
		list_images_relevant = new ArrayList<ImageInfos>();
		// folder to place image downloaded.
		um = new UrlManager(folder + File.separator + "originalImage");

		file = new File(folder + File.separator + "ScoreSmartCrawling");

		file_pos = new File(folder + File.separator + "PositiveScoreSmartCrawling");
		file_neg = new File(folder + File.separator + "NegativeScoreSmartCrawling");

		debut = deb;
		fin = fi;
	}

	@Override
	public String  call() throws Exception{
		// TODO Auto-generated method stub
        String status="";
        int i=debut;
		for ( ; i <= fin; i++) {
			// compute score of images
			int state =0;
			state= extract(query, myUrl.elementAt(i), techno);
			if(state==1){
			// Save all important images
			for (ImageInfos im : list_images_relevant) {
				String url_im = im.ImageSrc;
				try {
					um.getImage(url_im);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("Image " + url_im + " Not Saved");
					e.printStackTrace();
				}
			}
			terminate++;
			list_images_relevant.clear();
		}else{
			System.out.println(Thread.currentThread().getName()+" Url: "+myUrl.elementAt(i)+" Not processed ");
		}
			
		}
		if(i==fin)
			status =Thread.currentThread().getName()+"Finish Urls section "+debut+" - "+fin;

		return status;
	}

	public UrlNode(Image_cr ls) {
		img = ls;
		Webpage = img.getImageSrcPage();
		query = img.getImageQuery();
		tab = query.split(" ");
		UrlDAO = DaoFactory.getUrlDAO(CiacConnection.getInstance(CiacConnection.db_EV));
		// Initialize Weights array
		weigths = new double[tab.length];
		InitWeigths(query);
		// computeScore(Webpage);
		// liss = new
	}

	public String techno = "";
	// public String query="";

	public void setHTMLParser(String quer, String person, String[] urls, String techn, Double double1,
			String searchdomain) {
		// TODO Auto-generated constructor stub
		query = quer;
		techno = techn;
		treeshold = double1;
		tag = person;
		searchDomain = searchdomain;
		list_images_relevant = new ArrayList<ImageInfos>();
	}

	public static int distance(Element e1, Element e2, Document d) {
		boolean startCounting = false;
		int distance = 0;

		/* get all children and children of children */
		for (Element e : d.body().getAllElements()) {
			if (e.equals(e1) && !startCounting) {
				//System.out.println(e.ownText());
				startCounting = true;
				continue;
			}

			if (e.equals(e2) && !startCounting) {
				//System.out.println(e.ownText());
				startCounting = true;
				continue;
			}

			if (e.equals(e2) && startCounting) {
				//System.out.println(e.ownText());
				startCounting = false;
			}

			if (e.equals(e1) && startCounting) {
				//System.out.println(e.ownText());
				startCounting = false;
			}

			if (startCounting)
				distance++;
		}

		return distance;
	}

	public int[] FrequencyQuery(String query, String BlocText) {
		int[] t = new int[tab.length];
		// tab = query.split(" ") ;
		for (int i = 0; i < tab.length; i++) {
			t[i] = countingWord(tab[i], BlocText);
		}
		return t;
	}

	public String showWeigths() {
		// Random n = new Random();
		String st = "";
		for (int i = 0; i < tab.length; i++) {
			st += ":" + weigths[i];// = 1/(tab.length);//Math.random()
									// ;//countingWord(tab[i], BlocText);
		}
		return st;
	}

	public void InitWeigths(String tag) {
		// Random n = new Random();
		/*
		 * for(int i=0;i<tab.length;i++){ weigths[i]=
		 * 1/(tab.length);//Math.random() ;//countingWord(tab[i], BlocText); }
		 */
		if (tab.length == 1) {
			weigths[0] = 1;
		}

		if (tab.length == 2) {
			weigths[0] = .5;
			weigths[1] = .5;
			/*
			 * if(tag.toLowerCase().contains(tab[0])){ weigths[0]=.5; }
			 * if(tag.toLowerCase().contains(tab[1])) weigths[1]=.5;
			 */
			return;
		}
		if (tab.length == 3) {
			int i = 3;
			weigths[0] = 1.0 / 2.5;
			weigths[1] = 1.0 / 2.5;
			weigths[2] = 1.0 / 5.0;
			/*
			 * for(i=0;i<3;i++){ if(tag.toLowerCase().contains(tab[i])){
			 * weigths[i]=1.0/(2.5); }else weigths[i]=1.0/5.0; }
			 */
			return;
		}
		if (tab.length == 4) {
			int i = 3;
			weigths[0] = 1.0 / 3.0;
			weigths[1] = 1.0 / 3.0;
			weigths[2] = 1.0 / 6.0;
			weigths[3] = 1.0 / 6.0;
			/*
			 * for(i=0;i<4;i++){ if(tag.toLowerCase().contains(tab[i])){
			 * weigths[i]=1.0/3.0; }else weigths[i]=1.0/6.0; }
			 */
			return;
		}
		if (tab.length > 4) {
			int i = 3;
			weigths[0] = 1.0 / 4.0;
			weigths[1] = 1.0 / 4.0;
			int N = tab.length;
			for (i = 2; i < N; i++) {
				weigths[i] = 1.0 / (2 * (N - 2));
				// weigths[i]=1.0/N;
			}
			return;
		}
	}

	public Boolean hasMoreImage(String Webpage) {
		Boolean ok = false;
		int nb;
		nb = getAllImage_img(Webpage);
		if (nb != 0)
			ok = true;
		return ok;
	}

	public static void setCookies(List<String> cookies) {
		cookies = cookies;
	}

	public String GetPageContent(String url) throws Exception {
	
		URL obj = new URL(url);
	
		/*
		 * HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier()
		 * { public boolean verify(String hostname, SSLSession session) { return
		 * true; } });
		 */
		// BYPASS SSL CONNECTION
		SSLContext context = SSLContext.getInstance("TLS");
		context.init(null, new X509TrustManager[] { new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}
	
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}
	
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}
		} }, new SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
	
		conn = (HttpsURLConnection) obj.openConnection();
	
		// default is GET
		conn.setRequestMethod("GET");
	
		conn.setUseCaches(false);
	
		// act like a browser
		conn.setRequestProperty("User-Agent", USER_AGENT);
		conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		if (cookies != null) {
			for (String cookie : cookies) {
				conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
			}
		}
		int responseCode = conn.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);
	
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
	
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
	
		// Get the response cookies
		setCookies(conn.getHeaderFields().get("Set-Cookie"));
	
		return response.toString();
	
	}

	public ArrayList<ImageInfos> probability_to_img_lis(double prob) {
		ArrayList<ImageInfos> ls = new ArrayList<ImageInfos>();
		for (ImageInfos e : list_images_relevant) {
			if (Double.valueOf(e.probability) >= prob)
				ls.add(e);
		}
		return ls;
	}

	public ArrayList<ImageInfos> getListImgs() {
		return list_images_relevant;
	}

	public double computeScore(String WebPage, String q_r, Document doc2, double treeshold) {

		PageScore = getScoreImgsTag(WebPage, query, doc2, treeshold);
		return PageScore;

	}

	public void parsePageConcept(String[] pageUrl, String query, double treeshold) {
		Document doc = null;
		// String query="obama";
		for (String st : pageUrl) {
			try {
				String result;
				/*
				 * if(st.contains("https")) result = GetPageContenthttps(st);
				 * else result = GetPageContenthttp(st);
				 */

				try {
					doc = Jsoup.connect(st).get();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// doc=Jsoup.parse(result);
				double score = computeScore(st, query, doc, treeshold);
				// System.out.println("Web page: "+st+"\nScore is :"+score);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public double getScoreImgsTag(String pageDOM, String query, Document doc, double treeshold) {
		double score = 0.0;
		if (doc == null)
			return -1.0;
		Elements images = doc.select("img");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		FileWriter fw = null;
		BufferedWriter bw = null;

		FileWriter fw_pos = null;
		BufferedWriter bw_pos = null;

		FileWriter fw_neg = null;
		BufferedWriter bw_neg = null;
		try {
			fw = new FileWriter(file.getAbsoluteFile(), true);
			bw = new BufferedWriter(fw);

			fw_pos = new FileWriter(file_pos.getAbsoluteFile(), true);
			bw_pos = new BufferedWriter(fw_pos);

			fw_neg = new FileWriter(file_neg.getAbsoluteFile(), true);
			bw_neg = new BufferedWriter(fw_neg);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int image_perpage = images.size();
		System.out.println("Web page " + pageDOM + " Number of images =" + images.size());
		for (Element img1 : images) {
			// img1.
			String st = "";
			int[] freq1, freq2, freq = new int[tab.length];
			freq1 = FrequencyQuery(query, img1.attr("src")); // if the src
																// contain the
																// person name
																// create a new
																// Image object
																// and add it up
																// tothe new
																// Imgage list
			freq2 = FrequencyQuery(query, img1.attr("alt"));
			for (int i = 0; i < tab.length; i++) {
				freq[i] = freq1[i] + freq2[i];// s+=(weigths[i]*freq[i])/(Bloclength);
			}
			// compute the length of the img
			int Bloclength = countWords(img1.attr("src")) + countWords(img1.attr("alt"));
			// sum over each concept
			// weigths(concept)*frequence(concept)/length of the img
			score = 0.0;
			double s = 0.0;
			if (Bloclength != 0) {
				for (int i = 0; i < tab.length; i++) {
					s += (weigths[i] * freq[i]) / (Bloclength);
				}
			}
			// Normalize
			// add up to the score
			if (s < 0.000001)
				continue;
			score += s;

			// second bloc compute the distance to the text
			Elements texts = doc.select(tag_p);
			// System.out.println("\n Text \""+tag_p+"\" list size :"
			// +texts.size() );

			int[] freq3 = new int[tab.length];
			for (Element text : texts) {

				if (text.text().length() != 0) {
					freq = FrequencyQuery(query, text.text());
					/*
					 * for(int i=0;i<tab.length;i++){ //System.out.println(
					 * "Frequence "+tab[i]+" : "+freq[i]); }
					 */
					// compute the length of the text
					Bloclength = countWords(text.text());
					// System.out.println("Bloc length iteration :
					// "+text.text()+" is:" + Bloclength);
					// sum over each concept
					// weigths(concept)*frequence(concept)/length of the text
					s = 0.0;
					if (Bloclength != 0) {
						for (int i = 0; i < tab.length; i++) {
							// Normalize distance from image tag to text
							// System.out.println("distance block :" +
							// distance(img1,text, doc));
							s += (weigths[i] * freq[i]) / (distance(img1, text, doc) * (Bloclength));
							// s+=(weigths[i]*freq[i])/((Bloclength));
						}
						// System.out.println("score iteration :" + s);
					}
					// add up to the score if the block's score is greater than
					// treshold 0.00001
					if (s >= 0.00001)
						score += s;
					// System.out.println("score iteration : "+text.text()+"
					// is:" + score);
				}
			}

			if (Double.isInfinite(score) || Double.isNaN(score)) {
				// st+=""+pageDOM+" "+img1.attr("src")+" "+score+" #valeur"+"
				// "+image_perpage+"\n";
				st += "" + showWeigths() + " " + query + " " + pageDOM + " " + img1.attr("src") + " " + score + " "
						+ "#valeur" + " " + image_perpage + " "
						+ new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()) + "\n";

				try {

					bw_neg.write(st);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				// score/=image_perpage;
				double C = 1.0;
				double prob = 1.0 / (C * Math.pow((1.0 + Math.exp(-(score - 0.12) / 0.01)), 2.));
				// Date d = new Date();
				st += "" + showWeigths() + " " + query + " " + pageDOM + " " + img1.attr("src") + " " + score + " "
						+ prob + " " + image_perpage + " "
						+ new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()) + "\n";

				/*
				 * ImageInfos im = new ImageInfos(pageDOM, img1.attr("src"),
				 * String.valueOf(prob),searchDomain); liss.add(im);
				 */
				if (prob > treeshold) {
					try {
						if (img1.attr("src").indexOf("http") != -1 || img1.attr("src").indexOf("https") != -1) {

							if (score > 0.0) {
								ImageInfos im = new ImageInfos(pageDOM, img1.attr("src"), String.valueOf(prob),
										searchDomain);
								list_images_relevant.add(im);
							}

						}

						bw_pos.write(st);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					try {

						bw_neg.write(st);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
			// System.out.print(st);

			try {

				bw.write(st);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		try {
			bw.close();
			bw_neg.close();
			bw_pos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return score;
	}

	public String GetPageContenthttps(String url) throws Exception {
		
		String result = null;
		StringBuffer response=null;
		try
		  {
			URL obj = new URL(url);

		/*
		 * HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier()
		 * { public boolean verify(String hostname, SSLSession session) { return
		 * true; } });
		 */
		// BYPASS SSL CONNECTION
		SSLContext context = SSLContext.getInstance("TLS");
		context.init(null, new X509TrustManager[] { new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}
		} }, new SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());

		conn = (HttpsURLConnection) obj.openConnection();

		// default is GET
		conn.setRequestMethod("GET");

		conn.setUseCaches(false);

		// act like a browser
		conn.setRequestProperty("User-Agent", USER_AGENT);
		conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		if (cookies != null) {
			for (String cookie : cookies) {
				conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
			}
		}
		try{
		int responseCode = conn.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String inputLine;
		response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		result = response.toString();
		// Get the response cookies
		setCookies(conn.getHeaderFields().get("Set-Cookie"));
		  }catch(IOException ex){
			  System.err.println("Response Code: "+ex.getMessage());
		  }
		  }catch(IOException ex){
			  System.err.println("Error connexion "+ex.getMessage());
		
	}

		return result;

	}

	public String GetPageContenthttp(String url) throws Exception {

		String result =null;
		StringBuffer response=null;
		try
		  {
		URL obj = new URL(url);

		/*
		 * HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier()
		 * { public boolean verify(String hostname, SSLSession session) { return
		 * true; } });
		 */
		// BYPASS SSL CONNECTION
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

		// default is GET
		conn.setRequestMethod("GET");

		conn.setUseCaches(false);

		// act like a browser
		conn.setRequestProperty("User-Agent", USER_AGENT);
		conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		if (cookies != null) {
			for (String cookie : cookies) {
				conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
			}
		}
		try{
		int responseCode = conn.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String inputLine;
		 response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		result = response.toString();
		// Get the response cookies
		setCookies(conn.getHeaderFields().get("Set-Cookie"));
		  }catch(IOException ex){
			  System.err.println("Response Code: "+ex.getMessage());
		}
	
		  }catch(IOException ex){
			  System.err.println("Error connexion "+ex.getMessage());
		}
	return result;

}

	public int  extract(String query, String st, String techno) {
		//System.out.println("Score computing");

		ArrayList<String> list_queries = new ArrayList<String>();
		list_queries.add(query);
		// for(String q: list_queries){
		// String quer = q.getQuery();
		// String q_r=gettokens(q);
		// System.out.println("Query is :"+quer);

		// for (String st: dashboard){

		/*
		 * String [] list_urls = st.split(";"); int nb_link =10;
		 */
		// for (String s:list_urls){

		try {
			String result;
			if (st.contains("https"))
				result = GetPageContenthttps(st);
			else
				result = GetPageContenthttp(st);
			
			if(result==null){
				System.err.println("Query: "+query+"\nPage: "+st+"\nHave not Url Stream");
				return -1;
			}
			/*
			 * try { doc = Jsoup.connect(s).get(); } catch (IOException e) { //
			 * TODO Auto-generated catch block e.printStackTrace(); }
			 */
			doc = Jsoup.parse(result);
			double score = 0.0;
			if (techno.equalsIgnoreCase("cd")) {
				/*
				 * String query = "Celebrat"; tab = query.split(" ") ;
				 */
				tab = query.split(" ");
				weigths = new double[tab.length];
				// InitWeigths();
				weigths[0] = 1.0;
				// weigths[1]=0.5;
				// weigths[2]=0.5;
				long startTime = System.currentTimeMillis();
				treeshold = 0.51;
				// parsePageConcept(celebrating,query,treeshold);
				score = computeScore(st, query, doc, treeshold);
			} else if (techno.equalsIgnoreCase("pi")) {
				// tab = q.split(" ") ;
				String[] list_key = query.split(" ");
				tab = pre_processquery(list_key);
				weigths = new double[tab.length];
				InitWeigths(tag);
				/*
				 * System.out.print("Weigths :"); for(int i=0;i<tab.length;i++){
				 * // Normalize distance from image tag to text
				 * System.out.print(tab[i]+" _ " + weigths[i]); }
				 * Thread.sleep(2000);
				 */
				// weigths[0]=1;
				treeshold = 0.6;
				score = computeScore(st, query, doc, treeshold);
			}
			System.out.println("Web page: " + st + "\nScore is :" + score);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * if(nb_link==10) break; nb_link++;
		 */
		// }
		return 1;

	}
	/*
	 * if(cpt==10) break; cpt++;
	 */
	// }

	public double getScoreImgsTag() {
		double score = 0.0;
		String page = "";
		// get all images
		// doc = Jsoup.connect("http://yahoo.com").get();
		CookieHandler.setDefault(new CookieManager());

		// 1. Send a "GET" request, so that you can extract the form's data.
		try {
			page = GetPageContent(Webpage);
			//System.out.println(page);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		doc = Jsoup.parse(page);
		Elements images = doc.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
		// nb = images.size();
		System.out.println("\nImages list size : " + images.size());
		for (Element img1 : images) {

			System.out.println("\nsrc : " + img1.attr("src"));
			// System.out.println("height : " + img.attr("height"));
			// System.out.println("width : " + img.attr("width"));
			System.out.println("\nalt : " + img1.attr("alt"));
			// Compute frequency of concepts of the queryin the alt and src
			// attribute
			// if(img1.attr("src").contains(img.getImagePerson())){
			int[] freq1, freq2, freq = new int[tab.length];
			freq1 = FrequencyQuery(query, img1.attr("src")); // if the src
																// contain the
																// person name
																// create a new
																// Image object
																// and add it up
																// tothe new
																// Imgage list
			freq2 = FrequencyQuery(query, img1.attr("alt"));
			for (int i = 0; i < tab.length; i++) {
				freq[i] = freq1[i] + freq2[i];// s+=(weigths[i]*freq[i])/(Bloclength);
			}
			// compute the length of the img
			int Bloclength = countWords(img1.attr("src")) + countWords(img1.attr("alt"));
			// sum over each concept
			// weigths(concept)*frequence(concept)/length of the img
			double s = 0.0;
			if (Bloclength != 0) {
				for (int i = 0; i < tab.length; i++) {
					s += (weigths[i] * freq[i]) / (Bloclength);
				}
			}
			// Normalize
			// add up to the score
			score += s;

			// second bloc compute the distance to the text
			Elements texts = doc.select(tag_p);
			//System.out.println("\n Text \"" + tag_p + "\" list size :" + texts.size());

			int[] freq3 = new int[tab.length];
			for (Element text : texts) {

				if (text.text().length() != 0) {
					freq = FrequencyQuery(query, text.text());
					/*
					 * for(int i=0;i<tab.length;i++){ //System.out.println(
					 * "Frequence "+tab[i]+" : "+freq[i]); }
					 */
					// compute the length of the text
					Bloclength = countWords(text.text());
					// System.out.println("Bloc length iteration :
					// "+text.text()+" is:" + Bloclength);
					// sum over each concept
					// weigths(concept)*frequence(concept)/length of the text
					s = 0.0;
					if (Bloclength != 0) {
						for (int i = 0; i < tab.length; i++) {
							// Normalize distance from image tag to text
							s += (weigths[i] * freq[i]) / (distance(img1, text, doc) * (Bloclength));
						}
						// System.out.println("score iteration :" + s);
					}

					// add up to the score
					score += s;
					// System.out.println("score iteration : "+text.text()+"
					// is:" + score);
				}
			}

		}
		return score;
	}

	/*
	 * if(cpt==10) break; cpt++;
	 */
	// }

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

	private int countingWord(String value, String findWord) {
		int counter = 0;
		/*
		 * value=value.toLowerCase(); findWord = findWord.toLowerCase(); while
		 * (value.contains(findWord)) { int index = value.indexOf(findWord);
		 * value = value.substring(index + findWord.length(), value.length());
		 * counter++; }
		 */

		String patternString = value;

		Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(findWord);

		int count = 0;
		while (matcher.find()) {
			count++;
			/*
			 * System.out.println("found: " + count + " : " + matcher.start() +
			 * " - " + matcher.end());
			 */
		}

		return count;
	}

	public int countWords(String s) {
		/*
		 * int count = 1; for (int i=0;i<=str.length()-1;i++) {
		 * 
		 * if (str.charAt(i) == ' ' &&(str.charAt(i+1)!=' '||
		 * str.charAt(i+1)!='\'')) { count++; } } //System.out.println(
		 * "Number of word is "+count); return count;
		 */
		int wordCount = 0;

		boolean word = false;
		int endOfLine = s.length() - 1;

		for (int i = 0; i < s.length(); i++) {
			// if the char is a letter, word = true.
			if (Character.isLetter(s.charAt(i)) && i != endOfLine) {
				word = true;
				// if char isn't a letter and there have been letters before,
				// counter goes up.
			} else if (!Character.isLetter(s.charAt(i)) && word) {
				wordCount++;
				word = false;
				// last word of String; if it doesn't end with a non letter, it
				// wouldn't count without this.
			} else if (Character.isLetter(s.charAt(i)) && i == endOfLine) {
				wordCount++;
			}
		}
		return wordCount;
	}

	public void getLinks_a() {

		/*
		 * Note It’s recommended to specify a “userAgent” in Jsoup, to avoid
		 * HTTP 403 error messages. Document doc =
		 * Jsoup.connect("http://anyurl.com") .userAgent("Mozilla") .get();
		 */

		Document doc;
		try {

			// need http protocol
			doc = Jsoup.connect("http://google.com").get();

			// get page title
			String title = doc.title();
			System.out.println("title : " + title);

			// get all links
			Elements links = doc.select("a[href]");
			for (Element link : links) {

				// get the value from href attribute
				System.out.println("\nlink : " + link.attr("href"));
				System.out.println("text : " + link.text());

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public int getAllImage_img(String Webpage) {
		int nb = 0;
		Document doc;
		try {

			// get all images
			// doc = Jsoup.connect("http://yahoo.com").get();
			doc = Jsoup.connect(Webpage).get();
			Elements images = doc.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
			nb = images.size();

			/*
			 * for (Element image : images) {
			 * 
			 * System.out.println("\nsrc : " + image.attr("src"));
			 * System.out.println("height : " + image.attr("height"));
			 * System.out.println("width : " + image.attr("width"));
			 * System.out.println("alt : " + image.attr("alt"));
			 * 
			 * }
			 */

		} catch (IOException e) {
			e.printStackTrace();
		}
		return nb;

	}

}
