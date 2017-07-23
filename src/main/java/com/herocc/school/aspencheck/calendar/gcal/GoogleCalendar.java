package com.herocc.school.aspencheck.calendar.gcal;

import net.fortuna.ical4j.filter.Filter;
import net.fortuna.ical4j.filter.PeriodRule;
import net.fortuna.ical4j.filter.Rule;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.CalendarComponent;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.util.Collection;

public class GoogleCalendar {
	private Calendar ical;
	
	public GoogleCalendar(Calendar ical) {
		this.ical = ical;
	}
	
	public JsonArrayBuilder getAnnouncements() {
		JsonArrayBuilder jsonEvents = Json.createArrayBuilder();
		
		/*
		The following is from https://github.com/ical4j/ical4j/wiki/Examples#filtering-events
		It is kinda gross and triggers IntelliJ, but leave it until iCal4J updates their stuff or I find another fix
		*/
		java.util.Calendar today = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("America/New_York"));
		today.set(java.util.Calendar.HOUR_OF_DAY, 0);
		today.clear(java.util.Calendar.MINUTE);
		today.clear(java.util.Calendar.SECOND);
		Period period = new Period(new DateTime(today.getTime()), new Dur(0, 0, 0, 0));
		Filter filter = new Filter(new Rule[] {new PeriodRule<>(period)}, Filter.MATCH_ALL);
		Collection eventsToday = filter.filter(ical.getComponents(Component.VEVENT));
		// End (Most) Gross stuff
		
		for (Object ann : eventsToday) {
			CalendarComponent announcement = (CalendarComponent) ann; // Eww Assumptions
			JsonObjectBuilder jsonAnn = Json.createObjectBuilder();
			jsonAnn.add("title", announcement.getProperty(Property.SUMMARY).getValue()) // Title
							.add("description", announcement.getProperty(Property.DESCRIPTION).getValue()); // Body of announcement
			jsonEvents.add(jsonAnn);
		}
		
		return jsonEvents;
	}
	
	public JsonArrayBuilder getJsonData() {
		return getAnnouncements();
	}
}
