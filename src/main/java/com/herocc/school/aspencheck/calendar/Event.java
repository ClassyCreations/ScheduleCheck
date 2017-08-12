package com.herocc.school.aspencheck.calendar;

import lombok.Data;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
public class Event {
  private String title;
  private String description;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  
  public JsonObjectBuilder getJsonFormat() {
    JsonObjectBuilder jsonAnn = Json.createObjectBuilder();
    return jsonAnn
        .add("title", this.getTitle())
        .add("description", this.getDescription())
        .add("startTime", this.getStartTime().atZone(ZoneId.systemDefault()).toEpochSecond())
        .add("endTime", this.getEndTime().atZone(ZoneId.systemDefault()).toEpochSecond());
  }
}
