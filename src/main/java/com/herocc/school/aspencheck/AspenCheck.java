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
import java.util.logging.Level;
import java.util.logging.Logger;

public class AspenCheck {
	public static final Logger log = Logger.getLogger(AspenCheck.class.getName());
	
	@Parameter(names = {"--username", "-u"}, description = "Aspen Username")
	private String username;
	
	@Parameter(names = {"--password", "-p"}, description = "Aspen password")
	private String password;
	
	@Parameter(names = {"--debug", "-d"})
	private static boolean debug = false;
	
	@Parameter(names = {"--file", "-f"})
	private String filePath = null;
	
	@Parameter(names = {"--hidePrivateData"})
	private boolean hidePrivateData = true;
	
	public static Schedule schedule;
	public static Calendar calendar;
	
	public static void main(String[] args){
		AspenCheck aspenCheck = new AspenCheck();
		new JCommander(aspenCheck, args);
		if (debug) log.setLevel(Level.FINEST);
		aspenCheck.actuallyMain();
	}
	
	private void actuallyMain(){
		try {
			getLoginDetails();
			AspenWebFetch aspenWebFetch = new AspenWebFetch();
			aspenWebFetch.login(username, password);
			schedule = new Schedule(aspenWebFetch.schedulePage().parse());
			
			CalWebFetch calWebFetch = new CalWebFetch();
			calendar = new Calendar(calWebFetch.todayPage().parse());
			
			System.out.println(jsonData().build().toString());
			
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
					log.fine("Using credentials file: " + credsFile.getPath());
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
