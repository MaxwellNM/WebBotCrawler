package com.ciac.Image;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * @author Maxwell Ndognkon Manga 
 * <p>Mail: maxwellndognkong@gmail.com</p>
 * <p>Date: 17/09/2016</p>
 * @version 2.0
 * <p>file: com.ciac.Image.Image_cr.java</p>
 *  Image Object peristent entity
 * */
public class Image_cr implements Serializable {

	private String id = "000000000000000000000000";
	private String image_url;
	private String image_name;
	private String image_src_page;
	private String image_width;
	private String image_height;
	private String image_person; // get from query
	private String image_query;
	private Metadata image_metadata;

	public class Metadata {

		public Metadata() {

		}

		String image_type;
		String image_description;
		ArrayList<String> keywords;
		String image_origin; // web site containing image

		public String getType() {
			return image_type;

		}

		public String getDescription() {
			return image_description;

		}

		public String getOrigin() {
			return image_origin;

		}

		public ArrayList<String> getKeywords() {
			return keywords;

		}
	}

	public Image_cr() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getImageUrl() {
		return image_url;
	}

	public void setImageUrl(String url) {
		image_url = url;
	}

	public String getImageName() {
		return image_name;
	}

	public void setImageName(String name) {
		image_name = name;
	}

	public String getImageSrcPage() {
		return image_src_page;
	}

	public void setImageSrcPage(String srcPage) {
		image_src_page = srcPage;

	}

	public String getImageWidth() {
		return image_width;
	}

	public void setImageWidth(String width) {
		image_width = width;
	}

	public String getImageHeight() {
		return image_height;
	}

	public void setImageHeight(String height) {
		image_height = height;
	}

	public String getImagePerson() {
		return image_person;
	}

	public void setImagePerson(String person) {
		image_person = person;
	}

	public String getImageQuery() {
		return image_query;
	}

	public void setImageQuery(String Query) {
		image_query = Query;
	}

	public void setImageMetadata(String type, String description, ArrayList<String> keywords, String origin) {
		Metadata m = new Metadata();
		m.image_type = type;
		m.image_description = description;
		m.keywords = keywords;
		m.image_origin = origin;

		image_metadata = m;
	}

	public Metadata getImageMetadat() {
		return image_metadata;
	}

	public String printImage() {
		String st = "";
		st += "\nUrl=" + getImageUrl();
		st += "\nSrc" + getImageSrcPage();
		st += "\nW=" + getImageWidth();
		st += "\nH=" + getImageHeight();
		st += "\n";

		return st;

	}
}
