package com.herocc.school.aspencheck.calendar.gcal;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class GoogleCalFetch {
	private String calID;
	
	public GoogleCalFetch(String calID) {
		this.calID = calID;
	}
	
	public Calendar getCalendar() throws IOException {
		try (InputStream is = new URL("https://calendar.google.com/calendar/ical/" + calID + "/public/full.ics").openStream()) {
			return new CalendarBuilder().build(is);
		} catch (ParserException e) {
			e.printStackTrace();
		}
		return null;
	}
}
