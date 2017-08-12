package com.herocc.school.aspencheck;

import com.herocc.school.aspencheck.calendar.Event;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class CSVParse extends GenericEventGenerator {
  private String csv;
  
  public CSVParse(String csv) {
    this.csv = csv;
  }
  
  public List<Event> getEvents(boolean checkEventsOccurringNow) {
    List<Event> events = new ArrayList<>();
    try {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
      ZoneId zoneId = ZoneId.systemDefault();
      
      Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(new StringReader(csv));
      for (CSVRecord record : records) {
        Event e = new Event();
        e.setTitle(record.get(1));
        e.setDescription(record.get(2));
        
        LocalDateTime dtStart = LocalDateTime.MIN;
        LocalDateTime dtEnd = LocalDateTime.MIN;
        
        try {
          dtStart = LocalDate.parse(record.get(8), formatter).atStartOfDay();
          dtEnd = LocalDate.parse(record.get(9), formatter).atTime(23, 59, 59);
        } catch (DateTimeParseException dte) {
          AspenCheck.log.warning("Event " + e.getTitle() + " has invalid date: " + dte.getParsedString());
          dte.printStackTrace();
        }
  
        e.setStartTime(dtStart);
        e.setEndTime(dtEnd);
        
        if (checkEventsOccurringNow) {
          long nowEpoch = LocalDateTime.now().atZone(zoneId).toEpochSecond();
  
          // If the start time has occurred (start - current epoch is negative)
          // And the end time has not occurred (end - current is positive)
          long diffStart = dtStart.atZone(zoneId).toEpochSecond() - nowEpoch;
          long diffEnd = dtEnd.atZone(zoneId).toEpochSecond() - nowEpoch;
          
          if (diffStart < 0 && diffEnd > 0) events.add(e);
        } else {
          events.add(e);
        }
      }
    } catch (IOException e) {
      return events;
    }
    return events;
  }
}
