package com.herocc.school.aspencheck.calendar.webcal;

import com.herocc.school.aspencheck.AspenCheck;
import com.herocc.school.aspencheck.GenericWebFetch;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;

import java.io.IOException;

public class CalWebFetch extends GenericWebFetch {
	public Connection.Response todayPage() throws IOException {
		try {
			return getPage("https://melroseschools.com/calendar/today/?tribe_eventcategory=149");
		} catch (HttpStatusException e){
			AspenCheck.log.fine(e.getMessage());
			return null;
		}
	}
}
