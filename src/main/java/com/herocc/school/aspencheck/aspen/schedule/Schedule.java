package com.herocc.school.aspencheck.aspen.schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Schedule {
  public String day = "Z";
  public boolean classInSession = false;
  public String block = "Z";
  @Deprecated() public String advisoryBlock = "Z";
  public int blockOfDay = 0;
  public List<String> blockOrder = new ArrayList<>();
  public Map<String, List<String>> dayBlockOrder = new HashMap<>();

  @JsonIgnore List<Day> days = new ArrayList<>();

  public Schedule(List<Day> days) {
    this(days, null);
  }

  public Schedule(List<Day> days, Day currentDay) {
    if (currentDay != null) {
      day = currentDay.getDayTitle();
      block = currentDay.getCurrentBlock();
      blockOfDay = currentDay.getBlocks().indexOf(this.block) + 1;
      blockOrder = currentDay.getBlocks();
      classInSession = block != null;
    }

    for (Day d : days) {
      dayBlockOrder.put(d.getDayTitle(), d.getBlocks());
    }
  }
}
