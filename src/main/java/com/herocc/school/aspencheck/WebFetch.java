package com.herocc.school.aspencheck;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WebFetch {
	private String agent = "ScheduleGrab Bot";
	private Map<String, String> demCookies = new HashMap<>();
	
	public Connection.Response schedulePage() throws IOException {
		try {
			return Jsoup.connect("https://ma-melrose.myfollett.com/aspen/studentScheduleContextList.do?navkey=myInfo.sch.list")
							.userAgent(agent)
							.referrer("https://ma-melrose.myfollett.com/aspen/portalStudentDetail.do?navkey=myInfo.details.detail")
							.timeout(10 * 1000)
							.cookies(demCookies)
							.followRedirects(true)
							.execute();
		} catch (HttpStatusException e){
			if (!AspenCheck.quiet) System.out.println("Login details incorrect, or Aspen is having issues, please try again later!");
			if (AspenCheck.debug && !AspenCheck.quiet) e.printStackTrace();
			return null;
		}
	}
	
	public Connection.Response login(String username, String password) throws IOException {
		String loginUrl = "https://ma-melrose.myfollett.com/aspen/logon.do";
		Connection.Response loginPageResponse =
						Jsoup.connect(loginUrl)
										.userAgent(agent)
										.timeout(10 * 1000)
										.followRedirects(true)
										.execute();
		
		if (AspenCheck.debug && !AspenCheck.quiet) System.out.println("Fetched login page");
		
		Map<String, String> mapLoginPageCookies = loginPageResponse.cookies();
		Map<String, String> mapParams = new HashMap<>();
		mapParams.put("deploymentId", "ma-melrose");
		mapParams.put("userEvent", "930");
		mapParams.put("username", username);
		mapParams.put("password", password);
		mapParams.put("mobile", "false");
		
		if (username == null || password == null){
			System.out.println("Username or Password not specified!");
			System.exit(1);
		}
		
		Connection.Response responsePostLogin = Jsoup.connect(loginUrl)
						//referrer will be the login page's URL
						.referrer(loginUrl)
						//user agent
						.userAgent(agent)
						//connect and read time out
						.timeout(10 * 1000)
						//post parameters
						.data(mapParams)
						//cookies received from login page
						.cookies(mapLoginPageCookies)
						//many websites redirects the user after login, so follow them
						.followRedirects(true)
						.execute();
		
		if (AspenCheck.debug && !AspenCheck.quiet) System.out.println("HTTP Status Code: " + responsePostLogin.statusCode());
		Map<String, String> mapLoggedInCookies = responsePostLogin.cookies();
		demCookies.putAll(mapLoggedInCookies);
		demCookies.putAll(mapLoginPageCookies);
		return responsePostLogin;
	}
}
