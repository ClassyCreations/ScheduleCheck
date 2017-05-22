package com.herocc.school.aspencheck;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.herocc.school.aspencheck.aspen.AspenWebFetch;
import com.herocc.school.aspencheck.aspen.Schedule;
import com.herocc.school.aspencheck.calendar.CalWebFetch;
import com.herocc.school.aspencheck.calendar.Calendar;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.time.Instant;
import java.util.List;

public class AspenCheck {
	
	@Parameter(names = {"--username", "-u"}, description = "Aspen Username")
	private String username;
	
	@Parameter(names = {"--password", "-p"}, description = "Aspen password")
	private String password;
	
	@Parameter(names = {"--debug", "-d"})
	public static boolean debug = false;
	
	@Parameter(names = {"--quiet", "-q"})
	public static boolean quiet = false;
	
	@Parameter(names = {"--file", "-f"})
	private String filePath = null;
	
	@Parameter(names = {"--printJson", "-j"})
	private boolean printJson = false;
	
	@Parameter(names = {"--hidePrivateData"})
	private boolean hidePrivateData = true;
	
	Schedule schedule;
	Calendar calendar;
	
	public static int day;
	public static String className;
	public static String block;
	
	public static void main(String[] args){
		AspenCheck aspenCheck = new AspenCheck();
		new JCommander(aspenCheck, args);
		aspenCheck.actuallyMain(args);
	}
	
	private void actuallyMain(String[] args){
		try {
			getLoginDetails();
			AspenWebFetch aspenWebFetch = new AspenWebFetch();
			aspenWebFetch.login(username, password);
			schedule = new Schedule(aspenWebFetch.schedulePage().parse());
			day = schedule.day;
			className = schedule.currentClass;
			block = schedule.block;
			
			CalWebFetch calWebFetch = new CalWebFetch();
			calendar = new Calendar(calWebFetch.todayPage().parse());
			
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

		json = json.add("version", 2) // Increment as JSON data changes
						.add("asOf", Instant.now().getEpochSecond())
						.add("schedule", schedule.getJsonData(hidePrivateData)) // Schedule Data
						.add("calendar", calendar.getJsonData()); // Calendar Data
		
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
