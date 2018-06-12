package com.ciac.Image;

import java.io.Serializable;

/**
 * @author Maxwell Ndognkon Manga 
 * <p>Mail: maxwellndognkong@gmail.com</p>
 * <p>Date: 17/09/2016</p>
 * @version 2.0
 * <p>file: com.ciac.Image.Url.java</p>
 *  Url Object peristent entity
 * */
public class Url implements Serializable {

	private String id = "000000000000000000000000";
	private String url;
	private String score;
	private String index;

	public Url(String url1) {
		url = url1;
	}

	public Url() {
		// TODO Auto-generated constructor stub
	}

	public String getId() {
		return id;
	}

	public void setId(String i) {
		id = i;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String ur) {
		url = ur;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String s) {
		score = s;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String i) {
		index = i;
	}
}
