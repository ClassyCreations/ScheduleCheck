package com.herocc.school.aspencheck.calendar;

import com.herocc.school.aspencheck.*;
import lombok.Getter;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@CrossOrigin
@RestController
@RequestMapping("/announcements")
public class CalendarController extends GenericRestController {
  List<Event> announcements = new ArrayList<>();
  
  @Getter private List<Event> schoolEventsList = new ArrayList<>();
  @Getter private List<Event> hsAnnouncementsList = new ArrayList<>();
  
  @RequestMapping()
  public List<Event> getEvents() {
    if (AspenCheck.getUnixTime() > getNextRefreshTime()) {
      AspenCheck.log.log(Level.INFO, "Refreshing announcements, " + String.valueOf(AspenCheck.getUnixTime() + " > " + getNextRefreshTime()));
      new Thread(() -> {
        List<Event> tmp = new ArrayList<>();
        tmp.addAll(refreshSchoolCal());
        tmp.addAll(refreshHsAnnouncements());
        announcements = tmp;
        lastRefreshTimestamp = AspenCheck.getUnixTime();
      }).start();
    }
    return announcements;
  }
  
  @Cacheable("schoolCalEvents")
  @RequestMapping("district")
  public List<Event> getSchoolEvents() {
    getEvents(); // Just to refresh cache
    return getSchoolEventsList();
  }
  
  @Cacheable("hsAnnouncements")
  @RequestMapping("hs")
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
  
  @Override
  protected void refresh() {
    getEvents();
  }
}
