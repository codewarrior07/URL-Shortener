package com.codewarrior.URLShortener.dao;

import java.util.Date;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

public class URLShortenerDao {
	private static Cluster cluster;
	private static Session session;
	
	public URLShortenerDao(String host) {
		cluster = Cluster.builder().addContactPoint(host).build();
		session = cluster.connect("url_shortener");
	}
	
	public void saveURL(String url, long key) {
		PreparedStatement insertPs = session.prepare("INSERT INTO urls(url_id,long_url,isVisited) values(?,?,false)");
		BoundStatement bs = new BoundStatement(insertPs);
		bs.setLong(0, key);
		bs.setString(1, url);
		ResultSet result = session.execute(bs);
		if (!result.wasApplied()) {
			// TODO
			// throw new URLNotSavedException(); 
		}
	}
	
	public long getKey() {
		ResultSet result = session.execute("UPDATE url_counter set next_id=next_id+1 WHERE key='url_next_id';");
		if (result.wasApplied()) {
			result = session.execute("SELECT next_id from url_counter where key='url_next_id'");
			Row row = result.one();
			return row.getLong("next_id");
		}
		return -1;
	}
	
	public String getLongURLFromDB(long decodedKey) {
		PreparedStatement updatePs = session.prepare("UPDATE urls SET isVisited=true,lastvisited=? where url_id=?");
		BoundStatement bs = new BoundStatement(updatePs);
		bs.setTimestamp(0, new Date());
		bs.setLong(1, decodedKey);
		ResultSet result = session.execute(bs);
		if (result.wasApplied()) {
			result = session.execute("SELECT long_url FROM urls WHERE url_id="+decodedKey);
			Row row = result.one();
			return row.getString("long_url");
		}
		return null;
	}
}
