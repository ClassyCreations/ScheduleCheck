package com.herocc.school.schedulecheck;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScheduleCheck {
	private String agent = "ScheduleGrab Bot";
	private Map<String, String> demCookies = new HashMap<>();
	
	@Parameter(names = {"--username", "-u"}, description = "Aspen Username")
	private String username;
	
	@Parameter(names = {"--password", "-p"}, description = "Aspen password")
	private String password;
	
	@Parameter(names = {"--debug", "-d"})
	private boolean debug = false;
	
	@Parameter(names = {"--quiet", "-q"})
	private boolean quiet = false;
	
	@Parameter(names = {"--file", "-f"})
	private String filePath = null;
	
	@Parameter(names = {"--printJson", "-j"})
	private boolean printJson = false;
	
	@Parameter(names = {"--hidePrivateData"})
	private boolean hidePrivateData = true;
	
	private int day;
	private String className;
	private String block;
  
	public static void main(String[] args){
		ScheduleCheck scheduleCheck = new ScheduleCheck();
		new JCommander(scheduleCheck, args);
		scheduleCheck.actuallyMain(args);
	}
	
	private void actuallyMain(String[] args){
		try {
			getLoginDetails();
			loginResponse(username, password);
			Document schedPage = schedulePage().parse();
			
			day = getDay(schedPage);
			className = getClass(schedPage);
			block = getBlock(schedPage);
			if (block == null) block = "Z";
			if (className == null) className = "No Class in Session!";
			
			if (quiet && !printJson) {
				System.out.println(day);
				System.exit(day);
			} else if (!printJson) {
				System.out.println("Day: " + day);
				System.out.println("Class: " + className);
				if (block != null) System.out.println("Block: " + block);
			}
			if (filePath != null) writeJsonFile(new File(filePath).getAbsoluteFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public JsonObject getJsonData(){
		String localClassName = hidePrivateData ? "null" : className;
		
		return Json.createObjectBuilder()
						.add("version", 1) // Increment as JSON data changes
						.add("day", day)
						.add("block", block)
						.add("asOf", Instant.now().getEpochSecond())
						.add("class", localClassName)
						.build();
	}
	
	private void writeJsonFile(File file) throws IOException{
		JsonObject json = getJsonData();
		
		StringWriter stringWriter = new StringWriter();
		try (JsonWriter jw = Json.createWriter(stringWriter)){
			jw.writeObject(json);
		}
		String jData = stringWriter.toString();
		
		if (printJson) System.out.println(jData);
		
		file.delete();
		file.createNewFile();
		
		FileWriter fw = new FileWriter(file);
		fw.write(jData);
		fw.flush();
		fw.close();
	}
	
	/**
	 * Gets the login Username and Password
	 * Priority: Params, Env, File, Testing
	 * @throws IOException Couldn't read file
	 */
	private void getLoginDetails() throws IOException{
		if (username == null || password == null) {
			if (System.getenv("ASPEN_UNAME") != null && System.getenv("ASPEN_PASS") != null) {
				username = System.getenv("ASPEN_UNAME");
				password = System.getenv("ASPEN_PASS");
			} else {
				// File Check
				File credsFile = new File(System.getProperty("user.dir") + "creds.txt");
				if (credsFile.canRead()) {
					if (debug) System.out.println("Using credentials file: " + credsFile.getPath());
					List<String> lines = Files.readAllLines(credsFile.toPath());
					if (lines.get(0) != null && username == null) {
						username = lines.get(0);
					}
					if (lines.get(1) != null && password == null) {
						password = lines.get(1);
					}
				}
			}
		}
	}
	
	public int getDay(Document schedPage) throws IOException {
		Elements matching = schedPage.body().getElementsByAttributeValueContaining("style", "border: solid 1px red;");
		try {
			matching.first().text();
		} catch (NullPointerException e) {
			return 0;
		}
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
			return null;
		}
	}
	
	public String getBlock(Document schedPage){
		String currentClass = getClass(schedPage);
		if (currentClass == null) return null;
		Matcher m = Pattern.compile("([A-Za-z])(?!.)").matcher(currentClass); // That Regex though
		if (m.find()){
			return m.group(0);
		} else {
			if (!quiet && debug) System.out.println("No block found!");
			return null;
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
			return null;
		}
	}
	
	public Connection.Response loginResponse(String username, String password) throws IOException {
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
