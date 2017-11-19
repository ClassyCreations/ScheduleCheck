package com.herocc.school.aspencheck.calendar;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Event {
  // For me to remember: https://github.com/FasterXML/jackson-annotations/wiki/Jackson-Annotations
  private String title;
  private String description;
  @JsonIgnore private LocalDateTime startTime;
  @JsonIgnore private LocalDateTime endTime;
}
