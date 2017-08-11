package com.herocc.school.aspencheck;

import com.herocc.school.aspencheck.calendar.Event;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CSVParse implements GenericEventGenerator {
  private String csv;
  List<Event> events = new ArrayList<>();
  
  public CSVParse(String csv) {
    this.csv = csv;
  }
  
  public List<Event> getEvents(boolean checkEventsOccurringNow) {
    try {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
      ZoneId zoneId = ZoneId.systemDefault();
      
      Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(new StringReader(csv));
      for (CSVRecord record : records) {
        Event e = new Event();
        e.setTitle(record.get(1));
        e.setDescription(record.get(2));
        
        LocalDateTime dtStart = LocalDate.parse(record.get(8), formatter).atStartOfDay();
        e.setStartTime(dtStart);
        
        LocalDateTime dtEnd = LocalDate.parse(record.get(9), formatter).atTime(23, 59, 59);
        e.setEndTime(dtEnd);
        
        if (checkEventsOccurringNow) {
          long nowEpoch = LocalDateTime.now().atZone(zoneId).toEpochSecond();
          // If the start time has occurred (start - current epoch is negative)
          // And the end time has not occurred (end - current is positive)
          if (dtStart.atZone(zoneId).toEpochSecond() - nowEpoch < 0
              && dtEnd.atZone(zoneId).toEpochSecond() - nowEpoch > 0) events.add(e);
        } else {
          events.add(e);
        }
      }
    } catch (IOException e) {
      return events;
    }
    return events;
  }
  
  public JsonArrayBuilder getJsonData() {
    JsonArrayBuilder jsonEvents = Json.createArrayBuilder();
    for (Event event : getEvents(true)) {
      JsonObjectBuilder jsonAnn = Json.createObjectBuilder();
      jsonAnn.add("title", event.getTitle()).add("description", event.getDescription());
      jsonEvents.add(jsonAnn);
    }
    return jsonEvents;
  }
}
