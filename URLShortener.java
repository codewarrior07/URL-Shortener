package com.sriram.URLShortener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.sriram.URLShortener.exceptions.InvalidURLException;

public class URLShortener {
	private final static String keys = "1234567890abcdefghijklmnopqrstuvxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private final static int BASE_TOTAL = 62;
	private String URL_PREFIX = "http://siq.com/";
	private static MongoClient mongo;
	private MongoDatabase db;
	private MongoCollection<Document> urlCollection;
	private MongoCollection<Document> countersCollection;
	
	public URLShortener() {
		mongo = new MongoClient("localhost",27017);
		db = mongo.getDatabase("url_shortener");
		urlCollection = db.getCollection("urls");
		countersCollection = db.getCollection("counters");
	}
	public static void main(String[] e) throws InvalidURLException {
		URLShortener us = new URLShortener();
		String url = "http://www.google.com";
		String shortURL = us.shortenURL(url);
		System.out.println(shortURL);
		System.out.println(us.getLongURL("2D"));
		mongo.close();
	}
	
	public String shortenURL(String url) throws InvalidURLException {
		if(url == null || url.length()==0 || !isValidURL(url))
			throw new InvalidURLException();
		String key = saveURL(url);
		String shortURL = URL_PREFIX + key;
		return shortURL;
	}
	
	public String getLongURL(String shortURL) throws InvalidURLException {
		if(shortURL == null || shortURL.length()==0 || !isValidURL(shortURL))
			throw new InvalidURLException();
		String base62Key = shortURL.substring(shortURL.lastIndexOf(URL_PREFIX)+1);
		String longURL = getLongURLFromDB(decode(base62Key));
		return longURL;
	}
		
	private String getLongURLFromDB(long base62Key) {
		Document query = new Document();
		query.put("_id", base62Key);
		
		Document updateBody = new Document();
		updateBody.put("isVisited", true);
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		String currDateTime = dateFormat.format(date);
		updateBody.put("lastVisitedDate", currDateTime);
		
		Document update = new Document();
		update.put("$set", updateBody);
		
		FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
		options.returnDocument(ReturnDocument.AFTER);
		options.upsert(false);
		Document updatedDoc = urlCollection.findOneAndUpdate(query, update, options);
		return updatedDoc.getString("longURL");
	}
	
	private String saveURL(String URL) {
		long key = getKey();
		String shortURL = encode(key);
		Document newURLDoc = new Document();
		newURLDoc.put("_id", key);
		newURLDoc.put("longURL", URL);
		urlCollection.insertOne(newURLDoc);
		return shortURL;
	}
	
	private long getKey() {
		Document query = new Document("_id","url_count");
		Document incrementSeq = new Document();
		incrementSeq.put("seq", 1);
		Document update = new Document("$inc", incrementSeq);
		
		FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
		options.returnDocument(ReturnDocument.AFTER);
		options.upsert(false);
		
		Document updatedDoc = countersCollection.findOneAndUpdate(query, update, options);
		return updatedDoc.getDouble("seq").longValue();
	}
	
	// convert base 10 to base 62
	private String encode(long key) {
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
	private long decode(String url) {
		long decoded = 0L;
		for(int i=url.length()-1;i>=0;--i) {
			decoded += Math.pow(BASE_TOTAL, url.length()-i-1)*(keys.indexOf(url.charAt(i)));
		}
		return decoded;
	}
	
	// TODO
	private boolean isValidURL(String url) {
		return true;
	}
}
