package com.herocc.school.aspencheck.calendar;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Event {
  private String title;
  private String description;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
}
