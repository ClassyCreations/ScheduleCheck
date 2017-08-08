package com.herocc.school.aspencheck.calendar;

import lombok.Data;
import net.fortuna.ical4j.model.DateTime;

@Data
public class Event {
  private String title;
  private String description;
  private DateTime dateTime;
}
