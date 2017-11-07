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
  
  private String buildDescString(CSVRecord record) {
    String description = "";
    
    final String shortDesc = record.get(2).trim();
    final String occurring = record.get(3);
    final String startTime = record.get(4);
    final String location  = record.get(5).trim();
    final String cost      = record.get(6);
    final String contact   = record.get(7);
    
    //if (!shortDesc.toLowerCase().startsWith("a") || !shortDesc.toLowerCase().startsWith("an")) description += "a ";
    description += shortDesc;
    
    // Location / Date / Time occurring
    description += " in ";
    if (!location.toLowerCase().startsWith("room")) description += "the ";
    description += location + " on " + occurring + " at " + startTime;
    
    // Cost
    try {
      if (cost != null && Integer.parseInt(cost) != 0) description += " costing $" + cost;
    } catch (NumberFormatException numberException) {
      AspenCheck.log.warning("Unable to parse cost " + cost);
    }
    
    // Contact person
    description += ", contact " + contact + " for more information";
    
    return description;
  }
  
  public List<Event> getEvents(boolean checkEventsOccurringNow) {
    List<Event> events = new ArrayList<>();
    try {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
      ZoneId zoneId = ZoneId.systemDefault();
      
      try {
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(new StringReader(csv));
        for (CSVRecord record : records) {
          Event e = new Event();
          boolean invalidRecord = false;
          for (int i = 0; i < 9; i++) { // Number should be number of rows starting at 0 that shouldn't be blank
            if (record.get(i) == null || record.get(i).isEmpty()) invalidRecord = true;
          }
          if (invalidRecord) continue; // If the record is missing information, skip it
          e.setTitle(record.get(1));
          e.setDescription(buildDescString(record));
    
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
      } catch (NullPointerException npe) {
        AspenCheck.log.warning("Error getting events from CSV, is it publicly readable and correctly formatted?");
        npe.printStackTrace();
      }
    } catch (IOException e) {
      return events;
    }
    return events;
  }
}
