package com.herocc.school.aspencheck.aspen.schedule.parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.herocc.school.aspencheck.District;
import com.herocc.school.aspencheck.aspen.schedule.Day;
import com.herocc.school.aspencheck.aspen.schedule.Schedule;
import com.herocc.school.aspencheck.calendar.Event;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class ScheduleDocumentParser {
  private Document schedPage;
  private District district;

  public ScheduleDocumentParser(Document schedPage) {
    this.schedPage = schedPage;
  }

  public ScheduleDocumentParser(Document schedPage, District district) {
    this(schedPage);
    this.district = district;
  }

  public Schedule buildSchedule() {
    List<Day> days = loadAllDays();
    int currentDayIndex = getCurrentDayIndex(days) - 1;
    Day currentDay = currentDayIndex >= 0 ? days.get(currentDayIndex) : null;
    return new Schedule(days, currentDay);
  }

  private int getDaysInCycle() {
    return schedPage.body().getElementsByAttributeValueContaining("class", "inputGridHeader inputGridColumnHeader").size();
  }

  private List<Day> loadAllDays() {
    List<Day> daysSchedule = new ArrayList<>();
    for (int i = 1; i <= getDaysInCycle(); i++) {
      // The first column is not a day, it is the row key of the table
      daysSchedule.add(getDay(i));
    }
    return daysSchedule;
  }

  private int getCurrentDayIndex(List<Day> days) {
    for (Day d : days) {
      if (d.isToday()) {
        return d.getIndexOfCycle();
      }
    }

    // Check the district event calendar for anything starting with "Day <number>", if Aspen doesn't know otherwise
    if (district != null && district.events != null) {
      for (Event e : district.events) {
        try {
          if (e.getTitle().startsWith("Day ")) return Integer.parseInt(e.getTitle().replace("Day ", ""));
        } catch (NumberFormatException ignored) {}
      }
    }

    return 0;
  }

  private Day getDay(int index) {
    if (index == 0) return null;

    Day day = new Day();
    List<String> blocks = new ArrayList<>();
    day.setIndexOfCycle(index);

    Element tableHeader = schedPage.body().getElementsByAttributeValueContaining("class", "listHeader headerLabelBackground").first();
    Element dayHeader = tableHeader.children().get(index);

    day.setDayTitle(dayHeader.text());
    day.setToday(dayHeader.attr("style").contains("border: solid 1px red;"));

    for (Element tr : tableHeader.siblingElements()) {
      Element cellBlock = tr.children().get(index); // schedule[day][block]

      // Load the block of the day
      String blockText = cellBlock.getElementsByAttributeValueContaining("style", "font-weight: bold").text();
      if (!blockText.isEmpty()) blockText = blockText.split(Pattern.quote("("))[0];
      blocks.add(blockText);
      if (cellBlock.attributes().get("style").contains("border: solid 1px red;")) {
        // If this is the current block, set so
        day.setCurrentBlock(blockText);
      }
    }
    day.setBlocks(blocks);
    return day;
  }
}


