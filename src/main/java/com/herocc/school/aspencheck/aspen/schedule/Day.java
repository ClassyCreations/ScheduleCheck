package com.herocc.school.aspencheck.aspen.schedule;

import java.util.List;

import lombok.Data;


@Data
public class Day {
  private String dayTitle;
  private int indexOfCycle;
  private List<String> blocks;
  private String currentBlock;
  private boolean isToday;
}
