package com.codewarrior.URLShortener.domain;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ShortenURL {
	private String longURL;

	public String getLongURL() {
		return longURL;
	}

	public void setLongURL(String longURL) {
		this.longURL = longURL;
	}
	
}
