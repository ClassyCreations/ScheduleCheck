package com.herocc.school.aspencheck;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GenericWebFetch {
	public String agent = "ScheduleGrab Bot";
	public Map<String, String> demCookies = new HashMap<>();
	
	public Connection.Response getPage(String url) throws IOException {
			return Jsoup.connect(url)
							.userAgent(agent)
							.timeout(10 * 1000)
							.cookies(demCookies)
							.followRedirects(true)
							.execute();
	}
}
