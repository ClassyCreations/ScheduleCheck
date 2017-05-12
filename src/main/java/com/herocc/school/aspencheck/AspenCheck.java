package com.herocc.school.aspencheck;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
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

public class AspenCheck {
	
	@Parameter(names = {"--username", "-u"}, description = "Aspen Username")
	private String username;
	
	@Parameter(names = {"--password", "-p"}, description = "Aspen password")
	private String password;
	
	@Parameter(names = {"--debug", "-d"})
	static boolean debug = false;
	
	@Parameter(names = {"--quiet", "-q"})
	static boolean quiet = false;
	
	@Parameter(names = {"--file", "-f"})
	private String filePath = null;
	
	@Parameter(names = {"--printJson", "-j"})
	private boolean printJson = false;
	
	@Parameter(names = {"--hidePrivateData"})
	private boolean hidePrivateData = true;
	
	Schedule schedule;
	
	public static void main(String[] args){
		AspenCheck aspenCheck = new AspenCheck();
		new JCommander(aspenCheck, args);
		aspenCheck.actuallyMain(args);
	}
	
	private void actuallyMain(String[] args){
		try {
			getLoginDetails();
			WebFetch webFetch = new WebFetch();
			webFetch.login(username, password);
			schedule = new Schedule(webFetch.schedulePage().parse());
			
			int day = schedule.day;
			String className = schedule.currentClass;
			String block = schedule.block;
			if (className == null) className = "No Class in Session!";
			
			if (quiet && !printJson) {
				System.out.println(day);
				System.exit(day);
			} else if (!printJson) {
				System.out.println("Day: " + day);
				System.out.println("Class: " + className);
				System.out.println("Block: " + block);
			} else {
				System.out.println(jsonData().build().toString());
			}
			if (filePath != null) writeJsonFile(new File(filePath).getAbsoluteFile(), jsonData());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public JsonObjectBuilder jsonData(){
		JsonObjectBuilder json = Json.createObjectBuilder();
		JsonObjectBuilder scheduleJson = schedule.getJsonData(hidePrivateData);
		json = json.add("version", 2) // Increment as JSON data changes
						.add("asOf", Instant.now().getEpochSecond())
						.add("schedule", scheduleJson); // Schedule Data
		
		return json;
	}
	
	private void writeJsonFile(File file, JsonObjectBuilder json) throws IOException{
		StringWriter stringWriter = new StringWriter();
		try (JsonWriter jw = Json.createWriter(stringWriter)){
			jw.writeObject(json.build());
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
}
