package com.herocc.school.aspencheck;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GenericWebFetch {
	public Map<String, String> demCookies = new HashMap<>();
	
	public Connection.Response getPage(String url) throws IOException {
			return Jsoup.connect(url)
							.userAgent(Configs.WEB_USER_AGENT)
							.timeout(10 * 1000)
							.cookies(demCookies)
							.followRedirects(true)
							.execute();
	}
  
  public Connection.Response getPage(String url, Map<String, String> formData) throws IOException {
    return Jsoup.connect(url)
      .userAgent(Configs.WEB_USER_AGENT)
      .timeout(10 * 1000)
      .data(formData)
      .cookies(demCookies)
      .followRedirects(true)
      .execute();
  }
	
	public static String getURL(String url) {
		GenericWebFetch g = new GenericWebFetch();
		try {
			return g.getPage(url).body();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
