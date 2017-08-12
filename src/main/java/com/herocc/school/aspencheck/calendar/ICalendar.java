package com.herocc.school.aspencheck.calendar;

import com.herocc.school.aspencheck.GenericEventGenerator;
import net.fortuna.ical4j.filter.Filter;
import net.fortuna.ical4j.filter.PeriodRule;
import net.fortuna.ical4j.filter.Rule;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.CalendarComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ICalendar extends GenericEventGenerator {
  private Calendar ical;
  
  public ICalendar(Calendar ical) {
    this.ical = ical;
  }
  
  public List<Event> getEvents(boolean checkEventsOccurringNow) {
    List<Event> events = new ArrayList<>();
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
}
