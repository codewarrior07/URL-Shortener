package com.codewarrior.URLShortener.exceptions;

public class InvalidURLException extends Exception {
	private static final String errorMessage = "Invalid URL";
	
	@Override
	public String toString() {
		return this.errorMessage;
	}
}
