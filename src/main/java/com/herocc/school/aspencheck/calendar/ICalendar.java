package com.herocc.school.aspencheck.calendar;

import com.herocc.school.aspencheck.GenericEventGenerator;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
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
    if (ical == null) return events;
    Collection<VEvent> eventsToday = ical.getComponents(Component.VEVENT);
    
    for (VEvent announcement : eventsToday) {
      Event e = new Event();
  
      e.setTitle(announcement.getProperty(Property.SUMMARY).getValue()); // Title
      if (announcement.getProperty(Property.DESCRIPTION) != null) e.setDescription(announcement.getProperty(Property.DESCRIPTION).getValue()); // Description
      
      long eventStart = announcement.getStartDate().getDate().getTime() / 1000;
      long eventEnd = announcement.getEndDate().getDate().getTime() / 1000;
      
      e.setStartTime(LocalDateTime.ofEpochSecond(eventStart, 0, ZoneOffset.UTC));
      e.setEndTime(LocalDateTime.ofEpochSecond(eventEnd, 0, ZoneOffset.UTC));
  
      if (checkEventsOccurringNow) {
        long nowEpoch = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
        // If the start time has occurred (start - current epoch is negative)
        // And the end time has not occurred (end - current is positive)
        long diffStart = eventStart - nowEpoch;
        long diffEnd = eventEnd - nowEpoch;
        if (diffStart < 0 && diffEnd > 0) events.add(e);
      } else {
        events.add(e);
      }
    }
    return events;
  }
}
