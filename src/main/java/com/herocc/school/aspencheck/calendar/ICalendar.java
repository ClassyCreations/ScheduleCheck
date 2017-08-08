package com.herocc.school.aspencheck.calendar;

import net.fortuna.ical4j.filter.Filter;
import net.fortuna.ical4j.filter.PeriodRule;
import net.fortuna.ical4j.filter.Rule;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.CalendarComponent;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ICalendar {
  private Calendar ical;
  private List<Event> events;
  
  public ICalendar(Calendar ical, boolean checkEventsOccurringNow) {
    this.ical = ical;
    this.getEvents(checkEventsOccurringNow);
  }
  
  private List<Event> getEvents(boolean checkEventsOccurringNow) {
    events = new ArrayList<>();
    Collection eventsToday = ical.getComponents(Component.VEVENT);
    
    if (checkEventsOccurringNow) {
		  /*
		  The following is from https://github.com/ical4j/ical4j/wiki/Examples#filtering-events
		  It is kinda gross and triggers IntelliJ, but leave it until iCal4J updates their stuff or I find another fix
		  */
      java.util.Calendar now = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("America/New_York"));
      Period period = new Period(new DateTime(now.getTime()), new Dur(0, 0, 0, 0));
      Filter filter = new Filter(new Rule[]{new PeriodRule<>(period)}, Filter.MATCH_ALL);
      eventsToday = filter.filter(ical.getComponents(Component.VEVENT));
      // End (Most) Gross stuff
    }
    
    for (Object ann : eventsToday) {
      CalendarComponent announcement = (CalendarComponent) ann; // Eww Assumptions
      Event event = new Event();
      event.setTitle(announcement.getProperty(Property.SUMMARY).getValue()); // Title
      event.setDescription(announcement.getProperty(Property.DESCRIPTION).getValue()); // Description
      events.add(event);
    }
    return events;
  }
  
  public JsonArrayBuilder getJsonData() {
    JsonArrayBuilder jsonEvents = Json.createArrayBuilder();
    for (Event event : events) {
      JsonObjectBuilder jsonAnn = Json.createObjectBuilder();
      jsonAnn.add("title", event.getTitle()).add("description", event.getDescription());
      jsonEvents.add(jsonAnn);
    }
    return jsonEvents;
  }
}
