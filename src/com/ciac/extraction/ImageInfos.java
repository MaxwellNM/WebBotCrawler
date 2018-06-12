package com.ciac.extraction;

/**
 * @author Maxwell Ndognkon Manga
 * <p>Mail: maxwellndognkong@gmail.com</p>
 * <p>Date: 17/09/2016</p>
 * @version 2.0
 * <p>file: ImageInfos.java</p>
 *  Serializable class to record crawling experience and push them for stats 
 * */
public class ImageInfos {
	String ImageWebUrl;
	String ImageSrc;
	String probability;
	String searchDomain;

	public ImageInfos(String cd, String lema, String gl, String search) {
		ImageWebUrl = cd;
		ImageSrc = lema;
		probability = gl;
		searchDomain = search;

	}

	public String getImageWebUrl() {
		return ImageWebUrl;
	}

	public String getImageDomain() {
		return searchDomain;
	}

	public String getImageProbability() {
		return probability;
	}

	public String getImageSrce() {
		return ImageSrc;
	}

	public boolean Equals(ImageInfos im) {
		boolean ok = false;
		if (ImageSrc.equalsIgnoreCase(im.ImageSrc))
			// if(ImageWebUrl.equalsIgnoreCase(im.ImageWebUrl)&&ImageSrc.equalsIgnoreCase(im.ImageSrc))
			ok = true;
		return ok;
	}

}
