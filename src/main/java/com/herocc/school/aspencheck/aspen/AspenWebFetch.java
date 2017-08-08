package com.herocc.school.aspencheck.aspen;

import com.herocc.school.aspencheck.AspenCheck;
import com.herocc.school.aspencheck.GenericWebFetch;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AspenWebFetch extends GenericWebFetch {
	public Connection.Response schedulePage() throws IOException {
		try {
			return getPage("https://ma-melrose.myfollett.com/aspen/studentScheduleContextList.do?navkey=myInfo.sch.list");
		} catch (HttpStatusException e) {
			if (e.getStatusCode() == 404) {
        AspenCheck.log.severe("This login doesn't have a schedule page!");
      } else {
        AspenCheck.log.severe("Login details incorrect, or Aspen is having issues, please try again later!");
      }
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
		
		AspenCheck.log.finer("Fetched login page");
		
		Map<String, String> mapLoginPageCookies = loginPageResponse.cookies();
		Map<String, String> mapParams = new HashMap<>();
		mapParams.put("deploymentId", "ma-melrose");
		mapParams.put("userEvent", "930");
		mapParams.put("username", username);
		mapParams.put("password", password);
		mapParams.put("mobile", "false");
		
		if (username == null || password == null){
			AspenCheck.log.severe("Username or Password not specified!");
			return null;
		}
		
		Connection.Response responsePostLogin = Jsoup.connect(loginUrl)
						.referrer(loginUrl)
						.userAgent(agent)
						.timeout(10 * 1000)
						.data(mapParams)
						.cookies(mapLoginPageCookies)
						.followRedirects(true)
						.execute();
		
		Map<String, String> mapLoggedInCookies = responsePostLogin.cookies();
		demCookies.putAll(mapLoggedInCookies);
		demCookies.putAll(mapLoginPageCookies);
		return responsePostLogin;
	}
}
