package com.herocc.school.aspencheck.calendar;

import com.herocc.school.aspencheck.AspenCheck;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

import java.io.IOException;

public class CalWebFetch {
	public Connection.Response todayPage() throws IOException {
		try {
			return Jsoup.connect("https://melroseschools.com/calendar/today/?tribe_eventcategory=144")
							.execute();
		} catch (HttpStatusException e){
			if (AspenCheck.debug && !AspenCheck.quiet) e.printStackTrace();
			return null;
		}
	}
}
