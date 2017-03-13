package com.herocc.school.schedulecheck;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScheduleCheck {
	public String agent = "ScheduleGrab Bot";
	public Map<String, String> demCookies = new HashMap<>();
	
	@Parameter(names = {"--username", "-u"}, description = "Aspen Username")
	private String username;
	
	@Parameter(names = {"--password", "-p"}, description = "Aspen password", password = true)
	private String password;
	
	@Parameter(names = {"--debug", "-d"})
	private boolean debug = false;
  
	public static void main(String[] args){
		ScheduleCheck scheduleCheck = new ScheduleCheck();
		new JCommander(scheduleCheck, args);
		scheduleCheck.actuallyMain(args);
	}
	
	private void actuallyMain(String[] args){
		getLoginDetails();
		try {
			loginResponse();
			Document schedPage = schedulePage().parse();
			System.out.println("Day: " + getDay(schedPage));
			System.out.println("Class: " + getClass(schedPage));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void getLoginDetails(){
		if (username == null || password == null) {
			File credsFile = new File(System.getProperty("user.dir") + "creds.txt");
			if (credsFile.canRead()) {
				if (debug) System.out.println("Using credentials file: " + credsFile.getPath());
				try {
					List<String> lines = Files.readAllLines(credsFile.toPath());
					if (lines.get(0) != null && username == null) {
						username = lines.get(0);
					}
					if (lines.get(1) != null && password == null) {
						password = lines.get(1);
					}
				} catch (IOException e) {
					return;
				}
			}
		}
	}
	
	public int getDay(Document schedPage) throws IOException {
		Elements matching = schedPage.body().getElementsByAttributeValueContaining("style", "border: solid 1px red;");
		Matcher m = Pattern.compile("\\d+").matcher(matching.first().text());
		
		if (m.find()) {
			String thing = m.group(0);
			int dayNumber = Integer.parseInt(thing);
			if (dayNumber >= 1 && dayNumber <= 7) {
				return dayNumber;
			}
		}
		return 0;
	}
	
	public String getClass(Document schedPage){
		Elements matching = schedPage.body().getElementsByAttributeValueContaining("style", "border: solid 1px red;");
		try {
			return matching.get(1).text();
		} catch (IndexOutOfBoundsException e){
			return "No Class Currently";
		}
	}
	
	public Connection.Response schedulePage() throws IOException{
		try {
			return Jsoup.connect("https://ma-melrose.myfollett.com/aspen/studentScheduleContextList.do?navkey=myInfo.sch.list")
							.userAgent(agent)
							.referrer("https://ma-melrose.myfollett.com/aspen/portalStudentDetail.do?navkey=myInfo.details.detail")
							.timeout(10 * 1000)
							.cookies(demCookies)
							.followRedirects(true)
							.execute();
		} catch (HttpStatusException e){
			System.out.println("Login details incorrect, or Aspen is having issues, please try again later!");
			if (debug) e.printStackTrace();
		}
		System.out.println("You shouldn't be here...");
		return null;
	}
	
	public Connection.Response loginResponse() throws IOException {
		String loginUrl = "https://ma-melrose.myfollett.com/aspen/logon.do";
			Connection.Response loginPageResponse =
							Jsoup.connect(loginUrl)
											.userAgent(agent)
											.timeout(10 * 1000)
											.followRedirects(true)
											.execute();
			
			if (debug) System.out.println("Fetched login page");

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
			
			if (debug) System.out.println("HTTP Status Code: " + responsePostLogin.statusCode());
			Map<String, String> mapLoggedInCookies = responsePostLogin.cookies();
			demCookies.putAll(mapLoggedInCookies);
			demCookies.putAll(mapLoginPageCookies);
			return responsePostLogin;
	}
}
