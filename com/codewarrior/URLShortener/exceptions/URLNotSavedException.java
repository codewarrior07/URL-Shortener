package com.codewarrior.URLShortener.exception;

public class URLNotSavedException extends Exception {
	private static final long serialVersionUID = 1L;
	private static final String errorMessage = "Error saving URL";
	
	@Override
	public String toString() {
		return URLNotSavedException.errorMessage;
	}
}
