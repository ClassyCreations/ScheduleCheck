package com.herocc.school.aspencheck.calendar;

import com.herocc.school.aspencheck.*;
import lombok.Getter;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@RestController
public class CalendarController extends GenericRestController {
  List<Event> events = new ArrayList<>();
  
  @Getter private List<Event> schoolEventsList = new ArrayList<>();
  @Getter private List<Event> hsAnnouncementsList = new ArrayList<>();
  
  @RequestMapping("/announcements")
  public List<Event> getEvents() {
    if (System.currentTimeMillis() / 1000 > getNextRefreshTime()) {
      AspenCheck.log.log(Level.FINE, "Refreshing announcements, ");
      new Thread(() -> {
        List<Event> tmp = new ArrayList<>();
        tmp.addAll(refreshSchoolCal());
        tmp.addAll(refreshHsAnnouncements());
        events = tmp;
        refreshTime = System.currentTimeMillis() / 1000;
      }).start();
    }
    return events;
  }
  
  @Cacheable("schoolCalEvents")
  @RequestMapping("/announcements/district")
  public List<Event> getSchoolEvents() {
    getEvents(); // Just to refresh cache
    return getSchoolEventsList();
  }
  
  @Cacheable("hsAnnouncements")
  @RequestMapping("/announcements/hs")
  public List<Event> getHsAnnouncements() {
    getEvents(); // Just to refresh cache
    return getHsAnnouncementsList();
  }
  
  
  @CacheEvict(value = "schoolCalEvents", allEntries = true)
  public List<Event> refreshSchoolCal() {
    List<Event> calEvents = new ArrayList<>();
    try {
      calEvents.addAll(new ICalendar(AspenCheck.getICal(Configs.districtCalUrl)).getEvents(true));
      schoolEventsList = calEvents;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return schoolEventsList;
  }
  
  @CacheEvict(value = "hsAnnouncements", allEntries = true)
  public List<Event> refreshHsAnnouncements() {
    List<Event> hsAnns = new ArrayList<>();
    try {
      hsAnns.addAll(new ICalendar(AspenCheck.getICal(Configs.hsCalUrl)).getEvents(true));
      hsAnns.addAll(new CSVParse(GenericWebFetch.getURL(Configs.hsFormUrl)).getEvents(true));
      hsAnnouncementsList = hsAnns;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return hsAnnouncementsList;
  }
}
