package com.herocc.school.aspencheck;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.herocc.school.aspencheck.aspen.AspenWebFetch;
import com.herocc.school.aspencheck.aspen.Schedule;
import com.herocc.school.aspencheck.calendar.ICalendar;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;

import javax.json.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
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
  
  private String calendarURL = "http://melroseschools.com/calendar/today/?ical=1&tribe_display=day&tribe_eventcategory=149";
  private String announcementsURL = "https://calendar.google.com/calendar/ical/melroseschools.com_0iitdti0rfgbgc4un9vf8520bc@group.calendar.google.com/public/full.ics";
	
	public static Schedule schedule;
	public static ICalendar calendar;
	public static ICalendar hsAnnouncements;
	
	public static void main(String[] args) {
		AspenCheck aspenCheck = new AspenCheck();
		new JCommander(aspenCheck, args);
		if (debug) log.setLevel(Level.FINEST);
		aspenCheck.actuallyMain();
	}
	
	private void actuallyMain() {
		try {
			getLoginDetails();
			AspenWebFetch aspenWebFetch = new AspenWebFetch();
			if (aspenWebFetch.login(username, password) != null) schedule = new Schedule(aspenWebFetch.schedulePage().parse());
   
			calendar = new ICalendar(getICal(calendarURL));
			hsAnnouncements = new ICalendar(getICal(announcementsURL));
			
			System.out.println(jsonData().build().toString());
			
			if (filePath != null) writeJsonFile(new File(filePath).getAbsoluteFile(), jsonData());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public JsonObjectBuilder jsonData() {
		JsonObjectBuilder json = Json.createObjectBuilder();
		
		// Return empty instead of null if object doesn't exist
		JsonObjectBuilder s = schedule != null ? schedule.getJsonData(hidePrivateData) : Json.createObjectBuilder();

		CSVParse hsSheet = new CSVParse(GenericWebFetch.getURL("https://docs.google.com/spreadsheet/ccc?key=1C_Rmk0act0Q8VHdjeh0TAsmfbWtvK_P9z25U-7BJW78&output=csv"));
		JsonArrayBuilder hsA = mergeJsonArrays(hsAnnouncements.getJsonData(), hsSheet.getJsonData());
		json = json.add("version", 2) // Increment as JSON data changes
						.add("asOf", Instant.now().getEpochSecond()) // Current time
						.add("schedule", s) // Schedule Data
						.add("calendar", calendar.getJsonData()) // Calendar Data
						.add("announcements", Json.createObjectBuilder() // Announcements from GCal / CSV
										.add("hs", hsA) // High School Announcements
										// Possibly separate GCal later for Middle / Other schools?
						);
		
		return json;
	}
  
  private JsonArrayBuilder mergeJsonArrays(JsonArrayBuilder arr1, JsonArrayBuilder arr2) {
    JsonArrayBuilder arr = Json.createArrayBuilder();
    
    for (JsonValue j : arr1.build()) {
      arr.add(j);
    }
    for (JsonValue j : arr2.build()) {
      arr.add(j);
    }
    
    return arr;
  }
	
	private void writeJsonFile(File file, JsonObjectBuilder json) throws IOException {
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
	private void getLoginDetails() throws IOException {
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
  
  public Calendar getICal(String url) throws IOException {
    URLConnection c = new URL(url).openConnection();
    c.setRequestProperty("User-Agent", GenericWebFetch.agent);
    
    try (InputStream is = c.getInputStream()) {
      return new CalendarBuilder().build(is);
    } catch (ParserException e) {
      e.printStackTrace();
    }
    return null;
  }
}
