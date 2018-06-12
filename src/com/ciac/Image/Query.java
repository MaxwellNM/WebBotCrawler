package com.ciac.Image;

import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Maxwell Ndognkon Manga 
 * <p>Mail: maxwellndognkong@gmail.com</p>
 * <p>Date: 17/09/2016</p>
 * @version 2.0
 * <p>file: com.ciac.Image.Query.java</p>
 *  Query Object peristent entity
 * */
public class Query implements Serializable {

	private String id = "000000000000000000000000";
	private String person;
	private String domain;
	private String query;
	private Date created_on;
	private Date udpated_on;

	public Query() {

	}

	public String getId() {

		return id;
	}

	public void setId(String st) {

		id = st;
	}

	public String getPerson() {
		return this.person;
	}

	public void setPerson(String name) {
		person = name;
	}

	public String getDomain() {
		return this.domain;
	}

	public void setDomain(String name) {
		domain = name;
	}

	public String getQuery() {
		return this.query;
	}

	public void setListQuery(String q) {
		query = q;
	}

	public Date getDateCreate() {
		return this.created_on;
	}

	public void setDateCreate(Date d) {
		created_on = d;
	}

	public Date getDateUpdate() {
		return this.udpated_on;
	}

	public void setDateUpdate(Date d) {
		udpated_on = d;
	}

}
