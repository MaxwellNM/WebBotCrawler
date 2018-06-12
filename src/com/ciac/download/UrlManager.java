package com.ciac.download;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;

/**
 * @author Maxwell Ndognkon Manga
 * <p>Mail: maxwellndognkong@gmail.com</p>
 * <p>Date: 17/09/2016</p>
 * @version 2.0
 * <p>file: UrlManager.java</p>
 *  Crawler Download images
 * */
public class UrlManager {

	protected String Folder;
	protected int Nbr_down_links;
	protected int Nbrlinks;

	public UrlManager() {

	}

	public UrlManager(String fold) {
		Folder = fold;
		Nbr_down_links = 0;
		Nbrlinks = 0;
	}

	public String getNameDataset() {

		return Folder;

	}

	public void SetNameDataset(String st) {

		this.Folder = st;

	}

	public void precision() {
		System.out.println("Found links =" + Nbrlinks);
		System.out.println("Death links =" + Nbr_down_links);
		System.out.println("Percentage of death links(precision) =" + (Nbr_down_links / Nbrlinks));
	}

	// Test if an URL exists
	public boolean exists(String URLName) throws Exception {
		try {
			HttpURLConnection.setFollowRedirects(false);
			// note : you may also need
			// HttpURLConnection.setInstanceFollowRedirects(false)
			HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
			con.setRequestMethod("HEAD");
			int code=-1;
			try{
				code= con.getResponseCode();
			}catch(Exception e){
				System.err.println("Image Url Not found: "+URLName+" "+e.getMessage());
				return false;
			}
			return (code == HttpURLConnection.HTTP_OK);
		} catch (Exception e) {
			//e.printStackTrace();
			System.err.println("Image Url Not found: "+URLName+" "+e.getMessage());
			return false;
		}
	}

	public BufferedImage ImageByteToBuffered(byte[] imageByte) {
		BufferedImage bImageFromConvert = null;
		InputStream in = new ByteArrayInputStream(imageByte);
		try {
			bImageFromConvert = ImageIO.read(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bImageFromConvert;
	}

	public byte[] getImageByte(String URLimg) throws IOException {
		byte[] response = null;
		boolean rt=false;
		try{
			rt = exists(URLimg);
		}catch(Exception e){
			System.err.println(URLimg+": "+e.getMessage());
		}
		if (rt) { // If it is not a death link

			URL url = new URL(URLimg);
			InputStream in = new BufferedInputStream(url.openStream());
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int n = 0;
			while (-1 != (n = in.read(buf))) {
				out.write(buf, 0, n);
			}
			out.close();
			in.close();
			response = out.toByteArray();

			/*
			 * String[] list = URLimg.split("/"); String imName =
			 * list[list.length-1];
			 * 
			 * String[] strs = null; try{ strs = imName.split("\\?"); }
			 * catch(java.util.regex.PatternSyntaxException e){
			 * //e.printStackTrace(); }
			 */
			/*
			 * if(strs != null)imName = strs[0];
			 * 
			 * //save the image on the disk server // Save images in Flickr web
			 * server Directory FileOutputStream fos = new
			 * FileOutputStream(Folder+""+File.separator+""+imName);
			 * fos.write(response); fos.close();
			 */
		} else {
			Nbr_down_links++;
			System.out.println("Bad Url " + URLimg);
		}
		return response;
	}

	public void getImage(String URLimg) throws IOException {

		boolean rt=false;
		try{
			rt = exists(URLimg);
		}catch(Exception e){
			System.err.println(URLimg+": "+e.getMessage());
		}
		if (rt) { // If it is not a death link

			URL url = new URL(URLimg);
			InputStream in = new BufferedInputStream(url.openStream());
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int n = 0;
			while (-1 != (n = in.read(buf))) {
				out.write(buf, 0, n);
			}
			out.close();
			in.close();
			byte[] response = out.toByteArray();

			String[] list = URLimg.split("/");
			String imName = list[list.length - 1];

			String[] strs = null;
			try {
				strs = imName.split("\\?");
			} catch (java.util.regex.PatternSyntaxException e) {
				// e.printStackTrace();
			}
			if (strs != null)
				imName = strs[0];

			// save the image on the disk server
			// Save images in Flickr web server Directory
			FileOutputStream fos = new FileOutputStream(Folder + "" + File.separator + "" + imName);
			fos.write(response);
			fos.close();
		} else {
			Nbr_down_links++;
			System.out.println("Bad Url " + URLimg);
		}
	}

	/**
	 * Get the List of UrL Image comming from a given sites
	 * 
	 * @param Site_link:
	 *            the link of image-net
	 */
	public String[] getText(String Site_link) {

		String[] tab;
		try {
			URL url = new URL(Site_link);
			URLConnection urlConnection = url.openConnection();
			InputStream is = urlConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);

			int numCharsRead;
			char[] charArray = new char[1024];
			StringBuffer sb = new StringBuffer();
			while ((numCharsRead = isr.read(charArray)) > 0) {
				sb.append(charArray, 0, numCharsRead);

			}
			String result = sb.toString();
			tab = result.split("\n");

			return tab;
			// Vector<String> tabURL = new Vector<String>(tab);
			// tabURL.addAll(tab);
			/*
			 * System.out.println("*** BEGIN ***"); System.out.println(result);
			 * System.err.println("Last Element  = "+tab[tab.length-1]);
			 * System.out.println("*** END ***");
			 */
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String[] GetURLSites(String url) throws Exception {
		URL website = new URL(url);
		URLConnection connection = website.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

		StringBuilder response = new StringBuilder();
		String inputLine;

		while ((inputLine = in.readLine()) != null)
			response.append(inputLine + ";");

		in.close();

		String result = response.toString();
		String[] tab = result.split(";");
		Nbrlinks = tab.length;
		return tab;

		// return response.toString();
	}

}
