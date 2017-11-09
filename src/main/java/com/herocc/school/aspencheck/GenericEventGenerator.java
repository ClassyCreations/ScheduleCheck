package com.herocc.school.aspencheck;

import com.herocc.school.aspencheck.calendar.Event;

import java.util.List;

public abstract class GenericEventGenerator {
  public abstract List<Event> getEvents(boolean checkEventsOccurringNow);
}
