package com.herocc.school.aspencheck;

import com.herocc.school.aspencheck.calendar.Event;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import java.util.List;

public abstract class GenericEventGenerator {
  public abstract List<Event> getEvents(boolean checkEventsOccurringNow);
  
  public JsonArrayBuilder getJsonData() {
    JsonArrayBuilder jsonEvents = Json.createArrayBuilder();
    for (Event event : getEvents(true)) {
      jsonEvents.add(event.getJsonFormat());
    }
    return jsonEvents;
  }
}
