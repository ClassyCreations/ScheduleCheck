package com.herocc.school.aspencheck.calendar.gcal;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;

public class GoogleCalFetch {
	private String calID;
	
	public GoogleCalFetch(String calID) {
		this.calID = calID;
	}
	
	public String getCalFile() throws IOException {
		String ical = "";
		// Thanks, Mr. Oracle!
		// https://docs.oracle.com/javase/tutorial/networking/urls/readingURL.html
		URL icalUrl = new URL("https://calendar.google.com/calendar/ical/" + calID + "/public/full.ics");
		BufferedReader in = new BufferedReader(new InputStreamReader(icalUrl.openStream()));
		String inputLine;
		while ((inputLine = in.readLine()) != null) ical += inputLine + "\n";
		
		in.close();
		return ical;
	}
	
	public Calendar getCalendar() throws IOException {
		// Yay, so many buffer reader input builder buddies!
		String icalFile = getCalFile();
		StringReader sin = new StringReader(icalFile);
		CalendarBuilder builder = new CalendarBuilder();
		try {
			return builder.build(sin);
		} catch (ParserException e) {
			System.err.println("Error parsing iCal, contants:\n" + icalFile);
			e.printStackTrace();
		}
		return null;
	}
}
