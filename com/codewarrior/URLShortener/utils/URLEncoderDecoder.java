package com.codewarrior.URLShortener.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class URLEncoderDecoder {
	private final static String keys = "1234567890abcdefghijklmnopqrstuvxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private final static int BASE_TOTAL = 62;
	
	// convert base 10 to base 62
	public String encode(long key) {
		List<Integer> encoded = new ArrayList<Integer>();
		while(key>0) {
			int rem = (int) (key % BASE_TOTAL);
			encoded.add(rem);
			key = (long) Math.floor(key/BASE_TOTAL);
		}
		Collections.reverse(encoded);
		StringBuffer shortURL = new StringBuffer();
		encoded.stream().forEach(num -> shortURL.append(keys.charAt(num)));
		return shortURL.toString();
	}
	
	// convert base 62 to base 10
	public long decode(String url) {
		long decoded = 0L;
		for(int i=url.length()-1;i>=0;--i) {
			decoded += Math.pow(BASE_TOTAL, url.length()-i-1)*(keys.indexOf(url.charAt(i)));
		}
		return decoded;
	}
}
