package com.herocc.school.aspencheck.calendar;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Event {
  private String title;
  private String description;
  @JsonIgnore private LocalDateTime startTime;
  @JsonIgnore private LocalDateTime endTime;
}
