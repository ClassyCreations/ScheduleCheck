package com.herocc.school.aspencheck;

import com.herocc.school.aspencheck.aspen.schedule.AspenScheduleController;
import com.herocc.school.aspencheck.aspen.schedule.Schedule;
import com.herocc.school.aspencheck.calendar.CalendarController;
import com.herocc.school.aspencheck.calendar.Event;

import java.util.*;
import java.util.logging.Level;

import static com.herocc.school.aspencheck.GenericEventGenerator.SourceType;

public class District extends TimestampedObject {
  
  // Public Information
  public Schedule schedule;
  public ArrayList<Event> events;
  
  // Configs
  public String districtName;
  public String aspenBaseUrl;
  
  public String aspenUsername;
  public String aspenPassword;
  
  public Map<SourceType, List<String>> announcementsSources;
  
  
  public District() {
    asOf = 0;
    Timer autoRefresh = new Timer();
    new Thread(() -> {
      try {
        Thread.sleep(3000);
        checkCreds();
        autoRefresh.scheduleAtFixedRate(new TimerTask() {
          @Override
          public void run() {
            refresh();
          }
        }, 0, AspenCheck.config.refreshInterval * 1000);
      } catch (InterruptedException ignored) {}
    }).start();
    // We need to use a thread here because if we didn't, Jackson wouldn't have finished serializing the object
  }
  
  public void refresh() {
    AspenCheck.log.log(Level.INFO, "Refreshing " + districtName + "'s info, " + String.valueOf(AspenCheck.getUnixTime() + " > " + asOf));
    asOf = AspenCheck.getUnixTime();
    
    new Thread(() -> {
      Thread scheduleThread = new Thread(() -> AspenScheduleController.refreshSchedule(this));
      Thread calendarThread = new Thread(() -> CalendarController.refreshEvents(this));
      
      scheduleThread.start();
      calendarThread.start();
  
      try {
        scheduleThread.join();
        calendarThread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }).start();
  }
  
  private void checkCreds() {
    aspenUsername = AspenCheck.getEnvFromKey(aspenUsername);
    aspenPassword = AspenCheck.getEnvFromKey(aspenPassword);
    
    if (AspenCheck.isNullOrEmpty(aspenUsername) || AspenCheck.isNullOrEmpty(aspenPassword)) AspenCheck.log.warning("No aspen username or password for " + districtName);
  }
}
